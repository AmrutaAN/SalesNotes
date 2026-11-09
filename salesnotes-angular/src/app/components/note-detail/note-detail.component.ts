import { Component } from '@angular/core';
import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { CommonNoteDetailComponent } from '@common/components/common-note-detail/common-note-detail.component';

@Component({
  selector: 'app-note-detail',
  standalone: true,
  imports: [CommonModule, RouterLink, CurrencyPipe, DatePipe],
  templateUrl: './note-detail.component.html',
  styleUrls: ['./note-detail.component.scss']
})
export class NoteDetailComponent extends CommonNoteDetailComponent {
  // Inherits noteId, note, loading, errorMessage, loadNote(), updateStatus(), deleteCurrentNote(), navigateBack()
}
