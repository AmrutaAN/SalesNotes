import { Component } from '@angular/core';
import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { CommonNoteListComponent } from '../../common/components/common-note-list/common-note-list.component';

@Component({
  selector: 'app-note-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, CurrencyPipe, DatePipe],
  templateUrl: './note-list.component.html',
  styleUrls: ['./note-list.component.scss']
})
export class NoteListComponent extends CommonNoteListComponent {
  // Inherits all common component.ts business logic:
  // notes, filteredNotes, loading, errorMessage, searchTerm, selectedStatus, selectedPriority,
  // loadNotes(), applyFilter(), onSearchChange(), onStatusFilter(), onPriorityFilter(), deleteNote(), updateStatus()
}
