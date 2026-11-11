import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { LoginComponent } from './login.component';

describe('LoginComponent Password Visibility Toggle', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoginComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([])
      ]
    }).compileComponents();
  });

  it('should initialize with password hidden by default', () => {
    const fixture = TestBed.createComponent(LoginComponent);
    const component = fixture.componentInstance;
    expect(component.showPassword).toBe(false);
    expect(component.showOldPassword).toBe(false);
    expect(component.showNewPassword).toBe(false);
    expect(component.showConfirmPassword).toBe(false);
  });

  it('should toggle password field type between password and text', () => {
    const fixture = TestBed.createComponent(LoginComponent);
    const component = fixture.componentInstance;
    fixture.detectChanges();

    const inputEl = fixture.nativeElement.querySelector('#password') as HTMLInputElement;
    expect(inputEl.type).toBe('password');

    const toggleBtn = fixture.nativeElement.querySelector('.password-input-wrapper .eye-toggle-btn') as HTMLButtonElement;
    expect(toggleBtn).toBeTruthy();

    // Click to show password
    toggleBtn.click();
    fixture.detectChanges();

    expect(component.showPassword).toBe(true);
    expect(inputEl.type).toBe('text');

    // Click again to hide password
    toggleBtn.click();
    fixture.detectChanges();

    expect(component.showPassword).toBe(false);
    expect(inputEl.type).toBe('password');
  });

  it('should toggle reset password fields independently in Option A', () => {
    const fixture = TestBed.createComponent(LoginComponent);
    const component = fixture.componentInstance;

    component.setMode('reset_old');
    fixture.detectChanges();

    expect(component.showOldPassword).toBe(false);
    expect(component.showNewPassword).toBe(false);
    expect(component.showConfirmPassword).toBe(false);

    component.toggleShowOldPassword();
    expect(component.showOldPassword).toBe(true);
    expect(component.showNewPassword).toBe(false);

    component.toggleShowNewPassword();
    expect(component.showNewPassword).toBe(true);

    component.toggleShowConfirmPassword();
    expect(component.showConfirmPassword).toBe(true);
  });
});
