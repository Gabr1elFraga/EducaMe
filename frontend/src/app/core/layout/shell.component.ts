import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterOutlet } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatDividerModule } from '@angular/material/divider';
import { MatMenuModule } from '@angular/material/menu';
import { MatToolbarModule } from '@angular/material/toolbar';
import { NavigationService } from '../services/navigation.service';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterOutlet,
    MatButtonModule,
    MatCardModule,
    MatIconModule,
    MatListModule,
    MatDividerModule,
    MatMenuModule,
    MatToolbarModule,
  ],
  templateUrl: './shell.component.html',
  styleUrl: './shell.component.css',
})
export class ShellComponent {
  readonly brazilStates = [
    { value: 'AC', label: 'Acre' },
    { value: 'AL', label: 'Alagoas' },
    { value: 'AP', label: 'Amapa' },
    { value: 'AM', label: 'Amazonas' },
    { value: 'BA', label: 'Bahia' },
    { value: 'CE', label: 'Ceara' },
    { value: 'DF', label: 'Distrito Federal' },
    { value: 'ES', label: 'Espirito Santo' },
    { value: 'GO', label: 'Goias' },
    { value: 'MA', label: 'Maranhao' },
    { value: 'MT', label: 'Mato Grosso' },
    { value: 'MS', label: 'Mato Grosso do Sul' },
    { value: 'MG', label: 'Minas Gerais' },
    { value: 'PA', label: 'Para' },
    { value: 'PB', label: 'Paraiba' },
    { value: 'PR', label: 'Parana' },
    { value: 'PE', label: 'Pernambuco' },
    { value: 'PI', label: 'Piaui' },
    { value: 'RJ', label: 'Rio de Janeiro' },
    { value: 'RN', label: 'Rio Grande do Norte' },
    { value: 'RS', label: 'Rio Grande do Sul' },
    { value: 'RO', label: 'Rondonia' },
    { value: 'RR', label: 'Roraima' },
    { value: 'SC', label: 'Santa Catarina' },
    { value: 'SP', label: 'Sao Paulo' },
    { value: 'SE', label: 'Sergipe' },
    { value: 'TO', label: 'Tocantins' },
  ] as const;

  readonly navItems: ReturnType<NavigationService['getSections']>;
  readonly searchControl = new FormControl('', { nonNullable: true });
  readonly stateControl = new FormControl('Todos os estados', { nonNullable: true });

  constructor(
    private readonly navigationService: NavigationService,
    readonly authService: AuthService,
    private readonly router: Router,
  ) {
    this.navItems = this.navigationService.getSections();
  }

  async logout(): Promise<void> {
    await this.authService.signOut();
    await this.router.navigate(['/login'], { queryParams: { mode: 'login' } });
  }

  get userFirstName(): string {
    const metadata = this.authService.currentUser?.user_metadata as Record<string, unknown> | undefined;
    const name = [
      metadata?.['nome'],
      metadata?.['first_name'],
      metadata?.['full_name'],
      metadata?.['name'],
    ].find((value): value is string => typeof value === 'string' && value.trim().length > 0)?.trim() ?? '';

    if (!name) {
      const email = this.authService.currentUser?.email ?? 'Usuário';
      return email.split('@')[0] ?? 'Usuário';
    }

    return name.split(/\s+/)[0] ?? name;
  }

  get userEmail(): string {
    return this.authService.currentUser?.email ?? '';
  }

  get userInitial(): string {
    return (this.userFirstName.charAt(0) || 'E').toUpperCase();
  }

  openUserAction(action: 'perfil' | 'anuncios' | 'historico'): void {
    if (action === 'perfil') {
      void this.router.navigate(['/perfil']);
      return;
    }

    if (action === 'anuncios') {
      void this.router.navigate(['/meus-anuncios']);
      return;
    }

    console.log('Menu do usuario:', action);
  }
}
