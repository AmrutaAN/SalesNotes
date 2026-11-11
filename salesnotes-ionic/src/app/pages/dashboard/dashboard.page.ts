import { Component, inject } from '@angular/core';
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
  IonButtons,
  IonIcon
} from '@ionic/angular';
import { addIcons } from 'ionicons';
import { statsChartOutline, cashOutline, alertCircleOutline, checkmarkCircleOutline, refreshOutline, logOutOutline } from 'ionicons/icons';
import { CommonDashboardComponent } from '@common/components/common-dashboard/common-dashboard.component';
import { AuthService } from '@common/services/auth.service';

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
    IonButtons,
    IonIcon
  ]
})
export class DashboardIonicComponent extends CommonDashboardComponent {
  private authService = inject(AuthService);

  constructor() {
    super();
    addIcons({ statsChartOutline, cashOutline, alertCircleOutline, checkmarkCircleOutline, refreshOutline, logOutOutline });
  }

  logout() {
    this.authService.logout();
  }

  handleRefresh(event: any) {
    this.loadSummary();
    setTimeout(() => {
      event.target.complete();
    }, 600);
  }
}
