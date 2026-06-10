import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-stat-card',
  standalone: true,
  imports: [CommonModule, MatCardModule],
  template: `
    <mat-card class="stat-card" [attr.data-tone]="tone">
      <span class="label">{{ label }}</span>
      <strong>{{ value }}</strong>
      <small>{{ description }}</small>
    </mat-card>
  `,
  styles: [
    `
      :host {
        display: block;
      }

      .stat-card {
        position: relative;
        overflow: hidden;
        min-height: 154px;
        padding: 18px;
        border-radius: 24px;
        display: flex;
        flex-direction: column;
        justify-content: space-between;
        gap: 12px;
        border: 1px solid var(--line);
        background: linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(248, 251, 255, 0.98));
        box-shadow: var(--shadow-soft);
      }

      .stat-card::after {
        content: '';
        position: absolute;
        inset: auto 0 0 0;
        height: 4px;
        background: var(--tone, var(--primary));
      }

      .label {
        color: var(--muted);
        font: 700 0.74rem/1 'Plus Jakarta Sans', sans-serif;
        letter-spacing: 0.14em;
        text-transform: uppercase;
      }

      strong {
        color: var(--text);
        font: 800 clamp(1.8rem, 3vw, 2.8rem) / 1 'Plus Jakarta Sans', sans-serif;
        letter-spacing: -0.04em;
      }

      small {
        color: var(--muted);
        line-height: 1.55;
      }

      .stat-card[data-tone='gold'] {
        --tone: #f59e0b;
      }

      .stat-card[data-tone='blue'] {
        --tone: #2563eb;
      }

      .stat-card[data-tone='orange'] {
        --tone: #ea580c;
      }

      .stat-card[data-tone='green'] {
        --tone: #16a34a;
      }
    `,
  ],
})
export class StatCardComponent {
  @Input({ required: true }) label!: string;
  @Input({ required: true }) value!: string;
  @Input({ required: true }) description!: string;
  @Input() tone: 'gold' | 'blue' | 'orange' | 'green' = 'gold';
}
