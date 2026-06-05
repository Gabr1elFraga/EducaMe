import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { DASHBOARD_MOCK } from '../data/dashboard.mock';
import { DashboardSummary } from '../models/dashboard-summary.model';

@Injectable({
  providedIn: 'root',
})
export class DashboardService {
  getSummary(): Observable<DashboardSummary> {
    return of(DASHBOARD_MOCK);
  }
}
