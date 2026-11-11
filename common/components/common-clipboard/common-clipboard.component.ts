import { Component, OnInit, inject } from '@angular/core';
import { ClipboardClientService } from '../../services/clipboard-client.service';
import {
  ClipboardSessionData,
  ClipboardEntryItem,
  ExportRecordItem,
  ExportTargetModule
} from '../../models/salesnotes.model';

@Component({
  template: '',
  standalone: true
})
export class CommonClipboardComponent implements OnInit {
  protected clipboardService = inject(ClipboardClientService);

  session: ClipboardSessionData | null = null;
  entries: ClipboardEntryItem[] = [];
  selectedEntryIds = new Set<string>();
  loading = false;
  errorMessage: string | null = null;

  // Export State
  targetModule: ExportTargetModule = 'sales_order';
  externalRefId = '';
  exportNotes = '';
  clearAfterExport = true;
  exporting = false;
  lastExportResult: ExportRecordItem | null = null;

  // History State
  exports: ExportRecordItem[] = [];
  viewingHistory = false;

  ngOnInit(): void {
    this.loadClipboard();
  }

  loadClipboard(): void {
    this.loading = true;
    this.errorMessage = null;
    this.clipboardService.getClipboard().subscribe({
      next: (s) => {
        this.session = s;
        this.entries = s.entries || [];
        this.selectedEntryIds = new Set(this.entries.map(e => e.id));
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading clipboard:', err);
        this.errorMessage = 'Could not load clipboard session.';
        this.loading = false;
      }
    });
  }

  toggleSelectEntry(id: string): void {
    if (this.selectedEntryIds.has(id)) {
      this.selectedEntryIds.delete(id);
    } else {
      this.selectedEntryIds.add(id);
    }
  }

  selectAll(): void {
    this.selectedEntryIds = new Set(this.entries.map(e => e.id));
  }

  deselectAll(): void {
    this.selectedEntryIds.clear();
  }

  unstageEntry(id: string): void {
    this.clipboardService.unstageEntry(id).subscribe({
      next: (s) => {
        this.session = s;
        this.entries = s.entries || [];
        this.selectedEntryIds.delete(id);
      },
      error: (err) => console.error('Error unstaging entry:', err)
    });
  }

  clearAll(): void {
    this.clipboardService.clearClipboard().subscribe({
      next: (s) => {
        this.session = s;
        this.entries = [];
        this.selectedEntryIds.clear();
      },
      error: (err) => console.error('Error clearing clipboard:', err)
    });
  }

  exportSelected(): void {
    if (this.selectedEntryIds.size === 0) return;
    this.exporting = true;
    this.lastExportResult = null;

    const request = {
      targetModule: this.targetModule,
      externalRefId: this.externalRefId || undefined,
      notes: this.exportNotes || undefined,
      entryIds: Array.from(this.selectedEntryIds),
      clearAfterExport: this.clearAfterExport
    };

    this.clipboardService.exportClipboard(request).subscribe({
      next: (res) => {
        this.lastExportResult = res;
        this.exporting = false;
        this.loadClipboard();
        this.loadExportsHistory();
      },
      error: (err) => {
        console.error('Error exporting clipboard:', err);
        this.errorMessage = 'Failed to export clipboard entries.';
        this.exporting = false;
      }
    });
  }

  loadExportsHistory(): void {
    this.clipboardService.getExports().subscribe({
      next: (list) => this.exports = list,
      error: (err) => console.error('Error loading exports history:', err)
    });
  }

  toggleViewHistory(): void {
    this.viewingHistory = !this.viewingHistory;
    if (this.viewingHistory && this.exports.length === 0) {
      this.loadExportsHistory();
    }
  }
}
