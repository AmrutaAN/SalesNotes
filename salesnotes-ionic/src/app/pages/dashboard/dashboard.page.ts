import { Component } from '@angular/core';
import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import {
  IonHeader,
  IonToolbar,
  IonTitle,
  IonContent,
  IonRefresher,
  IonRefresherContent,
  IonGrid,
  IonRow,
  IonCol,
  IonCard,
  IonCardHeader,
  IonCardSubtitle,
  IonCardTitle,
  IonCardContent,
  IonList,
  IonItem,
  IonLabel,
  IonBadge,
  IonSpinner,
  IonButton,
  IonIcon
} from '@ionic/angular';
import { addIcons } from 'ionicons';
import { statsChartOutline, cashOutline, alertCircleOutline, checkmarkCircleOutline, refreshOutline } from 'ionicons/icons';
import { CommonDashboardComponent } from '../../common/components/common-dashboard/common-dashboard.component';

@Component({
  selector: 'app-dashboard-ionic',
  standalone: true,
  templateUrl: './dashboard.page.html',
  styleUrls: ['./dashboard.page.scss'],
  imports: [
    CommonModule,
    RouterLink,
    CurrencyPipe,
    DatePipe,
    IonHeader,
    IonToolbar,
    IonTitle,
    IonContent,
    IonRefresher,
    IonRefresherContent,
    IonGrid,
    IonRow,
    IonCol,
    IonCard,
    IonCardHeader,
    IonCardSubtitle,
    IonCardTitle,
    IonCardContent,
    IonList,
    IonItem,
    IonLabel,
    IonBadge,
    IonSpinner,
    IonButton,
    IonIcon
  ]
})
export class DashboardIonicComponent extends CommonDashboardComponent {
  constructor() {
    super();
    addIcons({ statsChartOutline, cashOutline, alertCircleOutline, checkmarkCircleOutline, refreshOutline });
  }

  handleRefresh(event: any) {
    this.loadSummary();
    setTimeout(() => {
      event.target.complete();
    }, 600);
  }
}
