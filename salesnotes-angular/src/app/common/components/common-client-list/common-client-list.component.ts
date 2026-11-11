import { Component, OnInit, inject } from '@angular/core';
import { ClientNoteService } from '../../services/client-note.service';
import { ClientProfile } from '../../models/salesnotes.model';

@Component({
  template: '',
  standalone: true
})
export class CommonClientListComponent implements OnInit {
  protected clientService = inject(ClientNoteService);

  clients: ClientProfile[] = [];
  filteredClients: ClientProfile[] = [];
  loading = false;
  errorMessage: string | null = null;

  searchTerm = '';
  priorityOnly = false;

  ngOnInit(): void {
    this.loadClients();
  }

  loadClients(): void {
    this.loading = true;
    this.errorMessage = null;

    this.clientService.getClients(this.searchTerm, this.priorityOnly ? true : undefined).subscribe({
      next: (res) => {
        this.clients = res.content || [];
        this.applyFilter();
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading clients:', err);
        this.errorMessage = 'Could not load clients from server. Ensure backend is running.';
        this.loading = false;
      }
    });
  }

  applyFilter(): void {
    let result = [...this.clients];
    if (this.searchTerm && this.searchTerm.trim() !== '') {
      const term = this.searchTerm.toLowerCase().trim();
      result = result.filter(c =>
        c.name.toLowerCase().includes(term) ||
        (c.company && c.company.toLowerCase().includes(term)) ||
        (c.industryTag && c.industryTag.toLowerCase().includes(term))
      );
    }
    if (this.priorityOnly) {
      result = result.filter(c => c.priorityFlag === true);
    }
    this.filteredClients = result;
  }

  onSearchChange(term: string): void {
    this.searchTerm = term;
    this.applyFilter();
  }

  togglePriorityFilter(): void {
    this.priorityOnly = !this.priorityOnly;
    this.applyFilter();
  }
}
