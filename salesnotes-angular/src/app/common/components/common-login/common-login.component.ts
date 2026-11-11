import { Component, OnInit, inject } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { AuthService } from '../../services/auth.service';

export type LoginViewMode = 'login' | 'forgot_options' | 'reset_old' | 'request_email' | 'reset_token';

@Component({
  template: '',
  standalone: true
})
export class CommonLoginComponent implements OnInit {
  protected authService = inject(AuthService);
  protected router = inject(Router);
  protected route = inject(ActivatedRoute);

  // View state
  viewMode: LoginViewMode = 'login';

  // Login credentials
  username = '';
  password = '';
  loading = false;
  errorMessage: string | null = null;
  successNotice: string | null = null;
  returnUrl = '/dashboard';

  // Forgot password - Method A (Old password verification)
  forgotUser = '';
  oldPwdInput = '';
  newPwdInput = '';
  confirmPwdInput = '';

  // Forgot password - Method B (Email reset link & token)
  emailOrUserInput = '';
  generatedToken = '';
  generatedResetUrl = '';
  generatedEmail = '';

  // Token reset form
  tokenInput = '';
  tokenUser = '';
  tokenNewPwd = '';
  tokenConfirmPwd = '';

  ngOnInit(): void {
    const queryReturnUrl = this.route.snapshot.queryParams['returnUrl'];
    if (queryReturnUrl) {
      this.returnUrl = queryReturnUrl;
    }

    // Check if opened with reset token in query params
    const tokenParam = this.route.snapshot.queryParams['token'];
    const userParam = this.route.snapshot.queryParams['user'];
    if (tokenParam) {
      this.tokenInput = tokenParam;
      if (userParam) {
        this.tokenUser = userParam;
      }
      this.viewMode = 'reset_token';
    }
  }

  setMode(mode: LoginViewMode): void {
    this.viewMode = mode;
    this.errorMessage = null;
    this.successNotice = null;
    if (mode === 'login') {
      // keep username if typed
    } else if (mode === 'forgot_options' && this.username.trim()) {
      this.forgotUser = this.username.trim();
      this.emailOrUserInput = this.username.trim();
    }
  }

  onSubmit(): void {
    if (!this.username.trim() || !this.password) {
      this.errorMessage = 'Please enter both username and password';
      return;
    }

    this.loading = true;
    this.errorMessage = null;
    this.successNotice = null;

    this.authService.login({
      username: this.username.trim(),
      password: this.password
    }).subscribe({
      next: (res) => {
        this.loading = false;
        this.router.navigateByUrl(this.returnUrl);
      },
      error: (err) => {
        this.loading = false;
        console.error('Login failed:', err);
        if (err?.error?.message) {
          // Specific prompt: "Wrong password entered for user '...' or user not found"
          this.errorMessage = err.error.message;
        } else if (err.status === 401) {
          this.errorMessage = 'Wrong password or invalid credentials. Please try again.';
        } else {
          this.errorMessage = 'Could not connect to authentication server. Please check backend is running at http://localhost:8080';
        }
      }
    });
  }

  selectPreset(username: string, pass: string): void {
    this.username = username;
    this.password = pass;
    this.errorMessage = null;
    this.successNotice = null;
  }

  // --- Scenario 1A: Reset password using old password verification ---
  submitResetUsingOld(): void {
    if (!this.forgotUser.trim() || !this.oldPwdInput.trim() || !this.newPwdInput.trim()) {
      this.errorMessage = 'All fields (Username, Old Password, New Password) are required.';
      return;
    }
    if (this.newPwdInput.length < 6) {
      this.errorMessage = 'New password must be at least 6 characters long.';
      return;
    }
    if (this.confirmPwdInput && this.newPwdInput !== this.confirmPwdInput) {
      this.errorMessage = 'New password and confirm password do not match.';
      return;
    }

    this.loading = true;
    this.errorMessage = null;
    this.successNotice = null;

    this.authService.resetPasswordUsingOldPassword({
      usernameOrEmail: this.forgotUser.trim(),
      oldPassword: this.oldPwdInput,
      newPassword: this.newPwdInput
    }).subscribe({
      next: (res) => {
        this.loading = false;
        this.successNotice = res.message || 'Password reset successfully! Please sign in with your new password.';
        this.username = this.forgotUser.trim();
        this.password = '';
        this.oldPwdInput = '';
        this.newPwdInput = '';
        this.confirmPwdInput = '';
        this.viewMode = 'login';
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err?.error?.message || 'Failed to verify old password. Please try again.';
      }
    });
  }

  // --- Scenario 1B: Request password reset link on email ---
  submitRequestEmailLink(): void {
    if (!this.emailOrUserInput.trim()) {
      this.errorMessage = 'Please enter your registered username or email address.';
      return;
    }

    this.loading = true;
    this.errorMessage = null;
    this.successNotice = null;

    this.authService.requestPasswordResetLink({
      usernameOrEmail: this.emailOrUserInput.trim()
    }).subscribe({
      next: (res) => {
        this.loading = false;
        this.generatedToken = res.token || '';
        this.generatedResetUrl = res.resetUrl || '';
        this.generatedEmail = res.email || '';
        this.tokenInput = res.token || '';
        this.tokenUser = this.emailOrUserInput.trim();
        this.successNotice = res.message;
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err?.error?.message || 'Could not find account for that username/email.';
      }
    });
  }

  goToTokenReset(): void {
    this.viewMode = 'reset_token';
    this.errorMessage = null;
  }

  // --- Scenario 1C: Complete reset using token/link ---
  submitResetUsingToken(): void {
    if (!this.tokenInput.trim() || !this.tokenNewPwd.trim()) {
      this.errorMessage = 'Reset token and new password are required.';
      return;
    }
    if (this.tokenNewPwd.length < 6) {
      this.errorMessage = 'New password must be at least 6 characters long.';
      return;
    }
    if (this.tokenConfirmPwd && this.tokenNewPwd !== this.tokenConfirmPwd) {
      this.errorMessage = 'New password and confirm password do not match.';
      return;
    }

    this.loading = true;
    this.errorMessage = null;
    this.successNotice = null;

    this.authService.resetPasswordWithToken({
      token: this.tokenInput.trim(),
      usernameOrEmail: this.tokenUser.trim(),
      newPassword: this.tokenNewPwd
    }).subscribe({
      next: (res) => {
        this.loading = false;
        this.successNotice = res.message || 'Password reset successfully! Please sign in.';
        this.username = this.tokenUser.trim();
        this.password = '';
        this.tokenInput = '';
        this.tokenNewPwd = '';
        this.tokenConfirmPwd = '';
        this.viewMode = 'login';
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err?.error?.message || 'Invalid or expired reset token.';
      }
    });
  }
}
