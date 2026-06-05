import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import {
  FormControl,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
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

  loading = false;
  signingUp = false;
  errorMessage = '';
  successMessage = '';
  configError = '';

  private redirectUrl = '/';

  constructor(
    readonly authService: AuthService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.redirectUrl = this.route.snapshot.queryParamMap.get('redirect') ?? '/';

    if (!this.authService.isConfigured()) {
      this.configError =
        'Configure a anon key do Supabase em frontend/src/app/core/config/supabase.config.ts antes de tentar entrar.';
    }
  }

  async submit(): Promise<void> {
    this.errorMessage = '';
    this.successMessage = '';

    if (this.emailControl.invalid || this.passwordControl.invalid) {
      this.emailControl.markAsTouched();
      this.passwordControl.markAsTouched();
      return;
    }

    this.loading = true;

    try {
      await this.authService.signIn(
        this.emailControl.value.trim(),
        this.passwordControl.value,
      );
      await this.router.navigateByUrl(this.redirectUrl);
    } catch (error) {
      this.errorMessage = this.normalizeError(error);
    } finally {
      this.loading = false;
    }
  }

  async signUp(): Promise<void> {
    this.errorMessage = '';
    this.successMessage = '';

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
        this.successMessage = 'Conta criada e autenticada com sucesso.';
        await this.router.navigateByUrl(this.redirectUrl);
        return;
      }

      this.successMessage =
        'Conta criada. Se o email confirmation estiver ativo, confirme o email para entrar.';
    } catch (error) {
      this.errorMessage = this.normalizeError(error);
    } finally {
      this.signingUp = false;
    }
  }

  private normalizeError(error: unknown): string {
    if (!(error instanceof Error)) {
      return 'Falha ao autenticar.';
    }

    const message = error.message.toLowerCase();

    if (message.includes('invalid login credentials') || message.includes('invalid_credentials')) {
      return 'Usuario ou senha invalidos.';
    }

    if (message.includes('email not confirmed')) {
      return 'Email ainda nao confirmado no Supabase.';
    }

    if (message.includes('invalid api key') || message.includes('invalid api key')) {
      return 'A chave publica do Supabase esta incorreta.';
    }

    return error.message;
  }
}
