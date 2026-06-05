import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { DashboardPageComponent } from './features/dashboard/dashboard.page';

export const routes: Routes = [
  {
    path: '',
    component: DashboardPageComponent,
    canActivate: [authGuard],
  },
  {
    path: '**',
    redirectTo: '',
  },
];
