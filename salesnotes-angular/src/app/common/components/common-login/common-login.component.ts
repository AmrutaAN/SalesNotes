import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
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
  protected cdr = inject(ChangeDetectorRef);

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

  // Forgot password - Method B (Email reset link sent to mailbox, NOT displayed on screen)
  emailOrUserInput = '';
  emailLinkSent = false;
  maskedTargetEmail = '';
  generatedToken = '';
  generatedResetUrl = '';
  generatedEmail = '';

  // Token reset form
  tokenInput = '';
  tokenUser = '';
  tokenNewPwd = '';
  tokenConfirmPwd = '';

  // Password visibility toggle states
  showPassword = false;
  showOldPassword = false;
  showNewPassword = false;
  showConfirmPassword = false;
  showTokenNewPassword = false;
  showTokenConfirmPassword = false;

  toggleShowPassword(): void {
    this.showPassword = !this.showPassword;
    this.cdr.markForCheck();
  }

  toggleShowOldPassword(): void {
    this.showOldPassword = !this.showOldPassword;
    this.cdr.markForCheck();
  }

  toggleShowNewPassword(): void {
    this.showNewPassword = !this.showNewPassword;
    this.cdr.markForCheck();
  }

  toggleShowConfirmPassword(): void {
    this.showConfirmPassword = !this.showConfirmPassword;
    this.cdr.markForCheck();
  }

  toggleShowTokenNewPassword(): void {
    this.showTokenNewPassword = !this.showTokenNewPassword;
    this.cdr.markForCheck();
  }

  toggleShowTokenConfirmPassword(): void {
    this.showTokenConfirmPassword = !this.showTokenConfirmPassword;
    this.cdr.markForCheck();
  }

  ngOnInit(): void {
    const queryReturnUrl = this.route.snapshot.queryParams['returnUrl'];
    if (queryReturnUrl) {
      this.returnUrl = queryReturnUrl;
    }

    // Subscribe to query params so changes (e.g. clicking a reset link with token) update reactively
    this.route.queryParams.subscribe(params => {
      const tokenParam = params['token'];
      const userParam = params['user'];
      if (tokenParam) {
        this.tokenInput = tokenParam;
        if (userParam) {
          this.tokenUser = userParam;
        }
        this.viewMode = 'reset_token';
        this.cdr.markForCheck();
      }
    });

    // Check if loaded on /forgot-password or /reset-password routes
    const currentPath = this.route.snapshot.routeConfig?.path;
    if (currentPath === 'forgot-password') {
      this.viewMode = 'forgot_options';
    } else if (currentPath === 'reset-password' && !this.route.snapshot.queryParams['token']) {
      this.viewMode = 'reset_token';
    }
  }

  setMode(mode: LoginViewMode): void {
    this.viewMode = mode;
    this.errorMessage = null;
    this.successNotice = null;
    this.showPassword = false;
    this.showOldPassword = false;
    this.showNewPassword = false;
    this.showConfirmPassword = false;
    this.showTokenNewPassword = false;
    this.showTokenConfirmPassword = false;
    this.emailLinkSent = false;
    this.maskedTargetEmail = '';
    if (mode === 'request_email') {
      this.generatedToken = '';
      this.generatedResetUrl = '';
      this.generatedEmail = '';
    } else if (mode === 'forgot_options') {
      this.generatedToken = '';
      this.generatedResetUrl = '';
      this.generatedEmail = '';
      if (this.username.trim()) {
        this.forgotUser = this.username.trim();
        this.emailOrUserInput = this.username.trim();
      }
    }
    this.cdr.markForCheck();
  }

  onSubmit(): void {
    if (!this.username.trim() || !this.password) {
      this.errorMessage = 'Please enter both username and password';
      this.cdr.markForCheck();
      return;
    }

    this.loading = true;
    this.errorMessage = null;
    this.successNotice = null;
    this.cdr.markForCheck();

    this.authService.login({
      username: this.username.trim(),
      password: this.password
    }).subscribe({
      next: (res) => {
        this.loading = false;
        this.cdr.markForCheck();
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
        this.cdr.markForCheck();
      }
    });
  }

  selectPreset(username: string, pass: string): void {
    this.username = username;
    this.password = pass;
    this.errorMessage = null;
    this.successNotice = null;
    this.cdr.markForCheck();
  }

  // --- Scenario 1A: Reset password using old password verification ---
  submitResetUsingOld(): void {
    if (!this.forgotUser.trim() || !this.oldPwdInput.trim() || !this.newPwdInput.trim() || !this.confirmPwdInput.trim()) {
      this.errorMessage = 'All fields (Username, Old Password, New Password, Confirm Password) are required.';
      this.cdr.markForCheck();
      return;
    }
    if (this.newPwdInput.length < 6) {
      this.errorMessage = 'New password must be at least 6 characters long.';
      this.cdr.markForCheck();
      return;
    }
    if (this.newPwdInput !== this.confirmPwdInput) {
      this.errorMessage = 'New password and confirm password do not match.';
      this.cdr.markForCheck();
      return;
    }

    this.loading = true;
    this.errorMessage = null;
    this.successNotice = null;
    this.cdr.markForCheck();

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
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err?.error?.message || 'Failed to verify old password. Please try again.';
        this.cdr.markForCheck();
      }
    });
  }

  // --- Scenario 1B: Request password reset link on email ---
  submitRequestEmailLink(): void {
    if (!this.emailOrUserInput.trim()) {
      this.errorMessage = 'Please enter your registered username or email address.';
      this.cdr.markForCheck();
      return;
    }

    this.loading = true;
    this.errorMessage = null;
    this.successNotice = null;
    this.cdr.markForCheck();

    this.authService.requestPasswordResetLink({
      usernameOrEmail: this.emailOrUserInput.trim()
    }).subscribe({
      next: (res) => {
        this.loading = false;
        this.emailLinkSent = true;
        this.maskedTargetEmail = res.maskedEmail || res.email || '';
        // Security policy: token and resetUrl are delivered to user's mail, never held in memory for screen display
        this.generatedToken = '';
        this.generatedResetUrl = '';
        this.generatedEmail = '';
        this.tokenInput = '';
        this.tokenUser = '';
        this.successNotice = res.message || 'A password reset link has been dispatched to your registered email address.';
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err?.error?.message || 'Could not find account for that username/email.';
        this.cdr.markForCheck();
      }
    });
  }

  goToTokenReset(): void {
    this.viewMode = 'reset_token';
    this.errorMessage = null;
    this.cdr.markForCheck();
  }

  // --- Scenario 1C: Complete reset using email link ---
  submitResetUsingToken(): void {
    if (!this.tokenInput.trim()) {
      this.errorMessage = 'Reset link is invalid or missing. Please click the link sent to your email or request a new one.';
      this.cdr.markForCheck();
      return;
    }
    if (!this.tokenNewPwd.trim() || !this.tokenConfirmPwd.trim()) {
      this.errorMessage = 'Please enter and confirm your new password.';
      this.cdr.markForCheck();
      return;
    }
    if (this.tokenNewPwd.length < 6) {
      this.errorMessage = 'New password must be at least 6 characters long.';
      this.cdr.markForCheck();
      return;
    }
    if (this.tokenNewPwd !== this.tokenConfirmPwd) {
      this.errorMessage = 'New password and confirm password do not match.';
      this.cdr.markForCheck();
      return;
    }

    this.loading = true;
    this.errorMessage = null;
    this.successNotice = null;
    this.cdr.markForCheck();

    this.authService.resetPasswordWithToken({
      token: this.tokenInput.trim(),
      usernameOrEmail: this.tokenUser.trim(),
      newPassword: this.tokenNewPwd
    }).subscribe({
      next: (res) => {
        this.loading = false;
        this.successNotice = res.message || 'Password reset successfully! Please sign in with your new password.';
        this.username = this.tokenUser.trim();
        this.password = '';
        this.tokenInput = '';
        this.tokenNewPwd = '';
        this.tokenConfirmPwd = '';
        this.viewMode = 'login';
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err?.error?.message || 'Invalid or expired password reset link. Please request a new link.';
        this.cdr.markForCheck();
      }
    });
  }
}
