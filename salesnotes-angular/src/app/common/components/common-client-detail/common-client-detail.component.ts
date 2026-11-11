import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ClientNoteService } from '../../services/client-note.service';
import { ClipboardClientService } from '../../services/clipboard-client.service';
import {
  ClientProfile,
  ClientSummary,
  TagInfo,
  LogEntryItem,
  InteractionType
} from '../../models/salesnotes.model';

@Component({
  template: '',
  standalone: true
})
export class CommonClientDetailComponent implements OnInit {
  protected route = inject(ActivatedRoute);
  protected clientService = inject(ClientNoteService);
  protected clipboardService = inject(ClipboardClientService);

  clientId = '';
  client: ClientProfile | null = null;
  summary: ClientSummary | null = null;
  tags: TagInfo[] = [];
  entries: LogEntryItem[] = [];
  filteredEntries: LogEntryItem[] = [];

  loading = false;
  entriesLoading = false;
  errorMessage: string | null = null;

  selectedTagId: string | 'ALL' = 'ALL';
  priorityOnly = false;
  searchTerm = '';

  // New Note Form State
  newInteractionType: InteractionType = 'field_visit';
  newTextContent = '';
  newTagId = '';
  newPriority = false;
  submittingNote = false;

  // Staged Log Entry IDs tracking
  stagedEntryIds = new Set<string>();

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.clientId = id;
        this.loadClient();
        this.loadTags();
      }
    });
  }

  loadClient(): void {
    this.loading = true;
    this.clientService.getClientById(this.clientId).subscribe({
      next: (c) => {
        this.client = c;
        this.loading = false;
        if (c.clientNoteId) {
          this.loadEntries(c.clientNoteId);
        }
        this.loadSummary();
      },
      error: (err) => {
        console.error('Error loading client:', err);
        this.errorMessage = 'Client not found or network error.';
        this.loading = false;
      }
    });
  }

  loadSummary(): void {
    this.clientService.getClientSummary(this.clientId).subscribe({
      next: (s) => this.summary = s,
      error: (err) => console.error('Error loading summary:', err)
    });
  }

  loadTags(): void {
    this.clientService.getTags().subscribe({
      next: (t) => this.tags = t,
      error: (err) => console.error('Error loading tags:', err)
    });
  }

  loadEntries(clientNoteId: string): void {
    this.entriesLoading = true;
    const tagParam = this.selectedTagId === 'ALL' ? undefined : this.selectedTagId;
    this.clientService.getEntries(clientNoteId, tagParam, this.priorityOnly ? true : undefined, this.searchTerm).subscribe({
      next: (res) => {
        this.entries = res.content || [];
        this.filteredEntries = this.entries;
        this.entriesLoading = false;
      },
      error: (err) => {
        console.error('Error loading log entries:', err);
        this.entriesLoading = false;
      }
    });
  }

  filterByTag(tagId: string | 'ALL'): void {
    this.selectedTagId = tagId;
    if (this.client?.clientNoteId) {
      this.loadEntries(this.client.clientNoteId);
    }
  }

  togglePriorityFilter(): void {
    this.priorityOnly = !this.priorityOnly;
    if (this.client?.clientNoteId) {
      this.loadEntries(this.client.clientNoteId);
    }
  }

  submitNewEntry(): void {
    if (!this.client?.clientNoteId || !this.newTextContent.trim()) {
      return;
    }
    this.submittingNote = true;
    const payload = {
      interactionType: this.newInteractionType,
      interactionDate: new Date().toISOString().split('T')[0],
      textContent: this.newTextContent.trim(),
      tagId: this.newTagId || undefined,
      priority: this.newPriority
    };

    this.clientService.createEntry(this.client.clientNoteId, payload).subscribe({
      next: (newEntry) => {
        this.newTextContent = '';
        this.newPriority = false;
        this.newTagId = '';
        this.submittingNote = false;
        if (this.client?.clientNoteId) {
          this.loadEntries(this.client.clientNoteId);
        }
        this.loadSummary();
      },
      error: (err) => {
        console.error('Error creating entry:', err);
        this.submittingNote = false;
      }
    });
  }

  stageToClipboard(entry: LogEntryItem): void {
    this.clipboardService.stageEntry(entry.id).subscribe({
      next: () => {
        this.stagedEntryIds.add(entry.id);
      },
      error: (err) => console.error('Error staging entry:', err)
    });
  }

  unstageFromClipboard(entry: LogEntryItem): void {
    this.clipboardService.unstageByLogEntryId(entry.id).subscribe({
      next: () => {
        this.stagedEntryIds.delete(entry.id);
      },
      error: (err) => console.error('Error unstaging entry:', err)
    });
  }
}
