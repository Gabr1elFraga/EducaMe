import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatTabsModule } from '@angular/material/tabs';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login-page',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatTabsModule,
  ],
  templateUrl: './login.page.html',
  styleUrl: './login.page.css',
})
export class LoginPageComponent implements OnInit {
  readonly emailControl = new FormControl('', {
    nonNullable: true,
    validators: [Validators.required, Validators.email],
  });

  readonly passwordControl = new FormControl('', {
    nonNullable: true,
    validators: [Validators.required, Validators.minLength(6)],
  });

  activeTabIndex = 0;
  loading = false;
  signingUp = false;
  errorMessage = '';
  successMessage = '';

  private redirectUrl = '/';

  constructor(
    readonly authService: AuthService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.redirectUrl = this.route.snapshot.queryParamMap.get('redirect') ?? '/';
  }

  openLoginTab(): void {
    this.activeTabIndex = 0;
    this.clearFeedback();
  }

  openRegisterTab(): void {
    this.activeTabIndex = 1;
    this.clearFeedback();
  }

  async submit(event?: Event): Promise<void> {
    event?.preventDefault();
    event?.stopPropagation();

    this.clearFeedback();

    if (this.emailControl.invalid || this.passwordControl.invalid) {
      this.emailControl.markAsTouched();
      this.passwordControl.markAsTouched();
      return;
    }

    this.loading = true;

    try {
      const session = await this.authService.signIn(
        this.emailControl.value.trim(),
        this.passwordControl.value,
      );

      if (!session) {
        throw new Error('Falha ao autenticar a conta.');
      }

      const refreshedSession = await this.authService.refreshSession();

      if (!refreshedSession) {
        throw new Error('A sessão não foi mantida após o login.');
      }

      await this.router.navigateByUrl(this.redirectUrl, { replaceUrl: true });
    } catch (error) {
      console.error('Login failed', error);
      this.errorMessage = this.normalizeError(error);
    } finally {
      this.loading = false;
    }
  }

  async signUp(event?: Event): Promise<void> {
    event?.preventDefault();
    event?.stopPropagation();

    this.clearFeedback();

    if (this.emailControl.invalid || this.passwordControl.invalid) {
      this.emailControl.markAsTouched();
      this.passwordControl.markAsTouched();
      return;
    }

    this.signingUp = true;

    try {
      const result = await this.authService.signUp(
        this.emailControl.value.trim(),
        this.passwordControl.value,
      );

      if (result.session) {
        await this.authService.refreshSession();
        await this.router.navigateByUrl(this.redirectUrl, { replaceUrl: true });
        return;
      }

      this.successMessage =
        'Cadastro concluído. Se necessário, confirme seu e-mail para continuar.';
    } catch (error) {
      console.error('Sign up failed', error);
      this.errorMessage = this.normalizeError(error);
    } finally {
      this.signingUp = false;
    }
  }

  clearFeedback(): void {
    this.errorMessage = '';
    this.successMessage = '';
  }

  private normalizeError(error: unknown): string {
    if (!(error instanceof Error)) {
      return 'Não foi possível concluir a autenticação.';
    }

    const message = error.message.toLowerCase();

    if (
      message.includes('invalid login credentials') ||
      message.includes('invalid_credentials')
    ) {
      return 'E-mail ou senha inválidos.';
    }

    if (message.includes('email not confirmed')) {
      return 'Sua conta ainda não foi confirmada.';
    }

    if (message.includes('invalid api key')) {
      return 'Não foi possível autenticar no momento.';
    }

    return 'Não foi possível concluir a autenticação.';
  }
}
