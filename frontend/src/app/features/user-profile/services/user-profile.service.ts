import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { UpdateUserProfilePayload, UserProfile } from '../models/user-profile.model';

@Injectable({
  providedIn: 'root',
})
export class UserProfileService {
  constructor(private readonly http: HttpClient) {}

  getMyProfile(): Observable<UserProfile> {
    return this.http.get<UserProfile>('/v1/perfil/me');
  }

  updateMyProfile(payload: UpdateUserProfilePayload): Observable<UserProfile> {
    return this.http.put<UserProfile>('/v1/perfil/me', payload);
  }
}
