import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);

  // ✅ Cloner la requête avec credentials
  const clonedReq = req.clone({ withCredentials: true });

  return next(clonedReq).pipe(
    catchError((err) => {
      // ✅ Vérifier qu'on est dans un navigateur avant d'utiliser localStorage
      if (err.status === 401 && typeof window !== 'undefined' && window.localStorage) {
        localStorage.removeItem('sbgs_user'); // Assurez-vous que la clé est correcte
        router.navigate(['/login']);
      }
      return throwError(() => err);
    }),
  );
};
