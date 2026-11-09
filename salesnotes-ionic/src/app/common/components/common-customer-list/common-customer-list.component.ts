import { Component, OnInit, inject } from '@angular/core';
import { CustomerService } from '../../services/customer.service';
import { Customer } from '../../models/note.model';

@Component({
  template: '',
  standalone: true
})
export class CommonCustomerListComponent implements OnInit {
  protected customerService = inject(CustomerService);

  customers: Customer[] = [];
  loading = false;
  errorMessage: string | null = null;
  searchTerm = '';

  ngOnInit(): void {
    this.loadCustomers();
  }

  loadCustomers(): void {
    this.loading = true;
    this.errorMessage = null;

    this.customerService.getCustomers(this.searchTerm).subscribe({
      next: (data) => {
        this.customers = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Failed to load customers:', err);
        this.errorMessage = 'Could not load customers from backend.';
        this.loading = false;
      }
    });
  }

  onSearchChange(term: string): void {
    this.searchTerm = term;
    this.loadCustomers();
  }

  deleteCustomer(id: number): void {
    if (confirm('Delete this customer? All associated notes will also be removed.')) {
      this.customerService.deleteCustomer(id).subscribe({
        next: () => {
          this.customers = this.customers.filter(c => c.id !== id);
        },
        error: (err) => {
          console.error('Failed to delete customer:', err);
          this.errorMessage = 'Failed to delete customer.';
        }
      });
    }
  }
}
