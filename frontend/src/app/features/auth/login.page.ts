import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject } from '@angular/core';
import {
  FormBuilder,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { AuthService } from '../../core/services/auth.service';
import { PessoaService } from '../../core/services/pessoa.service';

type AuthMode = 'signup' | 'login';
type CadastroGenero = 'FEMININO' | 'MASCULINO' | 'OUTRO';

@Component({
  selector: 'app-login-page',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatSelectModule,
    RouterLink,
  ],
  templateUrl: './login.page.html',
  styleUrl: './login.page.css',
})
export class LoginPageComponent implements OnInit {
  private readonly formBuilder = inject(FormBuilder);

  authMode: AuthMode = 'signup';

  readonly loginForm = this.formBuilder.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
  });

  readonly signupForm = this.formBuilder.nonNullable.group({
    nome: ['', [Validators.required, Validators.maxLength(120)]],
    sobrenome: ['', [Validators.required, Validators.maxLength(120)]],
    dataNascimento: ['', [Validators.required]],
    genero: [null as CadastroGenero | null, [Validators.required]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
  });

  readonly genderOptions: Array<{ value: CadastroGenero; label: string }> = [
    { value: 'FEMININO', label: 'Feminino' },
    { value: 'MASCULINO', label: 'Masculino' },
    { value: 'OUTRO', label: 'Outro' },
  ];

  loading = false;
  registering = false;
  errorMessage = '';
  successMessage = '';

  private redirectUrl = '/';

  constructor(
    readonly authService: AuthService,
    private readonly pessoaService: PessoaService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    const queryParams = this.route.snapshot.queryParamMap;

    this.redirectUrl = queryParams.get('redirect') ?? '/';
    this.authMode = queryParams.get('mode') === 'login' ? 'login' : 'signup';
  }

  openLoginTab(): void {
    this.authMode = 'login';
    this.clearFeedback();
  }

  openRegisterTab(): void {
    this.authMode = 'signup';
    this.clearFeedback();
  }

  async submit(event?: Event): Promise<void> {
    event?.preventDefault();
    event?.stopPropagation();

    if (this.authMode === 'signup') {
      await this.register(event);
      return;
    }

    await this.login(event);
  }

  async login(event?: Event): Promise<void> {
    event?.preventDefault();
    event?.stopPropagation();

    this.clearFeedback();

    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.loading = true;

    try {
      const session = await this.authService.signIn(
        this.loginForm.controls.email.value.trim(),
        this.loginForm.controls.password.value,
      );

      if (!session) {
        throw new Error('Falha ao autenticar a conta.');
      }

      await this.ensurePessoaFromCurrentUser();
      await this.router.navigateByUrl(this.redirectUrl, { replaceUrl: true });
    } catch (error) {
      console.error('Login failed', error);
      this.errorMessage = this.normalizeError(error);
    } finally {
      this.loading = false;
    }
  }

  async register(event?: Event): Promise<void> {
    event?.preventDefault();
    event?.stopPropagation();

    this.clearFeedback();

    if (this.signupForm.invalid) {
      this.signupForm.markAllAsTouched();
      return;
    }

    this.registering = true;

    try {
      const form = this.signupForm;
      const nome = form.controls.nome.value.trim();
      const sobrenome = form.controls.sobrenome.value.trim();
      const dataNascimento = form.controls.dataNascimento.value;
      const genero = form.controls.genero.value;
      const email = form.controls.email.value.trim();
      const password = form.controls.password.value;

      if (!genero) {
        form.controls.genero.markAsTouched();
        return;
      }

      const result = await this.authService.signUp(email, password, {
        nome,
        sobrenome,
        data_nascimento: dataNascimento,
        genero,
      });

      if (result.session) {
        await this.registrarPessoa(nome, sobrenome, dataNascimento, genero);
        await this.router.navigateByUrl(this.redirectUrl, { replaceUrl: true });
        return;
      }

      this.successMessage =
        'Cadastro concluido. Se necessario, confirme seu e-mail para continuar.';
    } catch (error) {
      console.error('Sign up failed', error);
      this.errorMessage = this.normalizeError(error);
    } finally {
      this.registering = false;
    }
  }

  clearFeedback(): void {
    this.errorMessage = '';
    this.successMessage = '';
  }

  get pageTitle(): string {
    return this.authMode === 'signup' ? 'Crie sua conta' : 'Entre na plataforma';
  }

  get pageSubtitle(): string {
    return this.authMode === 'signup'
      ? 'Cadastre-se com poucos dados e comece a usar a plataforma.'
      : 'Acesse sua conta com e-mail e senha.';
  }

  private async ensurePessoaFromCurrentUser(): Promise<void> {
    const metadata = this.authService.currentUser?.user_metadata as Record<string, unknown> | undefined;
    const nome = this.resolveMetadataString(metadata, 'nome', 'first_name');
    const sobrenome = this.resolveMetadataString(metadata, 'sobrenome', 'last_name');
    const dataNascimento = this.resolveMetadataString(metadata, 'data_nascimento', 'dataNascimento');
    const genero = this.resolveMetadataGenero(metadata);

    if (!nome || !sobrenome || !dataNascimento || !genero) {
      return;
    }

    await this.registrarPessoa(nome, sobrenome, dataNascimento, genero);
  }

  private async registrarPessoa(
    nome: string,
    sobrenome: string,
    dataNascimento: string,
    genero: CadastroGenero,
  ): Promise<void> {
    const authUserId = this.authService.currentUser?.id;

    if (!authUserId) {
      throw new Error('O usuario autenticado e obrigatorio para criar o perfil.');
    }

    await this.pessoaService.registrarPessoa({
      authUserId,
      nome,
      sobrenome,
      dataNascimento,
      genero,
    });
  }

  private resolveMetadataString(
    metadata: Record<string, unknown> | undefined,
    ...keys: string[]
  ): string | null {
    for (const key of keys) {
      const value = metadata?.[key];

      if (typeof value === 'string' && value.trim().length > 0) {
        return value.trim();
      }
    }

    return null;
  }

  private resolveMetadataGenero(metadata: Record<string, unknown> | undefined): CadastroGenero | null {
    const value = this.resolveMetadataString(metadata, 'genero', 'gender');
    return value === 'FEMININO' || value === 'MASCULINO' || value === 'OUTRO' ? value : null;
  }

  private normalizeError(error: unknown): string {
    if (error instanceof HttpErrorResponse) {
      if (typeof error.error?.message === 'string') {
        return error.error.message;
      }

      if (typeof error.error?.error === 'string') {
        return error.error.error;
      }

      return error.message || 'Nao foi possivel concluir a autenticacao.';
    }

    if (!(error instanceof Error)) {
      return 'Nao foi possivel concluir a autenticacao.';
    }

    const message = error.message.toLowerCase();

    if (
      message.includes('invalid login credentials') ||
      message.includes('invalid_credentials')
    ) {
      return 'E-mail ou senha invalidos.';
    }

    if (message.includes('email not confirmed')) {
      return 'Sua conta ainda nao foi confirmada.';
    }

    if (message.includes('invalid api key')) {
      return 'Nao foi possivel autenticar no momento.';
    }

    return error.message;
  }
}
