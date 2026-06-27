export interface DisciplinaResumo {
  id: string;
  nome: string;
  descricao: string | null;
}

export interface DisponibilidadeAnuncio {
  id: string;
  inicio: string;
  fim: string;
  status: 'DISPONIVEL' | 'RESERVADA' | 'BLOQUEADA' | string;
  observacao: string | null;
}

export interface AnuncioAula {
  id: string;
  professorId: string;
  disciplinaId: string;
  disciplinaNome: string;
  titulo: string;
  descricao: string | null;
  valorHora: number;
  modalidade: string;
  ativo: boolean;
  disponibilidades: DisponibilidadeAnuncio[];
}

export interface AnuncioAulaPayload {
  disciplinaId: string;
  titulo: string;
  descricao: string | null;
  valorHora: number;
  modalidade: string;
  ativo: boolean;
}

export interface DisponibilidadePayload {
  inicio: string;
  fim: string;
  observacao: string | null;
}
