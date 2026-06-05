import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-stat-card',
  standalone: true,
  imports: [CommonModule],
  template: `
    <article class="stat-card" [attr.data-tone]="tone">
      <span class="label">{{ label }}</span>
      <strong>{{ value }}</strong>
      <small>{{ description }}</small>
    </article>
  `,
  styles: [
    `
      .stat-card {
        min-height: 150px;
        padding: 18px;
        border-radius: 24px;
        display: flex;
        flex-direction: column;
        justify-content: space-between;
        border: 1px solid rgba(255, 255, 255, 0.08);
        background: linear-gradient(180deg, rgba(255, 255, 255, 0.08), rgba(255, 255, 255, 0.03));
      }

      .label {
        color: var(--muted);
        font: 700 0.78rem/1 "Trebuchet MS", "Segoe UI", sans-serif;
        letter-spacing: 0.14em;
        text-transform: uppercase;
      }

      strong {
        font: 700 clamp(1.9rem, 3vw, 3rem) / 1 "Trebuchet MS", "Segoe UI", sans-serif;
      }

      small {
        color: var(--muted);
      }

      .stat-card[data-tone='gold'] {
        border-color: rgba(243, 191, 104, 0.25);
      }

      .stat-card[data-tone='blue'] {
        border-color: rgba(106, 212, 255, 0.25);
      }

      .stat-card[data-tone='orange'] {
        border-color: rgba(255, 145, 99, 0.22);
      }

      .stat-card[data-tone='green'] {
        border-color: rgba(100, 216, 154, 0.25);
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
