import { Component } from '@angular/core';
import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import {
  IonHeader,
  IonToolbar,
  IonTitle,
  IonButtons,
  IonBackButton,
  IonContent,
  IonCard,
  IonCardHeader,
  IonCardSubtitle,
  IonCardTitle,
  IonCardContent,
  IonList,
  IonItem,
  IonLabel,
  IonBadge,
  IonButton,
  IonIcon,
  IonSpinner,
  IonSelect,
  IonSelectOption
} from '@ionic/angular';
import { addIcons } from 'ionicons';
import { createOutline, trashOutline, businessOutline, mailOutline, callOutline, calendarOutline, cashOutline } from 'ionicons/icons';
import { CommonNoteDetailComponent } from '../../common/components/common-note-detail/common-note-detail.component';

@Component({
  selector: 'app-note-detail-ionic',
  standalone: true,
  templateUrl: './note-detail.page.html',
  styleUrls: ['./note-detail.page.scss'],
  imports: [
    CommonModule,
    RouterLink,
    CurrencyPipe,
    DatePipe,
    IonHeader,
    IonToolbar,
    IonTitle,
    IonButtons,
    IonBackButton,
    IonContent,
    IonCard,
    IonCardHeader,
    IonCardSubtitle,
    IonCardTitle,
    IonCardContent,
    IonList,
    IonItem,
    IonLabel,
    IonBadge,
    IonButton,
    IonIcon,
    IonSpinner,
    IonSelect,
    IonSelectOption
  ]
})
export class NoteDetailIonicComponent extends CommonNoteDetailComponent {
  constructor() {
    super();
    addIcons({ createOutline, trashOutline, businessOutline, mailOutline, callOutline, calendarOutline, cashOutline });
  }

  override navigateBack(): void {
    this.router.navigate(['/tabs/notes']);
  }
}
