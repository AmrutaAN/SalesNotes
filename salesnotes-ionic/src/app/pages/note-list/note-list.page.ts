import { Component } from '@angular/core';
import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import {
  IonHeader,
  IonToolbar,
  IonTitle,
  IonContent,
  IonRefresher,
  IonRefresherContent,
  IonSearchbar,
  IonSegment,
  IonSegmentButton,
  IonLabel,
  IonList,
  IonItemSliding,
  IonItem,
  IonItemOptions,
  IonItemOption,
  IonIcon,
  IonBadge,
  IonFab,
  IonFabButton,
  IonSpinner,
  IonCard,
  IonCardContent
} from '@ionic/angular';
import { addIcons } from 'ionicons';
import { addOutline, trashOutline, createOutline, businessOutline, timeOutline, checkmarkOutline } from 'ionicons/icons';
import { CommonNoteListComponent } from '../../common/components/common-note-list/common-note-list.component';

@Component({
  selector: 'app-note-list-ionic',
  standalone: true,
  templateUrl: './note-list.page.html',
  styleUrls: ['./note-list.page.scss'],
  imports: [
    CommonModule,
    FormsModule,
    RouterLink,
    CurrencyPipe,
    DatePipe,
    IonHeader,
    IonToolbar,
    IonTitle,
    IonContent,
    IonRefresher,
    IonRefresherContent,
    IonSearchbar,
    IonSegment,
    IonSegmentButton,
    IonLabel,
    IonList,
    IonItemSliding,
    IonItem,
    IonItemOptions,
    IonItemOption,
    IonIcon,
    IonBadge,
    IonFab,
    IonFabButton,
    IonSpinner,
    IonCard,
    IonCardContent
  ]
})
export class NoteListIonicComponent extends CommonNoteListComponent {
  constructor() {
    super();
    addIcons({ addOutline, trashOutline, createOutline, businessOutline, timeOutline, checkmarkOutline });
  }

  handleRefresh(event: any) {
    this.loadNotes();
    setTimeout(() => {
      event.target.complete();
    }, 600);
  }

  onSegmentChanged(event: any) {
    const value = event.detail.value;
    this.onStatusFilter(value);
  }
}
