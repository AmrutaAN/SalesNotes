import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import {
  IonHeader,
  IonToolbar,
  IonTitle,
  IonButtons,
  IonButton,
  IonContent,
  IonList,
  IonItem,
  IonInput,
  IonTextarea,
  IonSelect,
  IonSelectOption,
  IonNote
} from '@ionic/angular';
import { CommonNoteFormComponent } from '@common/components/common-note-form/common-note-form.component';

@Component({
  selector: 'app-note-form-ionic',
  standalone: true,
  templateUrl: './note-form.page.html',
  styleUrls: ['./note-form.page.scss'],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    IonHeader,
    IonToolbar,
    IonTitle,
    IonButtons,
    IonButton,
    IonContent,
    IonList,
    IonItem,
    IonInput,
    IonTextarea,
    IonSelect,
    IonSelectOption,
    IonNote
  ]
})
export class NoteFormIonicComponent extends CommonNoteFormComponent {
  override cancel(): void {
    if (this.isEditMode && this.noteId) {
      this.router.navigate(['/tabs/notes', this.noteId]);
    } else {
      this.router.navigate(['/tabs/notes']);
    }
  }
}
