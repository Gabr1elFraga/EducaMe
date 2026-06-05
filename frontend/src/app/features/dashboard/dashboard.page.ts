import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { StatCardComponent } from '../../shared/components/stat-card/stat-card.component';
import { DashboardService } from './services/dashboard.service';

@Component({
  selector: 'app-dashboard-page',
  standalone: true,
  imports: [CommonModule, StatCardComponent],
  templateUrl: './dashboard.page.html',
  styleUrl: './dashboard.page.css',
})
export class DashboardPageComponent {
  readonly summary$ = this.dashboardService.getSummary();

  constructor(private readonly dashboardService: DashboardService) {}
}
