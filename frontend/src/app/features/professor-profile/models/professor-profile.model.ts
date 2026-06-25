export interface ProfessorEndereco {
  id: string;
  rua: string;
  numero: string;
  complemento: string | null;
  bairro: string;
  cidade: string;
  estado: string;
  cep: string;
  pais: string;
}

export interface ProfessorProfile {
  id: string;
  authUserId: string;
  nome: string;
  sobrenome: string;
  cpf: string;
  dataNascimento: string;
  bio: string | null;
  ativo: boolean;
  diploma: string | null;
  statusVerificacao: string | null;
  valorHoraAula: number | null;
  endereco: ProfessorEndereco | null;
}

export interface UpdateProfessorProfilePayload {
  bio: string | null;
  ativo: boolean;
  diploma: string | null;
  valorHoraAula: number | null;
}
