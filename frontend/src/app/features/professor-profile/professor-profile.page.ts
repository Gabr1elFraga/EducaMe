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
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { AuthService } from '../../core/services/auth.service';
import { ProfessorProfile } from './models/professor-profile.model';
import { ProfessorProfileService } from './services/professor-profile.service';

@Component({
  selector: 'app-professor-profile-page',
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
    MatSlideToggleModule,
  ],
  templateUrl: './professor-profile.page.html',
  styleUrl: './professor-profile.page.css',
})
export class ProfessorProfilePageComponent implements OnInit {
  private readonly formBuilder = inject(FormBuilder);

  readonly profileForm = this.formBuilder.nonNullable.group({
    bio: ['', [Validators.maxLength(4000)]],
    ativo: [true],
    diploma: ['', [Validators.maxLength(1000)]],
    valorHoraAula: [null as number | null, [Validators.min(0)]],
  });

  profile: ProfessorProfile | null = null;
  loading = true;
  saving = false;
  errorMessage = '';
  successMessage = '';

  constructor(
    private readonly professorProfileService: ProfessorProfileService,
    private readonly authService: AuthService,
  ) {}

  ngOnInit(): void {
    this.loadProfile();
  }

  loadProfile(): void {
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.professorProfileService.getMyProfile().subscribe({
      next: (profile) => {
        this.profile = profile;
        this.profileForm.patchValue({
          bio: profile.bio ?? '',
          ativo: profile.ativo,
          diploma: profile.diploma ?? '',
          valorHoraAula: profile.valorHoraAula,
        });
        this.loading = false;
      },
      error: (error: unknown) => {
        console.error('Falha ao carregar perfil professor', error);
        this.profile = this.buildDraftProfile();
        this.profileForm.patchValue({
          bio: '',
          ativo: true,
          diploma: '',
          valorHoraAula: null,
        });
        this.loading = false;
        this.errorMessage = '';
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
    const bio = this.profileForm.controls.bio.value.trim();
    const diploma = this.profileForm.controls.diploma.value.trim();

    this.professorProfileService.updateMyProfile({
      bio: bio.length > 0 ? bio : null,
      ativo: this.profileForm.controls.ativo.value,
      diploma: diploma.length > 0 ? diploma : null,
      valorHoraAula: this.profileForm.controls.valorHoraAula.value,
    }).subscribe({
      next: (profile) => {
        this.profile = profile;
        this.profileForm.patchValue({
          bio: profile.bio ?? '',
          ativo: profile.ativo,
          diploma: profile.diploma ?? '',
          valorHoraAula: profile.valorHoraAula,
        });
        this.successMessage = 'Perfil do professor atualizado.';
        this.saving = false;
      },
      error: (error: unknown) => {
        this.saving = false;
        this.errorMessage = this.normalizeError(error);
      },
    });
  }

  get fullName(): string {
    if (!this.profile) {
      return 'Professor';
    }

    return `${this.profile.nome} ${this.profile.sobrenome}`.trim();
  }

  get initials(): string {
    return this.fullName
      .split(/\s+/)
      .filter(Boolean)
      .slice(0, 2)
      .map((part) => part.charAt(0))
      .join('')
      .toUpperCase() || 'P';
  }

  private buildDraftProfile(): ProfessorProfile {
    const user = this.authService.currentUser;
    const metadata = user?.user_metadata as Record<string, unknown> | undefined;
    const fullName = this.resolveMetadataString(metadata, 'full_name', 'name');
    const nome = this.resolveMetadataString(metadata, 'nome', 'first_name') ?? this.firstFromFullName(fullName);
    const sobrenome = this.resolveMetadataString(metadata, 'sobrenome', 'last_name') ?? this.lastFromFullName(fullName);

    return {
      id: '',
      authUserId: user?.id ?? '',
      nome: nome ?? user?.email?.split('@')[0] ?? 'Professor',
      sobrenome: sobrenome ?? '',
      cpf: '',
      dataNascimento: this.resolveMetadataString(metadata, 'data_nascimento', 'dataNascimento') ?? '',
      bio: null,
      ativo: true,
      diploma: null,
      statusVerificacao: 'PENDENTE',
      valorHoraAula: null,
      endereco: null,
    };
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

  private firstFromFullName(fullName: string | null): string | null {
    return fullName?.split(/\s+/).filter(Boolean)[0] ?? null;
  }

  private lastFromFullName(fullName: string | null): string | null {
    const parts = fullName?.split(/\s+/).filter(Boolean) ?? [];
    return parts.length > 1 ? parts.slice(1).join(' ') : null;
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

    return 'Nao foi possivel carregar o perfil do professor.';
  }
}
