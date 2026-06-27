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
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import {
  AnuncioAula,
  AnuncioAulaPayload,
  DisciplinaResumo,
} from './models/professor-profile.model';
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
    MatSelectModule,
    MatSlideToggleModule,
  ],
  templateUrl: './professor-profile.page.html',
  styleUrl: './professor-profile.page.css',
})
export class ProfessorProfilePageComponent implements OnInit {
  private readonly formBuilder = inject(FormBuilder);

  readonly adForm = this.formBuilder.nonNullable.group({
    disciplinaId: ['', [Validators.required]],
    titulo: ['', [Validators.required, Validators.maxLength(160)]],
    descricao: ['', [Validators.maxLength(4000)]],
    valorHora: [0, [Validators.required, Validators.min(0)]],
    modalidade: ['online', [Validators.required, Validators.maxLength(40)]],
    ativo: [true],
  });

  readonly availabilityForm = this.formBuilder.nonNullable.group({
    inicio: ['', [Validators.required]],
    fim: ['', [Validators.required]],
    observacao: ['', [Validators.maxLength(1000)]],
  });

  disciplinas: DisciplinaResumo[] = [];
  ads: AnuncioAula[] = [];
  selectedAd: AnuncioAula | null = null;
  editingAdId: string | null = null;
  loading = true;
  savingAd = false;
  savingAvailability = false;
  errorMessage = '';
  successMessage = '';

  constructor(private readonly professorProfileService: ProfessorProfileService) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.professorProfileService.listDisciplinas().subscribe({
      next: (disciplinas) => {
        this.disciplinas = disciplinas;
        this.loadAds();
      },
      error: (error) => {
        this.loading = false;
        this.errorMessage = this.normalizeError(error, 'Nao foi possivel carregar as disciplinas.');
      },
    });
  }

  loadAds(): void {
    this.professorProfileService.listMyAds().subscribe({
      next: (ads) => {
        this.ads = ads;
        this.selectedAd = this.selectedAd
          ? ads.find((ad) => ad.id === this.selectedAd?.id) ?? ads[0] ?? null
          : ads[0] ?? null;
        this.loading = false;
      },
      error: (error) => {
        this.loading = false;
        this.errorMessage = this.normalizeError(error, 'Nao foi possivel carregar seus anuncios.');
      },
    });
  }

  selectAd(ad: AnuncioAula): void {
    this.selectedAd = ad;
    this.successMessage = '';
    this.errorMessage = '';
  }

  editAd(ad: AnuncioAula): void {
    this.editingAdId = ad.id;
    this.selectedAd = ad;
    this.adForm.patchValue({
      disciplinaId: ad.disciplinaId,
      titulo: ad.titulo,
      descricao: ad.descricao ?? '',
      valorHora: ad.valorHora,
      modalidade: ad.modalidade,
      ativo: ad.ativo,
    });
    this.successMessage = '';
    this.errorMessage = '';
  }

  newAd(): void {
    this.editingAdId = null;
    this.adForm.reset({
      disciplinaId: this.disciplinas[0]?.id ?? '',
      titulo: '',
      descricao: '',
      valorHora: 0,
      modalidade: 'online',
      ativo: true,
    });
    this.successMessage = '';
    this.errorMessage = '';
  }

  saveAd(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (this.adForm.invalid) {
      this.adForm.markAllAsTouched();
      return;
    }

    this.savingAd = true;
    const payload = this.buildAdPayload();
    const request = this.editingAdId
      ? this.professorProfileService.updateAd(this.editingAdId, payload)
      : this.professorProfileService.createAd(payload);

    request.subscribe({
      next: (ad) => {
        this.upsertAd(ad);
        this.selectedAd = ad;
        this.editingAdId = ad.id;
        this.savingAd = false;
        this.successMessage = 'Anuncio salvo.';
      },
      error: (error) => {
        this.savingAd = false;
        this.errorMessage = this.normalizeError(error, 'Nao foi possivel salvar o anuncio.');
      },
    });
  }

  toggleAdStatus(ad: AnuncioAula): void {
    this.professorProfileService.updateAdStatus(ad.id, !ad.ativo).subscribe({
      next: (updated) => {
        this.upsertAd(updated);
        this.selectedAd = updated;
      },
      error: (error) => {
        this.errorMessage = this.normalizeError(error, 'Nao foi possivel alterar o status do anuncio.');
      },
    });
  }

  addAvailability(): void {
    if (!this.selectedAd) {
      this.errorMessage = 'Selecione um anuncio antes de cadastrar horario.';
      return;
    }

    this.errorMessage = '';
    this.successMessage = '';

    if (this.availabilityForm.invalid) {
      this.availabilityForm.markAllAsTouched();
      return;
    }

    const inicio = this.toIsoOffset(this.availabilityForm.controls.inicio.value);
    const fim = this.toIsoOffset(this.availabilityForm.controls.fim.value);

    if (!inicio || !fim) {
      this.errorMessage = 'Informe inicio e fim do horario.';
      return;
    }

    this.savingAvailability = true;
    const observacao = this.availabilityForm.controls.observacao.value.trim();
    this.professorProfileService.addAvailability(this.selectedAd.id, {
      inicio,
      fim,
      observacao: observacao.length > 0 ? observacao : null,
    }).subscribe({
      next: (updated) => {
        this.upsertAd(updated);
        this.selectedAd = updated;
        this.availabilityForm.reset({ inicio: '', fim: '', observacao: '' });
        this.savingAvailability = false;
        this.successMessage = 'Horario cadastrado.';
      },
      error: (error) => {
        this.savingAvailability = false;
        this.errorMessage = this.normalizeError(error, 'Nao foi possivel cadastrar o horario.');
      },
    });
  }

  removeAvailability(availabilityId: string): void {
    if (!this.selectedAd) {
      return;
    }

    this.professorProfileService.removeAvailability(this.selectedAd.id, availabilityId).subscribe({
      next: (updated) => {
        this.upsertAd(updated);
        this.selectedAd = updated;
        this.successMessage = 'Horario removido.';
      },
      error: (error) => {
        this.errorMessage = this.normalizeError(error, 'Nao foi possivel remover o horario.');
      },
    });
  }

  get activeAdsCount(): number {
    return this.ads.filter((ad) => ad.ativo).length;
  }

  trackById(_: number, item: { id: string }): string {
    return item.id;
  }

  private buildAdPayload(): AnuncioAulaPayload {
    const descricao = this.adForm.controls.descricao.value.trim();
    return {
      disciplinaId: this.adForm.controls.disciplinaId.value,
      titulo: this.adForm.controls.titulo.value.trim(),
      descricao: descricao.length > 0 ? descricao : null,
      valorHora: Number(this.adForm.controls.valorHora.value),
      modalidade: this.adForm.controls.modalidade.value.trim(),
      ativo: this.adForm.controls.ativo.value,
    };
  }

  private upsertAd(ad: AnuncioAula): void {
    const index = this.ads.findIndex((item) => item.id === ad.id);
    if (index >= 0) {
      this.ads = this.ads.map((item) => item.id === ad.id ? ad : item);
      return;
    }

    this.ads = [ad, ...this.ads];
  }

  private toIsoOffset(value: string): string | null {
    if (!value) {
      return null;
    }

    const date = new Date(value);
    return Number.isNaN(date.getTime()) ? null : date.toISOString();
  }

  private normalizeError(error: unknown, fallback: string): string {
    if (error instanceof HttpErrorResponse) {
      if (error.status === 401 || error.status === 403) {
        return 'Sua sessao nao foi reconhecida pelo backend. Entre novamente e tente abrir seus anuncios.';
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

    return fallback;
  }
}
