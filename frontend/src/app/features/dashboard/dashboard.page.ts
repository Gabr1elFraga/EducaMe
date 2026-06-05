import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Observable } from 'rxjs';
import { StatCardComponent } from '../../shared/components/stat-card/stat-card.component';
import { DashboardService } from './services/dashboard.service';
import { DashboardSummary } from './models/dashboard-summary.model';

@Component({
  selector: 'app-dashboard-page',
  standalone: true,
  imports: [CommonModule, StatCardComponent],
  templateUrl: './dashboard.page.html',
  styleUrl: './dashboard.page.css',
})
export class DashboardPageComponent {
  readonly summary$: Observable<DashboardSummary>;

  constructor(private readonly dashboardService: DashboardService) {
    this.summary$ = this.dashboardService.getSummary();
  }
}
