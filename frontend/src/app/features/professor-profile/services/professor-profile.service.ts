import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {
  AnuncioAula,
  AnuncioAulaPayload,
  DisciplinaResumo,
  DisponibilidadePayload,
} from '../models/professor-profile.model';

@Injectable({
  providedIn: 'root',
})
export class ProfessorProfileService {
  constructor(private readonly http: HttpClient) {}

  listDisciplinas(): Observable<DisciplinaResumo[]> {
    return this.http.get<DisciplinaResumo[]>('/v1/disciplinas');
  }

  listMyAds(): Observable<AnuncioAula[]> {
    return this.http.get<AnuncioAula[]>('/v1/anuncios/me');
  }

  createAd(payload: AnuncioAulaPayload): Observable<AnuncioAula> {
    return this.http.post<AnuncioAula>('/v1/anuncios', payload);
  }

  updateAd(id: string, payload: AnuncioAulaPayload): Observable<AnuncioAula> {
    return this.http.put<AnuncioAula>(`/v1/anuncios/${id}`, payload);
  }

  updateAdStatus(id: string, ativo: boolean): Observable<AnuncioAula> {
    return this.http.patch<AnuncioAula>(`/v1/anuncios/${id}/status`, { ativo });
  }

  addAvailability(adId: string, payload: DisponibilidadePayload): Observable<AnuncioAula> {
    return this.http.post<AnuncioAula>(`/v1/anuncios/${adId}/disponibilidades`, payload);
  }

  removeAvailability(adId: string, availabilityId: string): Observable<AnuncioAula> {
    return this.http.delete<AnuncioAula>(`/v1/anuncios/${adId}/disponibilidades/${availabilityId}`);
  }
}
