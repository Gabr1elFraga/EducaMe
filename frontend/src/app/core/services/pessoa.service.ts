import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { firstValueFrom } from 'rxjs';

export interface CadastroPessoaPayload {
  authUserId: string;
  nome: string;
  sobrenome: string;
  dataNascimento: string;
  genero: 'FEMININO' | 'MASCULINO' | 'OUTRO';
}

@Injectable({
  providedIn: 'root',
})
export class PessoaService {
  private readonly http = inject(HttpClient);

  async registrarPessoa(payload: CadastroPessoaPayload): Promise<void> {
    await firstValueFrom(this.http.post<void>('/v1/autenticacao/pessoas', payload));
  }
}
