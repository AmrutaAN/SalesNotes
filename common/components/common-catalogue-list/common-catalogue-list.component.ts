import { Component, OnInit, inject } from '@angular/core';
import { CatalogueClientService } from '../../services/catalogue-client.service';
import { CatalogueProduct, PriceQuoteResult } from '../../models/salesnotes.model';

@Component({
  template: '',
  standalone: true
})
export class CommonCatalogueListComponent implements OnInit {
  protected catalogueService = inject(CatalogueClientService);

  items: CatalogueProduct[] = [];
  filteredItems: CatalogueProduct[] = [];
  categories: string[] = [];
  selectedCategory = 'ALL';
  searchTerm = '';
  loading = false;
  errorMessage: string | null = null;

  // Quote Calculator State
  selectedItemForQuote: CatalogueProduct | null = null;
  quoteQuantity = 1;
  calculatingQuote = false;
  quoteResult: PriceQuoteResult | null = null;

  ngOnInit(): void {
    this.loadCatalogue();
  }

  loadCatalogue(): void {
    this.loading = true;
    this.errorMessage = null;

    this.catalogueService.getItems(this.searchTerm, this.selectedCategory === 'ALL' ? undefined : this.selectedCategory).subscribe({
      next: (res) => {
        this.items = res.content || [];
        this.filteredItems = this.items;
        const cats = new Set<string>();
        this.items.forEach(i => {
          if (i.category) cats.add(i.category);
        });
        this.categories = Array.from(cats);
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading catalogue:', err);
        this.errorMessage = 'Could not load catalogue products.';
        this.loading = false;
      }
    });
  }

  applyFilter(): void {
    let result = [...this.items];
    if (this.searchTerm && this.searchTerm.trim()) {
      const term = this.searchTerm.toLowerCase().trim();
      result = result.filter(i =>
        i.name.toLowerCase().includes(term) ||
        i.sku.toLowerCase().includes(term) ||
        i.category.toLowerCase().includes(term)
      );
    }
    if (this.selectedCategory !== 'ALL') {
      result = result.filter(i => i.category === this.selectedCategory);
    }
    this.filteredItems = result;
  }

  onSearchChange(term: string): void {
    this.searchTerm = term;
    this.applyFilter();
  }

  selectCategory(category: string): void {
    this.selectedCategory = category;
    this.applyFilter();
  }

  openQuoteCalculator(item: CatalogueProduct): void {
    this.selectedItemForQuote = item;
    this.quoteQuantity = 1;
    this.quoteResult = null;
    this.calculateQuote();
  }

  calculateQuote(): void {
    if (!this.selectedItemForQuote || this.quoteQuantity < 1) return;
    this.calculatingQuote = true;
    this.catalogueService.calculateQuote({
      itemId: this.selectedItemForQuote.id,
      quantity: this.quoteQuantity
    }).subscribe({
      next: (res) => {
        this.quoteResult = res;
        this.calculatingQuote = false;
      },
      error: (err) => {
        console.error('Error calculating quote:', err);
        this.calculatingQuote = false;
      }
    });
  }
}
