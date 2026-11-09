import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { CommonNoteFormComponent } from '../../common/components/common-note-form/common-note-form.component';

@Component({
  selector: 'app-note-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './note-form.component.html',
  styleUrls: ['./note-form.component.scss']
})
export class NoteFormComponent extends CommonNoteFormComponent {
  // Inherits noteForm, isEditMode, noteId, customers, loading, submitting, errorMessage,
  // priorities, statuses, initForm(), loadCustomers(), loadNote(), submit(), cancel()
}
