export type GeneroTipo = 'FEMININO' | 'MASCULINO' | 'OUTRO' | 'NAO_INFORMADO';

export interface UserProfileAddress {
  id: string;
  rua: string | null;
  numero: string | null;
  complemento: string | null;
  bairro: string | null;
  cidade: string | null;
  estado: string | null;
  cep: string | null;
  pais: string | null;
}

export interface UserProfile {
  id: string;
  authUserId: string;
  nome: string;
  sobrenome: string;
  dataNascimento: string;
  genero: GeneroTipo;
  cpf: string | null;
  fotoPerfil: string | null;
  diploma: string | null;
  endereco: UserProfileAddress | null;
}

export interface UpdateUserProfilePayload {
  nome: string;
  sobrenome: string;
  dataNascimento: string;
  genero: GeneroTipo;
  cpf: string | null;
  fotoPerfil: string | null;
  diploma: string | null;
}
