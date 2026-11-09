import { Component, OnInit, inject } from '@angular/core';
import { SalesNoteService } from '../../services/sales-note.service';
import { DashboardSummary } from '../../models/note.model';

@Component({
  template: '',
  standalone: true
})
export class CommonDashboardComponent implements OnInit {
  protected noteService = inject(SalesNoteService);

  summary: DashboardSummary | null = null;
  loading = false;
  errorMessage: string | null = null;

  ngOnInit(): void {
    this.loadSummary();
  }

  loadSummary(): void {
    this.loading = true;
    this.errorMessage = null;

    this.noteService.getDashboardSummary().subscribe({
      next: (data) => {
        this.summary = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Failed to load dashboard metrics:', err);
        this.errorMessage = 'Could not connect to SalesNotes backend at http://localhost:8080';
        this.loading = false;
      }
    });
  }
}
