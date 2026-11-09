import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { SalesNoteService } from '../../services/sales-note.service';
import { CustomerService } from '../../services/customer.service';
import { Customer, SalesNote, SalesNoteRequest, Priority, NoteStatus } from '../../models/note.model';

@Component({
  template: '',
  standalone: true
})
export class CommonNoteFormComponent implements OnInit {
  protected fb = inject(FormBuilder);
  protected route = inject(ActivatedRoute);
  protected router = inject(Router);
  protected noteService = inject(SalesNoteService);
  protected customerService = inject(CustomerService);

  noteForm!: FormGroup;
  isEditMode = false;
  noteId: number | null = null;
  customers: Customer[] = [];
  loading = false;
  submitting = false;
  errorMessage: string | null = null;

  priorities: Priority[] = ['LOW', 'MEDIUM', 'HIGH', 'URGENT'];
  statuses: NoteStatus[] = ['OPEN', 'IN_PROGRESS', 'FOLLOW_UP_REQUIRED', 'WON', 'LOST', 'CLOSED'];

  ngOnInit(): void {
    this.initForm();
    this.loadCustomers();

    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.isEditMode = true;
      this.noteId = Number(idParam);
      this.loadNote(this.noteId);
    }
  }

  initForm(): void {
    this.noteForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(3)]],
      content: [''],
      customerId: [null, [Validators.required]],
      dealAmount: [null, [Validators.min(0)]],
      priority: ['MEDIUM', [Validators.required]],
      status: ['OPEN', [Validators.required]],
      followUpDate: [''],
      tags: ['']
    });
  }

  loadCustomers(): void {
    this.customerService.getCustomers().subscribe({
      next: (data) => {
        this.customers = data;
        if (!this.isEditMode && this.customers.length > 0 && !this.noteForm.get('customerId')?.value) {
          this.noteForm.patchValue({ customerId: this.customers[0].id });
        }
      },
      error: (err) => console.error('Error fetching customers:', err)
    });
  }

  loadNote(id: number): void {
    this.loading = true;
    this.noteService.getNoteById(id).subscribe({
      next: (note: SalesNote) => {
        this.noteForm.patchValue({
          title: note.title,
          content: note.content,
          customerId: note.customer?.id,
          dealAmount: note.dealAmount,
          priority: note.priority,
          status: note.status,
          followUpDate: note.followUpDate,
          tags: note.tags
        });
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading note:', err);
        this.errorMessage = 'Could not load note for editing.';
        this.loading = false;
      }
    });
  }

  submit(): void {
    if (this.noteForm.invalid) {
      this.noteForm.markAllAsTouched();
      return;
    }

    this.submitting = true;
    this.errorMessage = null;

    const formValue = this.noteForm.value;
    const request: SalesNoteRequest = {
      title: formValue.title,
      content: formValue.content,
      customerId: Number(formValue.customerId),
      dealAmount: formValue.dealAmount ? Number(formValue.dealAmount) : undefined,
      priority: formValue.priority,
      status: formValue.status,
      followUpDate: formValue.followUpDate || undefined,
      tags: formValue.tags || undefined
    };

    if (this.isEditMode && this.noteId) {
      this.noteService.updateNote(this.noteId, request).subscribe({
        next: () => {
          this.submitting = false;
          this.router.navigate(['/notes', this.noteId]);
        },
        error: (err) => {
          console.error('Error saving note:', err);
          this.errorMessage = 'Failed to update note.';
          this.submitting = false;
        }
      });
    } else {
      this.noteService.createNote(request).subscribe({
        next: (created) => {
          this.submitting = false;
          this.router.navigate(['/notes']);
        },
        error: (err) => {
          console.error('Error creating note:', err);
          this.errorMessage = 'Failed to create note.';
          this.submitting = false;
        }
      });
    }
  }

  cancel(): void {
    if (this.isEditMode && this.noteId) {
      this.router.navigate(['/notes', this.noteId]);
    } else {
      this.router.navigate(['/notes']);
    }
  }
}
