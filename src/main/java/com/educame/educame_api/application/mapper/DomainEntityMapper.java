package com.educame.educame_api.application.mapper;

import com.educame.educame_api.application.dto.aluno.AlunoResponse;
import com.educame.educame_api.application.dto.professor.ProfessorResponse;
import com.educame.educame_api.domain.aluno.Aluno;
import com.educame.educame_api.domain.anuncio.AnuncioAula;
import com.educame.educame_api.domain.aula.Aula;
import com.educame.educame_api.domain.avaliacao.Avaliacao;
import com.educame.educame_api.domain.disciplina.Disciplina;
import com.educame.educame_api.domain.disponibilidade.Disponibilidade;
import com.educame.educame_api.domain.endereco.Endereco;
import com.educame.educame_api.domain.pagamento.Pagamento;
import com.educame.educame_api.domain.penalidade.Penalidade;
import com.educame.educame_api.domain.professor.Professor;
import com.educame.educame_api.infrastructure.persistence.jpa.entity.AlunoEntity;
import com.educame.educame_api.infrastructure.persistence.jpa.entity.AnuncioAulaEntity;
import com.educame.educame_api.infrastructure.persistence.jpa.entity.AulaEntity;
import com.educame.educame_api.infrastructure.persistence.jpa.entity.AvaliacaoEntity;
import com.educame.educame_api.infrastructure.persistence.jpa.entity.DisciplinaEntity;
import com.educame.educame_api.infrastructure.persistence.jpa.entity.DisponibilidadeEntity;
import com.educame.educame_api.infrastructure.persistence.jpa.entity.EnderecoEntity;
import com.educame.educame_api.infrastructure.persistence.jpa.entity.PagamentoEntity;
import com.educame.educame_api.infrastructure.persistence.jpa.entity.PenalidadeEntity;
import com.educame.educame_api.infrastructure.persistence.jpa.entity.ProfessorEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface DomainEntityMapper {
	Endereco toDomain(EnderecoEntity entity);

	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	EnderecoEntity toEntity(Endereco domain);

	Aluno toDomain(AlunoEntity entity);

	@Mapping(target = "pessoa", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	AlunoEntity toEntity(Aluno domain);

	@Mapping(target = "endereco", source = "endereco", qualifiedByName = "toAlunoEnderecoResponse")
	AlunoResponse toResponse(Aluno domain);

	@Named("toAlunoEnderecoResponse")
	AlunoResponse.EnderecoResponse toAlunoEnderecoResponse(Endereco domain);

	Professor toDomain(ProfessorEntity entity);

	@Mapping(target = "pessoa", ignore = true)
	@Mapping(target = "genero", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	ProfessorEntity toEntity(Professor domain);

	@Mapping(target = "endereco", source = "endereco", qualifiedByName = "toProfessorEnderecoResponse")
	ProfessorResponse toResponse(Professor domain);

	@Named("toProfessorEnderecoResponse")
	ProfessorResponse.EnderecoResponse toProfessorEnderecoResponse(Endereco domain);

	Disciplina toDomain(DisciplinaEntity entity);

	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	DisciplinaEntity toEntity(Disciplina domain);

	AnuncioAula toDomain(AnuncioAulaEntity entity);

	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	AnuncioAulaEntity toEntity(AnuncioAula domain);

	Disponibilidade toDomain(DisponibilidadeEntity entity);

	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	DisponibilidadeEntity toEntity(Disponibilidade domain);

	Aula toDomain(AulaEntity entity);

	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	AulaEntity toEntity(Aula domain);

	Pagamento toDomain(PagamentoEntity entity);

	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	PagamentoEntity toEntity(Pagamento domain);

	Avaliacao toDomain(AvaliacaoEntity entity);

	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	AvaliacaoEntity toEntity(Avaliacao domain);

	Penalidade toDomain(PenalidadeEntity entity);

	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	PenalidadeEntity toEntity(Penalidade domain);
}
