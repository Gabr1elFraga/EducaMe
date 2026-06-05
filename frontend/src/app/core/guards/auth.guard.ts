import { inject } from '@angular/core';
import {
  CanActivateFn,
  Router,
  RouterStateSnapshot,
  UrlTree,
} from '@angular/router';
import { Session } from '@supabase/supabase-js';
import { filter, map, take } from 'rxjs';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = (_route, state: RouterStateSnapshot) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return authService.session$.pipe(
    filter((session): session is Session | null => session !== undefined),
    take(1),
    map((session) =>
      session
        ? true
        : router.createUrlTree(['/login'], {
            queryParams: { redirect: state.url },
          }),
    ),
  );
};

export const guestOnlyGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return authService.session$.pipe(
    filter((session): session is Session | null => session !== undefined),
    take(1),
    map((session) => (session ? router.createUrlTree(['/']) : true)),
  );
};
