package com.educame.educame_api.application.mapper;

import com.educame.educame_api.domain.aluno.Aluno;
import com.educame.educame_api.domain.aula.Aula;
import com.educame.educame_api.domain.avaliacao.Avaliacao;
import com.educame.educame_api.domain.disciplina.Disciplina;
import com.educame.educame_api.domain.disponibilidade.Disponibilidade;
import com.educame.educame_api.application.dto.aluno.AlunoResponse;
import com.educame.educame_api.application.dto.professor.ProfessorResponse;
import com.educame.educame_api.domain.endereco.Endereco;
import com.educame.educame_api.domain.enums.AulaStatus;
import com.educame.educame_api.domain.enums.DisponibilidadeStatus;
import com.educame.educame_api.domain.enums.GeneroTipo;
import com.educame.educame_api.domain.enums.PagamentoStatus;
import com.educame.educame_api.domain.pagamento.Pagamento;
import com.educame.educame_api.domain.penalidade.Penalidade;
import com.educame.educame_api.domain.professor.Professor;
import com.educame.educame_api.infrastructure.persistence.jpa.entity.AlunoEntity;
import com.educame.educame_api.infrastructure.persistence.jpa.entity.AulaEntity;
import com.educame.educame_api.infrastructure.persistence.jpa.entity.AvaliacaoEntity;
import com.educame.educame_api.infrastructure.persistence.jpa.entity.DisciplinaEntity;
import com.educame.educame_api.infrastructure.persistence.jpa.entity.DisponibilidadeEntity;
import com.educame.educame_api.infrastructure.persistence.jpa.entity.EnderecoEntity;
import com.educame.educame_api.infrastructure.persistence.jpa.entity.PagamentoEntity;
import com.educame.educame_api.infrastructure.persistence.jpa.entity.PenalidadeEntity;
import com.educame.educame_api.infrastructure.persistence.jpa.entity.ProfessorEntity;

public final class DomainEntityMapper {
	private DomainEntityMapper() {
	}

	public static Endereco toDomain(EnderecoEntity entity) {
		if (entity == null) {
			return null;
		}
		return new Endereco(
			entity.getId(),
			entity.getRua(),
			entity.getNumero(),
			entity.getComplemento(),
			entity.getBairro(),
			entity.getCidade(),
			entity.getEstado(),
			entity.getCep(),
			entity.getPais()
		);
	}

	public static EnderecoEntity toEntity(Endereco domain) {
		if (domain == null) {
			return null;
		}
		var entity = new EnderecoEntity();
		entity.setId(domain.getId());
		entity.setRua(domain.getRua());
		entity.setNumero(domain.getNumero());
		entity.setComplemento(domain.getComplemento());
		entity.setBairro(domain.getBairro());
		entity.setCidade(domain.getCidade());
		entity.setEstado(domain.getEstado());
		entity.setCep(domain.getCep());
		entity.setPais(domain.getPais());
		return entity;
	}

	public static Aluno toDomain(AlunoEntity entity) {
		if (entity == null) {
			return null;
		}
		return new Aluno(
			entity.getId(),
			entity.getAuthUserId(),
			entity.getNome(),
			entity.getSobrenome(),
			entity.getDataNascimento(),
			entity.getGenero(),
			toDomain(entity.getEndereco())
		);
	}

	public static AlunoEntity toEntity(Aluno domain) {
		if (domain == null) {
			return null;
		}
		var entity = new AlunoEntity();
		entity.setId(domain.getId());
		entity.setAuthUserId(domain.getAuthUserId());
		entity.setNome(domain.getNome());
		entity.setSobrenome(domain.getSobrenome());
		entity.setDataNascimento(domain.getDataNascimento());
		entity.setGenero(domain.getGenero());
		entity.setEndereco(toEntity(domain.getEndereco()));
		return entity;
	}

	public static AlunoResponse toResponse(Aluno domain) {
		if (domain == null) {
			return null;
		}
		var endereco = domain.getEndereco();
		return new AlunoResponse(
			domain.getId(),
			domain.getAuthUserId(),
			domain.getNome(),
			domain.getSobrenome(),
			domain.getDataNascimento(),
			domain.getGenero(),
			endereco == null ? null : new AlunoResponse.EnderecoResponse(
				endereco.getId(),
				endereco.getRua(),
				endereco.getNumero(),
				endereco.getComplemento(),
				endereco.getBairro(),
				endereco.getCidade(),
				endereco.getEstado(),
				endereco.getCep(),
				endereco.getPais()
			)
		);
	}

	public static Professor toDomain(ProfessorEntity entity) {
		if (entity == null) {
			return null;
		}
		return new Professor(
			entity.getId(),
			entity.getAuthUserId(),
			entity.getNome(),
			entity.getSobrenome(),
			entity.getBio(),
			toDomain(entity.getEndereco()),
			entity.isAtivo()
		);
	}

	public static ProfessorEntity toEntity(Professor domain) {
		if (domain == null) {
			return null;
		}
		var entity = new ProfessorEntity();
		entity.setId(domain.getId());
		entity.setAuthUserId(domain.getAuthUserId());
		entity.setNome(domain.getNome());
		entity.setSobrenome(domain.getSobrenome());
		entity.setBio(domain.getBio());
		entity.setEndereco(toEntity(domain.getEndereco()));
		entity.setAtivo(domain.isAtivo());
		return entity;
	}

	public static ProfessorResponse toResponse(Professor domain) {
		if (domain == null) {
			return null;
		}
		var endereco = domain.getEndereco();
		return new ProfessorResponse(
			domain.getId(),
			domain.getAuthUserId(),
			domain.getNome(),
			domain.getSobrenome(),
			domain.getBio(),
			endereco == null ? null : new ProfessorResponse.EnderecoResponse(
				endereco.getId(),
				endereco.getRua(),
				endereco.getNumero(),
				endereco.getComplemento(),
				endereco.getBairro(),
				endereco.getCidade(),
				endereco.getEstado(),
				endereco.getCep(),
				endereco.getPais()
			),
			domain.isAtivo()
		);
	}

	public static Disciplina toDomain(DisciplinaEntity entity) {
		if (entity == null) {
			return null;
		}
		return new Disciplina(entity.getId(), entity.getNome(), entity.getDescricao(), entity.isAtivo());
	}

	public static DisciplinaEntity toEntity(Disciplina domain) {
		if (domain == null) {
			return null;
		}
		var entity = new DisciplinaEntity();
		entity.setId(domain.getId());
		entity.setNome(domain.getNome());
		entity.setDescricao(domain.getDescricao());
		entity.setAtivo(domain.isAtivo());
		return entity;
	}

	public static Disponibilidade toDomain(DisponibilidadeEntity entity) {
		if (entity == null) {
			return null;
		}
		return new Disponibilidade(
			entity.getId(),
			toDomain(entity.getProfessor()),
			entity.getInicio(),
			entity.getFim(),
			entity.getStatus(),
			entity.getObservacao()
		);
	}

	public static DisponibilidadeEntity toEntity(Disponibilidade domain) {
		if (domain == null) {
			return null;
		}
		var entity = new DisponibilidadeEntity();
		entity.setId(domain.getId());
		entity.setProfessor(toEntity(domain.getProfessor()));
		entity.setInicio(domain.getInicio());
		entity.setFim(domain.getFim());
		entity.setStatus(domain.getStatus());
		entity.setObservacao(domain.getObservacao());
		return entity;
	}

	public static Aula toDomain(AulaEntity entity) {
		if (entity == null) {
			return null;
		}
		return new Aula(
			entity.getId(),
			toDomain(entity.getAluno()),
			toDomain(entity.getProfessor()),
			toDomain(entity.getDisciplina()),
			entity.getInicio(),
			entity.getFim(),
			entity.getStatus(),
			entity.getModalidade(),
			entity.getObservacao()
		);
	}

	public static AulaEntity toEntity(Aula domain) {
		if (domain == null) {
			return null;
		}
		var entity = new AulaEntity();
		entity.setId(domain.getId());
		entity.setAluno(toEntity(domain.getAluno()));
		entity.setProfessor(toEntity(domain.getProfessor()));
		entity.setDisciplina(toEntity(domain.getDisciplina()));
		entity.setInicio(domain.getInicio());
		entity.setFim(domain.getFim());
		entity.setStatus(domain.getStatus());
		entity.setModalidade(domain.getModalidade());
		entity.setObservacao(domain.getObservacao());
		return entity;
	}

	public static Pagamento toDomain(PagamentoEntity entity) {
		if (entity == null) {
			return null;
		}
		return new Pagamento(
			entity.getId(),
			toDomain(entity.getAula()),
			toDomain(entity.getAluno()),
			entity.getValor(),
			entity.getStatus(),
			entity.getDataVencimento(),
			entity.getDataPagamento(),
			entity.getMetodoPagamento(),
			entity.getReferenciaExterna()
		);
	}

	public static PagamentoEntity toEntity(Pagamento domain) {
		if (domain == null) {
			return null;
		}
		var entity = new PagamentoEntity();
		entity.setId(domain.getId());
		entity.setAula(toEntity(domain.getAula()));
		entity.setAluno(toEntity(domain.getAluno()));
		entity.setValor(domain.getValor());
		entity.setStatus(domain.getStatus());
		entity.setDataVencimento(domain.getDataVencimento());
		entity.setDataPagamento(domain.getDataPagamento());
		entity.setMetodoPagamento(domain.getMetodoPagamento());
		entity.setReferenciaExterna(domain.getReferenciaExterna());
		return entity;
	}

	public static Avaliacao toDomain(AvaliacaoEntity entity) {
		if (entity == null) {
			return null;
		}
		return new Avaliacao(
			entity.getId(),
			toDomain(entity.getAula()),
			toDomain(entity.getAluno()),
			toDomain(entity.getProfessor()),
			entity.getNota(),
			entity.getComentario()
		);
	}

	public static AvaliacaoEntity toEntity(Avaliacao domain) {
		if (domain == null) {
			return null;
		}
		var entity = new AvaliacaoEntity();
		entity.setId(domain.getId());
		entity.setAula(toEntity(domain.getAula()));
		entity.setAluno(toEntity(domain.getAluno()));
		entity.setProfessor(toEntity(domain.getProfessor()));
		entity.setNota(domain.getNota());
		entity.setComentario(domain.getComentario());
		return entity;
	}

	public static Penalidade toDomain(PenalidadeEntity entity) {
		if (entity == null) {
			return null;
		}
		return new Penalidade(
			entity.getId(),
			toDomain(entity.getAluno()),
			toDomain(entity.getAula()),
			entity.getMotivo(),
			entity.getValor(),
			entity.getAplicadaEm()
		);
	}

	public static PenalidadeEntity toEntity(Penalidade domain) {
		if (domain == null) {
			return null;
		}
		var entity = new PenalidadeEntity();
		entity.setId(domain.getId());
		entity.setAluno(toEntity(domain.getAluno()));
		entity.setAula(toEntity(domain.getAula()));
		entity.setMotivo(domain.getMotivo());
		entity.setValor(domain.getValor());
		entity.setAplicadaEm(domain.getAplicadaEm());
		return entity;
	}

	public static GeneroTipo toDomain(GeneroTipo value) {
		return value;
	}

	public static AulaStatus toDomain(AulaStatus value) {
		return value;
	}

	public static DisponibilidadeStatus toDomain(DisponibilidadeStatus value) {
		return value;
	}

	public static PagamentoStatus toDomain(PagamentoStatus value) {
		return value;
	}
}
