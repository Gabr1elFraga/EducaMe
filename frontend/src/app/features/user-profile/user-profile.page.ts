import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { GeneroTipo, UserProfile } from './models/user-profile.model';
import { UserProfileService } from './services/user-profile.service';

@Component({
  selector: 'app-user-profile-page',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatCardModule,
    MatDividerModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatProgressSpinnerModule,
    MatSelectModule,
  ],
  templateUrl: './user-profile.page.html',
  styleUrl: './user-profile.page.css',
})
export class UserProfilePageComponent implements OnInit {
  private readonly formBuilder = inject(FormBuilder);

  readonly genderOptions: Array<{ value: GeneroTipo; label: string }> = [
    { value: 'NAO_INFORMADO', label: 'Nao informado' },
    { value: 'FEMININO', label: 'Feminino' },
    { value: 'MASCULINO', label: 'Masculino' },
    { value: 'OUTRO', label: 'Outro' },
  ];

  readonly profileForm = this.formBuilder.nonNullable.group({
    nome: ['', [Validators.required, Validators.maxLength(120)]],
    sobrenome: ['', [Validators.required, Validators.maxLength(120)]],
    dataNascimento: ['', [Validators.required]],
    genero: ['NAO_INFORMADO' as GeneroTipo, [Validators.required]],
    cpf: ['', [Validators.maxLength(20)]],
    fotoPerfil: ['', [Validators.maxLength(1000)]],
    diploma: ['', [Validators.maxLength(1000)]],
  });

  profile: UserProfile | null = null;
  loading = true;
  saving = false;
  errorMessage = '';
  successMessage = '';

  constructor(private readonly userProfileService: UserProfileService) {}

  ngOnInit(): void {
    this.loadProfile();
  }

  loadProfile(): void {
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.userProfileService.getMyProfile().subscribe({
      next: (profile) => {
        this.profile = profile;
        this.patchForm(profile);
        this.loading = false;
      },
      error: (error: unknown) => {
        this.loading = false;
        this.errorMessage = this.normalizeError(error);
      },
    });
  }

  saveProfile(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (this.profileForm.invalid) {
      this.profileForm.markAllAsTouched();
      return;
    }

    this.saving = true;
    this.userProfileService.updateMyProfile({
      nome: this.profileForm.controls.nome.value.trim(),
      sobrenome: this.profileForm.controls.sobrenome.value.trim(),
      dataNascimento: this.profileForm.controls.dataNascimento.value,
      genero: this.profileForm.controls.genero.value,
      cpf: this.normalizeOptional(this.profileForm.controls.cpf.value),
      fotoPerfil: this.normalizeOptional(this.profileForm.controls.fotoPerfil.value),
      diploma: this.normalizeOptional(this.profileForm.controls.diploma.value),
    }).subscribe({
      next: (profile) => {
        this.profile = profile;
        this.patchForm(profile);
        this.successMessage = 'Perfil atualizado.';
        this.saving = false;
      },
      error: (error: unknown) => {
        this.saving = false;
        this.errorMessage = this.normalizeError(error);
      },
    });
  }

  get fullName(): string {
    return this.profile ? `${this.profile.nome} ${this.profile.sobrenome}`.trim() : 'Seu perfil';
  }

  get initials(): string {
    return this.fullName
      .split(/\s+/)
      .filter(Boolean)
      .slice(0, 2)
      .map((part) => part.charAt(0))
      .join('')
      .toUpperCase() || 'E';
  }

  private patchForm(profile: UserProfile): void {
    this.profileForm.patchValue({
      nome: profile.nome ?? '',
      sobrenome: profile.sobrenome ?? '',
      dataNascimento: profile.dataNascimento ?? '',
      genero: profile.genero ?? 'NAO_INFORMADO',
      cpf: profile.cpf ?? '',
      fotoPerfil: profile.fotoPerfil ?? '',
      diploma: profile.diploma ?? '',
    });
  }

  private normalizeOptional(value: string): string | null {
    const normalized = value.trim();
    return normalized.length > 0 ? normalized : null;
  }

  private normalizeError(error: unknown): string {
    if (error instanceof HttpErrorResponse) {
      if (error.status === 401 || error.status === 403) {
        return 'Sua sessao nao foi reconhecida pelo backend. Entre novamente e tente abrir o perfil.';
      }

      if (error.status === 404) {
        return 'Nao encontramos o cadastro base de pessoa para este usuario.';
      }

      if (typeof error.error?.message === 'string') {
        return error.error.message;
      }

      if (typeof error.error?.error === 'string') {
        return error.error.error;
      }
    }

    if (error instanceof Error) {
      return error.message;
    }

    return 'Nao foi possivel carregar o seu perfil.';
  }
}
