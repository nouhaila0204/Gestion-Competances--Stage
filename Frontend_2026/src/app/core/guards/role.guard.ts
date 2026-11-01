import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService, Role } from '../services/auth.service';

export const roleGuard: CanActivateFn = (route) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const rolesAutorises = route.data['roles'] as Role[];
  if (auth.hasRole(...rolesAutorises)) return true;
  router.navigate(['/acces-refuse']);
  return false;
};
