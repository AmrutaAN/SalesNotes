import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import {
  LoginRequest,
  LoginResponse,
  AuthUser,
  VerifyOldPasswordRequest,
  ForgotPasswordRequestDto,
  ResetPasswordWithTokenDto,
  ChangePasswordRequest,
  PasswordActionResponse
} from '../models/auth.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  private apiUrl = 'http://localhost:8080/api/v1/auth';

  private readonly TOKEN_KEY = 'salesnotes_access_token';
  private readonly REFRESH_KEY = 'salesnotes_refresh_token';
  private readonly USER_KEY = 'salesnotes_user';

  // Reactive state
  currentUserSignal = signal<AuthUser | null>(this.loadStoredUser());

  constructor() {}

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap(res => {
        if (res && res.accessToken) {
          localStorage.setItem(this.TOKEN_KEY, res.accessToken);
          if (res.refreshToken) {
            localStorage.setItem(this.REFRESH_KEY, res.refreshToken);
          }
          if (res.user) {
            localStorage.setItem(this.USER_KEY, JSON.stringify(res.user));
            this.currentUserSignal.set(res.user);
          }
        }
      })
    );
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.REFRESH_KEY);
    localStorage.removeItem(this.USER_KEY);
    this.currentUserSignal.set(null);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  isAuthenticated(): boolean {
    const token = this.getToken();
    return !!token && token.trim().length > 0;
  }

  getCurrentUser(): AuthUser | null {
    return this.currentUserSignal();
  }

  hasAnyRole(roles: string[]): boolean {
    const user = this.getCurrentUser();
    if (!user || !user.role) return false;
    const current = user.role.toLowerCase();
    // SuperUser has access to everything
    if (current === 'superuser') return true;
    return roles.some(r => r.toLowerCase() === current);
  }

  resetPasswordUsingOldPassword(data: VerifyOldPasswordRequest): Observable<PasswordActionResponse> {
    return this.http.post<PasswordActionResponse>(`${this.apiUrl}/reset-password/verify-old`, data);
  }

  requestPasswordResetLink(data: ForgotPasswordRequestDto): Observable<PasswordActionResponse> {
    return this.http.post<PasswordActionResponse>(`${this.apiUrl}/forgot-password/request-link`, data);
  }

  resetPasswordWithToken(data: ResetPasswordWithTokenDto): Observable<PasswordActionResponse> {
    return this.http.post<PasswordActionResponse>(`${this.apiUrl}/forgot-password/reset`, data);
  }

  changePassword(data: ChangePasswordRequest): Observable<PasswordActionResponse> {
    return this.http.post<PasswordActionResponse>(`${this.apiUrl}/change-password`, data);
  }

  private loadStoredUser(): AuthUser | null {
    try {
      const raw = localStorage.getItem(this.USER_KEY);
      if (raw) {
        return JSON.parse(raw);
      }
    } catch (e) {
      console.error('Failed to parse stored user from localStorage', e);
    }
    return null;
  }
}
