package com.educame.educame_api.application.usecase.professor;

import com.educame.educame_api.application.dto.professor.AtualizarProfessorPerfilRequest;
import com.educame.educame_api.application.dto.professor.ProfessorResponse;
import com.educame.educame_api.application.mapper.DomainEntityMapper;
import com.educame.educame_api.domain.contract.ProfileCadastroRepository;
import com.educame.educame_api.domain.professor.Professor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class ProfessorPerfilUseCase {
	private final ProfileCadastroRepository profileCadastroRepository;
	private final DomainEntityMapper domainEntityMapper;

	public ProfessorPerfilUseCase(ProfileCadastroRepository profileCadastroRepository, DomainEntityMapper domainEntityMapper) {
		this.profileCadastroRepository = profileCadastroRepository;
		this.domainEntityMapper = domainEntityMapper;
	}

	public ProfessorResponse buscarPerfil(UUID authUserId, Professor professorFallback) {
		return domainEntityMapper.toResponse(ensureProfessorByAuthUserId(authUserId, professorFallback));
	}

	@Transactional
	public ProfessorResponse atualizarPerfil(UUID authUserId, Professor professorFallback, AtualizarProfessorPerfilRequest request) {
		var professor = ensureProfessorByAuthUserId(authUserId, professorFallback);
		professor.setBio(normalizeBio(request.bio()));
		professor.setAtivo(Boolean.TRUE.equals(request.ativo()));
		professor.setDiploma(normalizeText(request.diploma()));
		professor.setValorHoraAula(request.valorHoraAula());
		return domainEntityMapper.toResponse(profileCadastroRepository.updateProfessorProfile(professor));
	}

	private Professor ensureProfessorByAuthUserId(UUID authUserId, Professor professorFallback) {
		if (authUserId == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "O usuario autenticado e obrigatorio.");
		}

		try {
			return profileCadastroRepository.ensureProfessorByAuthUserId(authUserId, professorFallback);
		} catch (IllegalArgumentException ex) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cadastro de pessoa nao encontrado para este usuario.");
		}
	}

	private String normalizeBio(String bio) {
		return normalizeText(bio);
	}

	private String normalizeText(String value) {
		if (value == null) {
			return null;
		}

		var normalized = value.trim();
		return normalized.isEmpty() ? null : normalized;
	}
}
