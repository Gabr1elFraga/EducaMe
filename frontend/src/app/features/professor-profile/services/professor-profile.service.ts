import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {
  ProfessorProfile,
  UpdateProfessorProfilePayload,
} from '../models/professor-profile.model';

@Injectable({
  providedIn: 'root',
})
export class ProfessorProfileService {
  constructor(private readonly http: HttpClient) {}

  getMyProfile(): Observable<ProfessorProfile> {
    return this.http.get<ProfessorProfile>('/v1/professores/me');
  }

  updateMyProfile(payload: UpdateProfessorProfilePayload): Observable<ProfessorProfile> {
    return this.http.put<ProfessorProfile>('/v1/professores/me', payload);
  }
}
