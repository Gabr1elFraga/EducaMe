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
  errorMessage = '';
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
      this.errorMessage =
        error instanceof Error ? error.message : 'Falha ao autenticar.';
    } finally {
      this.loading = false;
    }
  }
}
