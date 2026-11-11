import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  CatalogueProduct,
  PriceQuoteRequest,
  PriceQuoteResult
} from '../models/salesnotes.model';

@Injectable({
  providedIn: 'root'
})
export class CatalogueClientService {
  private http = inject(HttpClient);
  private baseUrl = 'http://localhost:8080/api/v1/catalogue';

  getItems(search?: string, category?: string, page = 0, size = 50): Observable<{ content: CatalogueProduct[]; totalElements: number }> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (search && search.trim()) {
      params = params.set('search', search.trim());
    }
    if (category && category.trim()) {
      params = params.set('category', category.trim());
    }

    return this.http.get<{ content: CatalogueProduct[]; totalElements: number }>(this.baseUrl, { params });
  }

  getItemById(id: string): Observable<CatalogueProduct> {
    return this.http.get<CatalogueProduct>(`${this.baseUrl}/${id}`);
  }

  calculateQuote(request: PriceQuoteRequest): Observable<PriceQuoteResult> {
    return this.http.post<PriceQuoteResult>(`${this.baseUrl}/quote`, request);
  }

  getChangeLog(itemId: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/${itemId}/changelog`);
  }
}
