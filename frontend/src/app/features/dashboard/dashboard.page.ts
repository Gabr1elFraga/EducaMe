import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { StatCardComponent } from '../../shared/components/stat-card/stat-card.component';
import { DashboardSummary } from './models/dashboard-summary.model';
import { DashboardService } from './services/dashboard.service';

@Component({
  selector: 'app-dashboard-page',
  standalone: true,
  imports: [CommonModule, StatCardComponent],
  templateUrl: './dashboard.page.html',
  styleUrl: './dashboard.page.css',
})
export class DashboardPageComponent implements OnInit {
  summary: DashboardSummary | null = null;
  loading = true;
  errorMessage = '';

  constructor(private readonly dashboardService: DashboardService) {}

  ngOnInit(): void {
    this.dashboardService.getSummary().subscribe({
      next: (summary) => {
        this.summary = summary;
        this.loading = false;
      },
      error: (error: unknown) => {
        this.loading = false;
        this.errorMessage = this.normalizeError(error);
      },
    });
  }

  private normalizeError(error: unknown): string {
    if (error instanceof Error) {
      return error.message;
    }

    return 'Nao foi possivel carregar o dashboard.';
  }
}
