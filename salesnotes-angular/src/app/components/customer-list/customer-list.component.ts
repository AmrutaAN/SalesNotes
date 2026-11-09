import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { CommonCustomerListComponent } from '../../common/components/common-customer-list/common-customer-list.component';

@Component({
  selector: 'app-customer-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './customer-list.component.html',
  styleUrls: ['./customer-list.component.scss']
})
export class CustomerListComponent extends CommonCustomerListComponent {
  // Inherits customers, loading, errorMessage, searchTerm, loadCustomers(), onSearchChange(), deleteCustomer()
}
