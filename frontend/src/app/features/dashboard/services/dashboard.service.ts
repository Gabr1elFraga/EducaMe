import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { DashboardSummary } from '../models/dashboard-summary.model';

@Injectable({
  providedIn: 'root',
})
export class DashboardService {
  constructor(private readonly http: HttpClient) {}

  getSummary(): Observable<DashboardSummary> {
    return this.http.get<DashboardSummary>('/dashboard/summary');
  }
}
