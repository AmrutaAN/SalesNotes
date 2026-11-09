import { Component, OnInit, inject } from '@angular/core';
import { SalesNoteService } from '../../services/sales-note.service';
import { SalesNote, NoteStatus, Priority } from '../../models/note.model';

@Component({
  template: '',
  standalone: true
})
export class CommonNoteListComponent implements OnInit {
  protected noteService = inject(SalesNoteService);

  notes: SalesNote[] = [];
  filteredNotes: SalesNote[] = [];
  loading = false;
  errorMessage: string | null = null;

  searchTerm = '';
  selectedStatus: NoteStatus | 'ALL' = 'ALL';
  selectedPriority: Priority | 'ALL' = 'ALL';

  ngOnInit(): void {
    this.loadNotes();
  }

  loadNotes(): void {
    this.loading = true;
    this.errorMessage = null;

    const statusParam = this.selectedStatus === 'ALL' ? undefined : this.selectedStatus;
    const priorityParam = this.selectedPriority === 'ALL' ? undefined : this.selectedPriority;

    this.noteService.getNotes(this.searchTerm, statusParam, priorityParam).subscribe({
      next: (data) => {
        this.notes = data;
        this.applyFilter();
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading notes:', err);
        this.errorMessage = 'Could not connect to SalesNotes backend. Please check backend is running at http://localhost:8080';
        this.loading = false;
      }
    });
  }

  applyFilter(): void {
    let result = [...this.notes];
    if (this.searchTerm && this.searchTerm.trim() !== '') {
      const term = this.searchTerm.toLowerCase().trim();
      result = result.filter(n =>
        n.title.toLowerCase().includes(term) ||
        (n.content && n.content.toLowerCase().includes(term)) ||
        (n.customer && n.customer.name.toLowerCase().includes(term)) ||
        (n.customer && n.customer.company.toLowerCase().includes(term)) ||
        (n.tags && n.tags.toLowerCase().includes(term))
      );
    }
    if (this.selectedStatus !== 'ALL') {
      result = result.filter(n => n.status === this.selectedStatus);
    }
    if (this.selectedPriority !== 'ALL') {
      result = result.filter(n => n.priority === this.selectedPriority);
    }
    this.filteredNotes = result;
  }

  onSearchChange(term: string): void {
    this.searchTerm = term;
    this.applyFilter();
  }

  onStatusFilter(status: NoteStatus | 'ALL'): void {
    this.selectedStatus = status;
    this.applyFilter();
  }

  onPriorityFilter(priority: Priority | 'ALL'): void {
    this.selectedPriority = priority;
    this.applyFilter();
  }

  deleteNote(id: number): void {
    if (confirm('Are you sure you want to delete this sales note?')) {
      this.noteService.deleteNote(id).subscribe({
        next: () => {
          this.notes = this.notes.filter(n => n.id !== id);
          this.applyFilter();
        },
        error: (err) => {
          console.error('Error deleting note:', err);
          this.errorMessage = 'Failed to delete note.';
        }
      });
    }
  }

  updateStatus(note: SalesNote, newStatus: NoteStatus): void {
    if (!note.id) return;
    this.noteService.updateNoteStatus(note.id, newStatus).subscribe({
      next: (updated) => {
        const index = this.notes.findIndex(n => n.id === note.id);
        if (index > -1) {
          this.notes[index] = updated;
          this.applyFilter();
        }
      },
      error: (err) => {
        console.error('Error updating status:', err);
      }
    });
  }

  getPriorityBadgeClass(priority: Priority): string {
    switch (priority) {
      case 'URGENT': return 'badge-urgent';
      case 'HIGH': return 'badge-high';
      case 'MEDIUM': return 'badge-medium';
      case 'LOW': return 'badge-low';
      default: return '';
    }
  }

  getStatusBadgeClass(status: NoteStatus): string {
    switch (status) {
      case 'WON': return 'badge-won';
      case 'LOST': return 'badge-lost';
      case 'IN_PROGRESS': return 'badge-progress';
      case 'FOLLOW_UP_REQUIRED': return 'badge-followup';
      case 'CLOSED': return 'badge-closed';
      case 'OPEN': default: return 'badge-open';
    }
  }
}
