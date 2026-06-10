import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import {
  FormBuilder,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatTabsModule } from '@angular/material/tabs';
import { AuthService } from '../../core/services/auth.service';

type UserActor = 'aluno' | 'professor';

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
  private readonly formBuilder = inject(FormBuilder);

  readonly loginForm = this.formBuilder.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
  });

  readonly alunoRegisterForm = this.formBuilder.nonNullable.group({
    nome: ['', [Validators.required, Validators.maxLength(120)]],
    sobrenome: ['', [Validators.required, Validators.maxLength(120)]],
    dataNascimento: ['', [Validators.required]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
  });

  readonly professorRegisterForm = this.formBuilder.nonNullable.group({
    nome: ['', [Validators.required, Validators.maxLength(120)]],
    sobrenome: ['', [Validators.required, Validators.maxLength(120)]],
    cpf: ['', [Validators.required]],
    dataNascimento: ['', [Validators.required]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
  });

  selectedActor: UserActor | null = null;
  activeTabIndex = 0;
  loading = false;
  registering = false;
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

  selectActor(actor: UserActor): void {
    this.selectedActor = actor;
    this.clearFeedback();
  }

  resetActor(): void {
    this.selectedActor = null;
    this.activeTabIndex = 0;
    this.clearFeedback();
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

    if (this.selectedActor === null) {
      this.errorMessage = 'Selecione primeiro se voce e Aluno ou Professor.';
      return;
    }

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

      const refreshedSession = await this.authService.refreshSession();

      if (!refreshedSession) {
        throw new Error('A sessao nao foi mantida apos o login.');
      }

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

    if (this.selectedActor === null) {
      this.errorMessage = 'Selecione primeiro se voce e Aluno ou Professor.';
      return;
    }

    this.registering = true;

    try {
      if (this.selectedActor === 'professor') {
        const form = this.professorRegisterForm;

        if (form.invalid) {
          form.markAllAsTouched();
          return;
        }

        if (!this.isAdult(form.controls.dataNascimento.value)) {
          this.errorMessage = 'Professor menor de idade nao pode ministrar aulas.';
          return;
        }

        const result = await this.authService.signUp(
          form.controls.email.value.trim(),
          form.controls.password.value,
          {
            profile_type: 'professor',
            nome: form.controls.nome.value.trim(),
            sobrenome: form.controls.sobrenome.value.trim(),
            cpf: form.controls.cpf.value.trim(),
            data_nascimento: form.controls.dataNascimento.value,
          },
        );

        if (result.session) {
          await this.authService.refreshSession();
          await this.router.navigateByUrl(this.redirectUrl, { replaceUrl: true });
          return;
        }

        this.successMessage =
          'Cadastro concluido. Se necessario, confirme seu e-mail para continuar.';
        return;
      }

      const form = this.alunoRegisterForm;

      if (form.invalid) {
        form.markAllAsTouched();
        return;
      }

      const result = await this.authService.signUp(
        form.controls.email.value.trim(),
        form.controls.password.value,
        {
          profile_type: 'aluno',
          nome: form.controls.nome.value.trim(),
          sobrenome: form.controls.sobrenome.value.trim(),
          data_nascimento: form.controls.dataNascimento.value,
        },
      );

      if (result.session) {
        await this.authService.refreshSession();
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

  get selectedActorLabel(): string {
    if (this.selectedActor === 'professor') {
      return 'Professor';
    }

    if (this.selectedActor === 'aluno') {
      return 'Aluno';
    }

    return '';
  }

  get selectedActorIcon(): string {
    return this.selectedActor === 'professor' ? 'school' : 'person';
  }

  get selectedActorDescription(): string {
    return this.selectedActor === 'professor'
      ? 'Cadastro com nome, sobrenome, CPF e data de nascimento.'
      : 'Cadastro com nome, sobrenome e data de nascimento.';
  }

  get registerButtonLabel(): string {
    return this.selectedActor === 'professor' ? 'Criar professor' : 'Criar aluno';
  }

  private isAdult(dateValue: string): boolean {
    if (!dateValue) {
      return false;
    }

    const birthDate = new Date(`${dateValue}T00:00:00`);

    if (Number.isNaN(birthDate.getTime())) {
      return false;
    }

    const today = new Date();
    let age = today.getFullYear() - birthDate.getFullYear();
    const monthDiff = today.getMonth() - birthDate.getMonth();

    if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
      age--;
    }

    return age >= 18;
  }

  private normalizeError(error: unknown): string {
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

    if (message.includes('menor de idade')) {
      return 'Professor menor de idade nao pode ministrar aulas.';
    }

    if (message.includes('ja esta cadastrado como professor')) {
      return 'Este usuario ja possui cadastro de professor.';
    }

    if (message.includes('ja esta cadastrado como aluno')) {
      return 'Este usuario ja possui cadastro de aluno.';
    }

    if (message.includes('cpf')) {
      return 'CPF invalido.';
    }

    if (message.includes('invalid api key')) {
      return 'Nao foi possivel autenticar no momento.';
    }

    return error.message;
  }
}
