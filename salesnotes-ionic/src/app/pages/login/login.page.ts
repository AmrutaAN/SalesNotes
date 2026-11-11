import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  IonHeader,
  IonToolbar,
  IonTitle,
  IonContent,
  IonCard,
  IonCardHeader,
  IonCardTitle,
  IonCardSubtitle,
  IonCardContent,
  IonItem,
  IonInput,
  IonButton,
  IonSpinner,
  IonIcon,
  IonText
} from '@ionic/angular';
import { addIcons } from 'ionicons';
import { lockClosedOutline, personOutline, logInOutline } from 'ionicons/icons';
import { CommonLoginComponent } from '@common/components/common-login/common-login.component';

@Component({
  selector: 'app-login-ionic',
  standalone: true,
  templateUrl: './login.page.html',
  styleUrls: ['./login.page.scss'],
  imports: [
    CommonModule,
    FormsModule,
    IonHeader,
    IonToolbar,
    IonTitle,
    IonContent,
    IonCard,
    IonCardHeader,
    IonCardTitle,
    IonCardSubtitle,
    IonCardContent,
    IonItem,
    IonInput,
    IonButton,
    IonSpinner,
    IonIcon,
    IonText
  ]
})
export class LoginIonicPage extends CommonLoginComponent {
  constructor() {
    super();
    addIcons({ lockClosedOutline, personOutline, logInOutline });
  }
}
