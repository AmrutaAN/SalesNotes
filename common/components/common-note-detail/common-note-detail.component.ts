import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { SalesNoteService } from '../../services/sales-note.service';
import { SalesNote, NoteStatus } from '../../models/note.model';

@Component({
  template: '',
  standalone: true
})
export class CommonNoteDetailComponent implements OnInit {
  protected route = inject(ActivatedRoute);
  protected router = inject(Router);
  protected noteService = inject(SalesNoteService);

  noteId: number | null = null;
  note: SalesNote | null = null;
  loading = false;
  errorMessage: string | null = null;

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.noteId = Number(idParam);
      this.loadNote(this.noteId);
    }
  }

  loadNote(id: number): void {
    this.loading = true;
    this.errorMessage = null;

    this.noteService.getNoteById(id).subscribe({
      next: (data) => {
        this.note = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error fetching note details:', err);
        this.errorMessage = 'Could not load sales note details.';
        this.loading = false;
      }
    });
  }

  updateStatus(newStatus: NoteStatus): void {
    if (!this.noteId) return;
    this.noteService.updateNoteStatus(this.noteId, newStatus).subscribe({
      next: (updated) => {
        this.note = updated;
      },
      error: (err) => {
        console.error('Failed to change status:', err);
      }
    });
  }

  deleteCurrentNote(): void {
    if (!this.noteId) return;
    if (confirm('Delete this note permanently?')) {
      this.noteService.deleteNote(this.noteId).subscribe({
        next: () => {
          this.navigateBack();
        },
        error: (err) => {
          console.error('Error deleting note:', err);
        }
      });
    }
  }

  navigateBack(): void {
    this.router.navigate(['/notes']);
  }
}
