import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  ClipboardSessionData,
  ExportRecordItem,
  ExportTargetModule
} from '../models/salesnotes.model';

@Injectable({
  providedIn: 'root'
})
export class ClipboardClientService {
  private http = inject(HttpClient);
  private baseUrl = 'http://localhost:8080/api/v1/clipboard';

  getClipboard(): Observable<ClipboardSessionData> {
    return this.http.get<ClipboardSessionData>(this.baseUrl);
  }

  stageEntry(logEntryId: string): Observable<ClipboardSessionData> {
    return this.http.post<ClipboardSessionData>(`${this.baseUrl}/stage`, { logEntryId });
  }

  bulkStage(logEntryIds: string[]): Observable<ClipboardSessionData> {
    return this.http.post<ClipboardSessionData>(`${this.baseUrl}/bulk-stage`, { logEntryIds });
  }

  unstageEntry(id: string): Observable<ClipboardSessionData> {
    return this.http.delete<ClipboardSessionData>(`${this.baseUrl}/entries/${id}`);
  }

  unstageByLogEntryId(logEntryId: string): Observable<ClipboardSessionData> {
    return this.http.delete<ClipboardSessionData>(`${this.baseUrl}/entries/by-log-entry/${logEntryId}`);
  }

  clearClipboard(): Observable<ClipboardSessionData> {
    return this.http.delete<ClipboardSessionData>(this.baseUrl);
  }

  exportClipboard(request: {
    targetModule: ExportTargetModule;
    externalRefId?: string;
    notes?: string;
    entryIds?: string[];
    clearAfterExport?: boolean;
  }): Observable<ExportRecordItem> {
    return this.http.post<ExportRecordItem>(`${this.baseUrl}/export`, request);
  }

  getExports(): Observable<ExportRecordItem[]> {
    return this.http.get<ExportRecordItem[]>(`${this.baseUrl}/exports`);
  }

  getExportById(id: string): Observable<ExportRecordItem> {
    return this.http.get<ExportRecordItem>(`${this.baseUrl}/exports/${id}`);
  }
}
