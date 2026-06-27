import { Routes } from '@angular/router';
import { authGuard, guestOnlyGuard } from './core/guards/auth.guard';
import { ShellComponent } from './core/layout/shell.component';
import { LoginPageComponent } from './features/auth/login.page';
import { DashboardPageComponent } from './features/dashboard/dashboard.page';

export const routes: Routes = [
  {
    path: 'login',
    component: LoginPageComponent,
    canActivate: [guestOnlyGuard],
  },
  {
    path: '',
    component: ShellComponent,
    canActivate: [authGuard],
    children: [
      {
        path: '',
        pathMatch: 'full',
        component: DashboardPageComponent,
      },
      {
        path: 'perfil',
        loadComponent: () =>
          import('./features/user-profile/user-profile.page').then(
            (module) => module.UserProfilePageComponent,
          ),
      },
      {
        path: 'meus-anuncios',
        loadComponent: () =>
          import('./features/professor-profile/professor-profile.page').then(
            (module) => module.ProfessorProfilePageComponent,
          ),
      },
      {
        path: 'perfil-professor',
        redirectTo: 'meus-anuncios',
      },
    ],
  },
  {
    path: '**',
    redirectTo: '',
  },
];
