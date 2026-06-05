import { Injectable } from '@angular/core';
import {
  AuthChangeEvent,
  Session,
  SupabaseClient,
  User,
  createClient,
} from '@supabase/supabase-js';
import { BehaviorSubject } from 'rxjs';
import { SUPABASE_CONFIG } from '../config/supabase.config';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly client: SupabaseClient;
  private readonly sessionSubject = new BehaviorSubject<Session | null | undefined>(
    undefined,
  );

  readonly session$ = this.sessionSubject.asObservable();

  constructor() {
    this.client = createClient(SUPABASE_CONFIG.url, SUPABASE_CONFIG.anonKey, {
      auth: {
        autoRefreshToken: true,
        detectSessionInUrl: false,
        persistSession: true,
      },
    });

    void this.loadSession();

    this.client.auth.onAuthStateChange(
      (event: AuthChangeEvent, session: Session | null) => {
        if (session) {
          this.sessionSubject.next(session);
          return;
        }

        if (event === 'SIGNED_OUT' || this.sessionSubject.value === undefined) {
          this.sessionSubject.next(null);
        }
      },
    );
  }

  get currentSession(): Session | null | undefined {
    return this.sessionSubject.value;
  }

  get currentUser(): User | null {
    return this.sessionSubject.value?.user ?? null;
  }

  get accessToken(): string | null {
    return this.sessionSubject.value?.access_token ?? null;
  }

  isConfigured(): boolean {
    return !SUPABASE_CONFIG.anonKey.includes('REPLACE_WITH_SUPABASE_ANON_KEY');
  }

  async signIn(email: string, password: string): Promise<Session> {
    const { data, error } = await this.client.auth.signInWithPassword({
      email,
      password,
    });

    if (error) {
      throw new Error(error.message);
    }

    if (!data.session) {
      throw new Error('Supabase nao retornou uma sessao apos o login.');
    }

    this.sessionSubject.next(data.session);
    return data.session;
  }

  async signUp(email: string, password: string): Promise<{
    session: Session | null;
    user: User | null;
  }> {
    const { data, error } = await this.client.auth.signUp({
      email,
      password,
    });

    if (error) {
      throw new Error(error.message);
    }

    if (data.session) {
      this.sessionSubject.next(data.session);
    }

    return {
      session: data.session,
      user: data.user,
    };
  }

  async signOut(): Promise<void> {
    const { error } = await this.client.auth.signOut();

    if (error) {
      throw new Error(error.message);
    }

    this.sessionSubject.next(null);
  }

  async loadSession(): Promise<void> {
    const { data, error } = await this.client.auth.getSession();

    if (error) {
      if (this.sessionSubject.value === undefined) {
        this.sessionSubject.next(null);
      }
      return;
    }

    if (data.session || this.sessionSubject.value === undefined || this.sessionSubject.value === null) {
      this.sessionSubject.next(data.session);
    }
  }
}
