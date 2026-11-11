import { Component, inject, signal, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterOutlet, RouterLink, RouterLinkActive, Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs';
import { AuthService } from '@common/services/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'SalesNotes - Web App';
  protected authService = inject(AuthService);
  protected router = inject(Router);
  protected cdr = inject(ChangeDetectorRef);

  currentUrl = signal<string>(window.location.pathname);

  // Change Password Modal State
  showChangePasswordModal = false;
  changePwdForm = {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  };
  changePwdLoading = false;
  changePwdError: string | null = null;
  changePwdSuccess: string | null = null;

  showModalOldPassword = false;
  showModalNewPassword = false;
  showModalConfirmPassword = false;

  toggleModalOldPassword(): void {
    this.showModalOldPassword = !this.showModalOldPassword;
    this.cdr.markForCheck();
  }

  toggleModalNewPassword(): void {
    this.showModalNewPassword = !this.showModalNewPassword;
    this.cdr.markForCheck();
  }

  toggleModalConfirmPassword(): void {
    this.showModalConfirmPassword = !this.showModalConfirmPassword;
    this.cdr.markForCheck();
  }

  constructor() {
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: any) => {
      this.currentUrl.set(event.urlAfterRedirects || event.url);
    });
  }

  isLoggedIn(): boolean {
    return this.authService.isAuthenticated();
  }

  showNavigation(): boolean {
    const url = this.currentUrl();
    const isAuthRoute = url.includes('/login') || url.includes('/reset-password') || url.includes('/forgot-password') || url === '/' || url === '';
    return this.isLoggedIn() && !isAuthRoute;
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

  openChangePasswordModal(): void {
    this.changePwdForm = {
      oldPassword: '',
      newPassword: '',
      confirmPassword: ''
    };
    this.showModalOldPassword = false;
    this.showModalNewPassword = false;
    this.showModalConfirmPassword = false;
    this.changePwdError = null;
    this.changePwdSuccess = null;
    this.showChangePasswordModal = true;
    this.cdr.markForCheck();
  }

  closeChangePasswordModal(): void {
    this.showChangePasswordModal = false;
    this.cdr.markForCheck();
  }

  submitChangePassword(): void {
    if (!this.changePwdForm.oldPassword.trim() || !this.changePwdForm.newPassword.trim() || !this.changePwdForm.confirmPassword.trim()) {
      this.changePwdError = 'All fields are required.';
      this.cdr.markForCheck();
      return;
    }
    if (this.changePwdForm.newPassword.length < 6) {
      this.changePwdError = 'New password must be at least 6 characters.';
      this.cdr.markForCheck();
      return;
    }
    if (this.changePwdForm.newPassword !== this.changePwdForm.confirmPassword) {
      this.changePwdError = 'New password and confirm password do not match.';
      this.cdr.markForCheck();
      return;
    }

    this.changePwdLoading = true;
    this.changePwdError = null;
    this.changePwdSuccess = null;
    this.cdr.markForCheck();

    this.authService.changePassword(this.changePwdForm).subscribe({
      next: (res) => {
        this.changePwdLoading = false;
        this.changePwdSuccess = res.message || 'Password changed successfully!';
        this.cdr.markForCheck();
        setTimeout(() => {
          this.closeChangePasswordModal();
        }, 1500);
      },
      error: (err) => {
        this.changePwdLoading = false;
        this.changePwdError = err?.error?.message || 'Failed to change password. Please verify your current password.';
        this.cdr.markForCheck();
      }
    });
  }
}
