import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SalesNote, SalesNoteRequest, DashboardSummary, NoteStatus, Priority } from '../models/note.model';

@Injectable({
  providedIn: 'root'
})
export class SalesNoteService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api';

  getNotes(search?: string, status?: NoteStatus, priority?: Priority, customerId?: number): Observable<SalesNote[]> {
    let params = new HttpParams();
    if (search && search.trim()) {
      params = params.set('search', search.trim());
    }
    if (status) {
      params = params.set('status', status);
    }
    if (priority) {
      params = params.set('priority', priority);
    }
    if (customerId) {
      params = params.set('customerId', customerId.toString());
    }
    return this.http.get<SalesNote[]>(`${this.apiUrl}/notes`, { params });
  }

  getNoteById(id: number): Observable<SalesNote> {
    return this.http.get<SalesNote>(`${this.apiUrl}/notes/${id}`);
  }

  createNote(request: SalesNoteRequest): Observable<SalesNote> {
    return this.http.post<SalesNote>(`${this.apiUrl}/notes`, request);
  }

  updateNote(id: number, request: SalesNoteRequest): Observable<SalesNote> {
    return this.http.put<SalesNote>(`${this.apiUrl}/notes/${id}`, request);
  }

  updateNoteStatus(id: number, status: NoteStatus): Observable<SalesNote> {
    return this.http.patch<SalesNote>(`${this.apiUrl}/notes/${id}/status`, { status });
  }

  deleteNote(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/notes/${id}`);
  }

  getDashboardSummary(): Observable<DashboardSummary> {
    return this.http.get<DashboardSummary>(`${this.apiUrl}/dashboard/summary`);
  }
}
