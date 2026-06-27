package com.educame.educame_api.application.usecase.anuncio;

import com.educame.educame_api.application.dto.anuncio.AnuncioAulaRequest;
import com.educame.educame_api.application.dto.anuncio.AnuncioAulaResponse;
import com.educame.educame_api.application.dto.anuncio.AtualizarStatusAnuncioRequest;
import com.educame.educame_api.application.dto.anuncio.DisponibilidadeAnuncioRequest;
import com.educame.educame_api.domain.enums.DisponibilidadeStatus;
import com.educame.educame_api.infrastructure.persistence.jpa.entity.AnuncioAulaEntity;
import com.educame.educame_api.infrastructure.persistence.jpa.entity.DisponibilidadeEntity;
import com.educame.educame_api.infrastructure.persistence.jpa.repository.AnuncioAulaJpaRepository;
import com.educame.educame_api.infrastructure.persistence.jpa.repository.DisciplinaJpaRepository;
import com.educame.educame_api.infrastructure.persistence.jpa.repository.DisponibilidadeJpaRepository;
import com.educame.educame_api.infrastructure.persistence.jpa.repository.ProfessorJpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AnuncioAulaUseCase {
	private final AnuncioAulaJpaRepository anuncioRepository;
	private final ProfessorJpaRepository professorRepository;
	private final DisciplinaJpaRepository disciplinaRepository;
	private final DisponibilidadeJpaRepository disponibilidadeRepository;
	private final NamedParameterJdbcTemplate jdbcTemplate;

	public AnuncioAulaUseCase(
		AnuncioAulaJpaRepository anuncioRepository,
		ProfessorJpaRepository professorRepository,
		DisciplinaJpaRepository disciplinaRepository,
		DisponibilidadeJpaRepository disponibilidadeRepository,
		NamedParameterJdbcTemplate jdbcTemplate
	) {
		this.anuncioRepository = anuncioRepository;
		this.professorRepository = professorRepository;
		this.disciplinaRepository = disciplinaRepository;
		this.disponibilidadeRepository = disponibilidadeRepository;
		this.jdbcTemplate = jdbcTemplate;
	}

	public List<AnuncioAulaResponse> listarMeusAnuncios(UUID authUserId) {
		var professor = findProfessor(authUserId);
		return anuncioRepository.findByProfessor_IdOrderByCreatedAtDesc(professor.getId())
			.stream()
			.map(this::toResponse)
			.toList();
	}

	@Transactional
	public AnuncioAulaResponse criar(UUID authUserId, AnuncioAulaRequest request) {
		var professor = findProfessor(authUserId);
		var disciplina = disciplinaRepository.findById(request.disciplinaId())
			.filter(DisciplinaEntity -> DisciplinaEntity.isAtivo())
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Disciplina nao encontrada."));

		var anuncio = new AnuncioAulaEntity();
		anuncio.setProfessor(professor);
		anuncio.setDisciplina(disciplina);
		applyRequest(anuncio, request);
		return toResponse(anuncioRepository.save(anuncio));
	}

	@Transactional
	public AnuncioAulaResponse atualizar(UUID authUserId, UUID anuncioId, AnuncioAulaRequest request) {
		var anuncio = findOwnedAnuncio(authUserId, anuncioId);
		var disciplina = disciplinaRepository.findById(request.disciplinaId())
			.filter(DisciplinaEntity -> DisciplinaEntity.isAtivo())
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Disciplina nao encontrada."));

		anuncio.setDisciplina(disciplina);
		applyRequest(anuncio, request);
		return toResponse(anuncioRepository.save(anuncio));
	}

	@Transactional
	public AnuncioAulaResponse atualizarStatus(UUID authUserId, UUID anuncioId, AtualizarStatusAnuncioRequest request) {
		var anuncio = findOwnedAnuncio(authUserId, anuncioId);
		anuncio.setAtivo(request.ativo());
		return toResponse(anuncioRepository.save(anuncio));
	}

	@Transactional
	public AnuncioAulaResponse adicionarDisponibilidade(UUID authUserId, UUID anuncioId, DisponibilidadeAnuncioRequest request) {
		var anuncio = findOwnedAnuncio(authUserId, anuncioId);
		validatePeriodo(request.inicio(), request.fim());

		var disponibilidadeId = UUID.randomUUID();
		jdbcTemplate.update("""
			insert into disponibilidades (
				id,
				anuncio_id,
				professor_id,
				inicio,
				fim,
				status,
				observacao,
				created_at,
				updated_at
			)
			values (
				:id,
				:anuncioId,
				:professorId,
				:inicio,
				:fim,
				cast(:status as public.disponibilidade_status),
				:observacao,
				:now,
				:now
			)
			""",
			new MapSqlParameterSource()
				.addValue("id", disponibilidadeId)
				.addValue("anuncioId", anuncio.getId())
				.addValue("professorId", anuncio.getProfessor().getId())
				.addValue("inicio", request.inicio())
				.addValue("fim", request.fim())
				.addValue("status", "disponivel")
				.addValue("observacao", normalizeText(request.observacao()))
				.addValue("now", OffsetDateTime.now()));

		return toResponse(anuncioRepository.findById(anuncio.getId()).orElse(anuncio));
	}

	@Transactional
	public AnuncioAulaResponse removerDisponibilidade(UUID authUserId, UUID anuncioId, UUID disponibilidadeId) {
		var anuncio = findOwnedAnuncio(authUserId, anuncioId);
		var disponibilidade = disponibilidadeRepository.findById(disponibilidadeId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Disponibilidade nao encontrada."));
		if (disponibilidade.getAnuncio() == null || !anuncio.getId().equals(disponibilidade.getAnuncio().getId())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Disponibilidade nao pertence ao anuncio informado.");
		}
		if (disponibilidade.getStatus() == DisponibilidadeStatus.RESERVADA) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nao e possivel remover uma disponibilidade reservada.");
		}

		disponibilidadeRepository.delete(disponibilidade);
		return toResponse(anuncioRepository.findById(anuncio.getId()).orElse(anuncio));
	}

	private void applyRequest(AnuncioAulaEntity anuncio, AnuncioAulaRequest request) {
		anuncio.setTitulo(request.titulo().trim());
		anuncio.setDescricao(normalizeText(request.descricao()));
		anuncio.setValorHora(request.valorHora());
		anuncio.setModalidade(request.modalidade().trim());
		anuncio.setAtivo(request.ativo());
	}

	private AnuncioAulaEntity findOwnedAnuncio(UUID authUserId, UUID anuncioId) {
		var professor = findProfessor(authUserId);
		var anuncio = anuncioRepository.findById(anuncioId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Anuncio nao encontrado."));
		if (anuncio.getProfessor() == null || !professor.getId().equals(anuncio.getProfessor().getId())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Anuncio nao pertence ao professor autenticado.");
		}
		return anuncio;
	}

	private com.educame.educame_api.infrastructure.persistence.jpa.entity.ProfessorEntity findProfessor(UUID authUserId) {
		if (authUserId == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "O usuario autenticado e obrigatorio.");
		}
		return professorRepository.findByPessoa_AuthUserId(authUserId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Professor nao encontrado para o usuario autenticado."));
	}

	private void validatePeriodo(OffsetDateTime inicio, OffsetDateTime fim) {
		if (inicio == null || fim == null || !fim.isAfter(inicio)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Horario final deve ser posterior ao horario inicial.");
		}
	}

	private String normalizeText(String value) {
		return value != null && !value.isBlank() ? value.trim() : null;
	}

	private AnuncioAulaResponse toResponse(AnuncioAulaEntity anuncio) {
		return new AnuncioAulaResponse(
			anuncio.getId(),
			anuncio.getProfessor().getId(),
			anuncio.getDisciplina().getId(),
			anuncio.getDisciplina().getNome(),
			anuncio.getTitulo(),
			anuncio.getDescricao(),
			anuncio.getValorHora(),
			anuncio.getModalidade(),
			anuncio.isAtivo(),
			disponibilidadeRepository.findByAnuncio_IdOrderByInicioAsc(anuncio.getId())
				.stream()
				.map(this::toDisponibilidadeResponse)
				.toList()
		);
	}

	private AnuncioAulaResponse.DisponibilidadeResponse toDisponibilidadeResponse(DisponibilidadeEntity disponibilidade) {
		return new AnuncioAulaResponse.DisponibilidadeResponse(
			disponibilidade.getId(),
			disponibilidade.getInicio(),
			disponibilidade.getFim(),
			disponibilidade.getStatus() != null ? disponibilidade.getStatus().name() : null,
			disponibilidade.getObservacao()
		);
	}
}
