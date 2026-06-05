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
        component: DashboardPageComponent,
      },
    ],
  },
  {
    path: '**',
    redirectTo: '',
  },
];
