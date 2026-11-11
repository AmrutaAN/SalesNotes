import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  AttachmentInfo,
  VoiceTranscriptionRecord
} from '../models/salesnotes.model';

@Injectable({
  providedIn: 'root'
})
export class VoiceTranscriptionClientService {
  private http = inject(HttpClient);
  private baseUrl = 'http://localhost:8080/api/v1';

  uploadVoiceNote(
    clientNoteId: string,
    logEntryId: string,
    audioFile: File | Blob,
    durationSeconds?: number
  ): Observable<AttachmentInfo> {
    const formData = new FormData();
    formData.append('file', audioFile, 'voice_note.m4a');
    if (durationSeconds !== undefined && durationSeconds !== null) {
      formData.append('durationSeconds', durationSeconds.toString());
    }

    return this.http.post<AttachmentInfo>(
      `${this.baseUrl}/notes/${clientNoteId}/entries/${logEntryId}/audio`,
      formData
    );
  }

  requestTranscription(attachmentId: string, languageHint = 'en'): Observable<VoiceTranscriptionRecord> {
    return this.http.post<VoiceTranscriptionRecord>(`${this.baseUrl}/transcriptions/request`, {
      attachmentId,
      languageHint
    });
  }

  getTranscription(id: string): Observable<VoiceTranscriptionRecord> {
    return this.http.get<VoiceTranscriptionRecord>(`${this.baseUrl}/transcriptions/${id}`);
  }

  getByAttachmentId(attachmentId: string): Observable<VoiceTranscriptionRecord> {
    return this.http.get<VoiceTranscriptionRecord>(`${this.baseUrl}/transcriptions/by-attachment/${attachmentId}`);
  }

  confirmTranscription(id: string, finalTranscript: string, applyToNote = true): Observable<VoiceTranscriptionRecord> {
    return this.http.post<VoiceTranscriptionRecord>(`${this.baseUrl}/transcriptions/${id}/confirm`, {
      finalTranscript,
      applyToNote
    });
  }

  discardTranscription(id: string): Observable<VoiceTranscriptionRecord> {
    return this.http.post<VoiceTranscriptionRecord>(`${this.baseUrl}/transcriptions/${id}/discard`, {});
  }
}
