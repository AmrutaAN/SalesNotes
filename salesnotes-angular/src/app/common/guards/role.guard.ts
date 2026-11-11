import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const roleGuard: CanActivateFn = (route) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // If not even authenticated, let authGuard handle it
  if (!authService.isAuthenticated()) {
    router.navigate(['/login']);
    return false;
  }

  const expectedRoles: string[] = route.data?.['roles'] || [];

  if (expectedRoles.length === 0 || authService.hasAnyRole(expectedRoles)) {
    return true;
  }

  // Not authorized for this feature; redirect to dashboard
  console.warn(`Access denied. User role does not match required roles: [${expectedRoles.join(', ')}]`);
  router.navigate(['/dashboard']);
  return false;
};
