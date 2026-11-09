import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  IonHeader,
  IonToolbar,
  IonTitle,
  IonContent,
  IonSearchbar,
  IonList,
  IonItem,
  IonLabel,
  IonIcon,
  IonSpinner,
  IonCard,
  IonCardContent,
  IonNote
} from '@ionic/angular';
import { addIcons } from 'ionicons';
import { businessOutline, mailOutline, callOutline } from 'ionicons/icons';
import { CommonCustomerListComponent } from '../../common/components/common-customer-list/common-customer-list.component';

@Component({
  selector: 'app-customer-list-ionic',
  standalone: true,
  templateUrl: './customer-list.page.html',
  styleUrls: ['./customer-list.page.scss'],
  imports: [
    CommonModule,
    FormsModule,
    IonHeader,
    IonToolbar,
    IonTitle,
    IonContent,
    IonSearchbar,
    IonList,
    IonItem,
    IonLabel,
    IonIcon,
    IonSpinner,
    IonCard,
    IonCardContent,
    IonNote
  ]
})
export class CustomerListIonicComponent extends CommonCustomerListComponent {
  constructor() {
    super();
    addIcons({ businessOutline, mailOutline, callOutline });
  }
}
