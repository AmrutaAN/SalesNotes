import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  ClientProfile,
  ClientSummary,
  TagInfo,
  LogEntryItem
} from '../models/salesnotes.model';

@Injectable({
  providedIn: 'root'
})
export class ClientNoteService {
  private http = inject(HttpClient);
  private baseUrl = 'http://localhost:8080/api/v1';

  getClients(search?: string, priority?: boolean, page = 0, size = 50): Observable<{ content: ClientProfile[]; totalElements: number }> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (search && search.trim()) {
      params = params.set('search', search.trim());
    }
    if (priority !== undefined && priority !== null) {
      params = params.set('priority', priority.toString());
    }

    return this.http.get<{ content: ClientProfile[]; totalElements: number }>(`${this.baseUrl}/clients`, { params });
  }

  getClientById(id: string): Observable<ClientProfile> {
    return this.http.get<ClientProfile>(`${this.baseUrl}/clients/${id}`);
  }

  getClientSummary(id: string): Observable<ClientSummary> {
    return this.http.get<ClientSummary>(`${this.baseUrl}/clients/${id}/summary`);
  }

  getTags(): Observable<TagInfo[]> {
    return this.http.get<TagInfo[]>(`${this.baseUrl}/tags`);
  }

  getEntries(
    clientNoteId: string,
    tagId?: string,
    priority?: boolean,
    search?: string,
    page = 0,
    size = 50
  ): Observable<{ content: LogEntryItem[]; totalElements: number }> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (tagId) {
      params = params.set('tagId', tagId);
    }
    if (priority !== undefined && priority !== null) {
      params = params.set('priority', priority.toString());
    }
    if (search && search.trim()) {
      params = params.set('search', search.trim());
    }

    return this.http.get<{ content: LogEntryItem[]; totalElements: number }>(
      `${this.baseUrl}/notes/${clientNoteId}/entries`,
      { params }
    );
  }

  createEntry(clientNoteId: string, entry: Partial<LogEntryItem> & { tagId?: string }): Observable<LogEntryItem> {
    return this.http.post<LogEntryItem>(`${this.baseUrl}/notes/${clientNoteId}/entries`, entry);
  }

  updateTag(entryId: string, tagId: string | null): Observable<LogEntryItem> {
    return this.http.patch<LogEntryItem>(`${this.baseUrl}/entries/${entryId}/tag`, { tagId });
  }

  updatePriority(entryId: string, priority: boolean): Observable<LogEntryItem> {
    return this.http.patch<LogEntryItem>(`${this.baseUrl}/entries/${entryId}/priority`, { priority });
  }

  deleteEntry(entryId: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/entries/${entryId}`);
  }
}
