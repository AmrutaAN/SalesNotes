import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, RouterLink, RouterLinkActive, Router } from '@angular/router';
import { AuthService } from '@common/services/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'SalesNotes - Web App';
  protected authService = inject(AuthService);
  protected router = inject(Router);

  isLoggedIn(): boolean {
    return this.authService.isAuthenticated();
  }

  isLoginPage(): boolean {
    return this.router.url.startsWith('/login');
  }

  getCurrentUser() {
    return this.authService.getCurrentUser();
  }

  canAccessUsers(): boolean {
    return this.authService.hasAnyRole(['SuperUser', 'Sysadmin', 'companyadmin']);
  }

  canAccessRoles(): boolean {
    return this.authService.hasAnyRole(['SuperUser', 'Sysadmin']);
  }

  onLogout(): void {
    this.authService.logout();
  }
}
