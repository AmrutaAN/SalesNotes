import { Component, Input, Output, EventEmitter, inject } from '@angular/core';
import { VoiceTranscriptionClientService } from '../../services/voice-transcription-client.service';
import { VoiceTranscriptionRecord } from '../../models/salesnotes.model';

@Component({
  template: '',
  standalone: true
})
export class CommonTranscriptionComponent {
  protected transcriptionService = inject(VoiceTranscriptionClientService);

  @Input() clientNoteId = '';
  @Input() logEntryId = '';
  @Output() transcriptionConfirmed = new EventEmitter<string>();

  // Audio Recording State
  isRecording = false;
  recordingDurationSeconds = 0;
  audioBlob: Blob | null = null;
  audioUrl: string | null = null;
  private mediaRecorder: any = null;
  private timerInterval: any = null;
  private audioChunks: Blob[] = [];

  // Transcription State
  transcription: VoiceTranscriptionRecord | null = null;
  isTranscribing = false;
  isConfirming = false;
  editedTranscript = '';
  applyToNote = true;
  errorMessage: string | null = null;

  async startRecording(): Promise<void> {
    this.errorMessage = null;
    this.audioChunks = [];
    this.audioBlob = null;
    this.audioUrl = null;
    this.recordingDurationSeconds = 0;

    try {
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      this.mediaRecorder = new MediaRecorder(stream);

      this.mediaRecorder.ondataavailable = (event: any) => {
        if (event.data.size > 0) {
          this.audioChunks.push(event.data);
        }
      };

      this.mediaRecorder.onstop = () => {
        this.audioBlob = new Blob(this.audioChunks, { type: 'audio/mp4' });
        this.audioUrl = URL.createObjectURL(this.audioBlob);
        stream.getTracks().forEach(track => track.stop());
      };

      this.mediaRecorder.start();
      this.isRecording = true;

      this.timerInterval = setInterval(() => {
        this.recordingDurationSeconds++;
      }, 1000);
    } catch (err) {
      console.error('Microphone access denied or unsupported:', err);
      this.errorMessage = 'Could not access microphone for voice recording.';
    }
  }

  stopRecording(): void {
    if (this.mediaRecorder && this.isRecording) {
      this.mediaRecorder.stop();
      this.isRecording = false;
      if (this.timerInterval) {
        clearInterval(this.timerInterval);
      }
    }
  }

  uploadAndTranscribe(): void {
    if (!this.audioBlob || !this.clientNoteId || !this.logEntryId) return;

    this.isTranscribing = true;
    this.transcriptionService.uploadVoiceNote(
      this.clientNoteId,
      this.logEntryId,
      this.audioBlob,
      this.recordingDurationSeconds
    ).subscribe({
      next: (attachment) => {
        this.requestAiTranscription(attachment.id);
      },
      error: (err) => {
        console.error('Error uploading voice note:', err);
        this.errorMessage = 'Failed to upload audio recording.';
        this.isTranscribing = false;
      }
    });
  }

  requestAiTranscription(attachmentId: string): void {
    this.transcriptionService.requestTranscription(attachmentId).subscribe({
      next: (vt) => {
        this.transcription = vt;
        this.editedTranscript = vt.rawTranscript || '';
        this.isTranscribing = false;
      },
      error: (err) => {
        console.error('Error requesting transcription:', err);
        this.errorMessage = 'Voice transcription request failed.';
        this.isTranscribing = false;
      }
    });
  }

  confirmTranscript(): void {
    if (!this.transcription) return;
    this.isConfirming = true;
    this.transcriptionService.confirmTranscription(
      this.transcription.id,
      this.editedTranscript,
      this.applyToNote
    ).subscribe({
      next: (confirmed) => {
        this.transcription = confirmed;
        this.isConfirming = false;
        this.transcriptionConfirmed.emit(this.editedTranscript);
      },
      error: (err) => {
        console.error('Error confirming transcript:', err);
        this.isConfirming = false;
      }
    });
  }

  discardTranscript(): void {
    if (!this.transcription) return;
    this.transcriptionService.discardTranscription(this.transcription.id).subscribe({
      next: () => {
        this.transcription = null;
        this.audioBlob = null;
        this.audioUrl = null;
      },
      error: (err) => console.error('Error discarding transcript:', err)
    });
  }
}
