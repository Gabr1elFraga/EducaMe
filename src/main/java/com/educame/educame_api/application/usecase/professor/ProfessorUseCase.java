package com.educame.educame_api.application.usecase.professor;

import com.educame.educame_api.application.dto.professor.ProfessorRequest;
import com.educame.educame_api.application.dto.professor.ProfessorResponse;
import com.educame.educame_api.application.mapper.DomainEntityMapper;
import com.educame.educame_api.domain.contract.EnderecoRepository;
import com.educame.educame_api.domain.contract.ProfessorRepository;
import com.educame.educame_api.domain.endereco.Endereco;
import com.educame.educame_api.domain.professor.Professor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class ProfessorUseCase {
	private final ProfessorRepository professorRepository;
	private final EnderecoRepository enderecoRepository;

	public ProfessorUseCase(ProfessorRepository professorRepository, EnderecoRepository enderecoRepository) {
		this.professorRepository = professorRepository;
		this.enderecoRepository = enderecoRepository;
	}

	public List<ProfessorResponse> list() {
		return professorRepository.findAll()
			.stream()
			.map(DomainEntityMapper::toResponse)
			.toList();
	}

	public ProfessorResponse findById(UUID id) {
		return professorRepository.findById(id)
			.map(DomainEntityMapper::toResponse)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Professor nao encontrado."));
	}

	public ProfessorResponse create(ProfessorRequest request) {
		var professor = new Professor();
		professor.setAuthUserId(request.authUserId());
		professor.setNome(request.nome());
		professor.setSobrenome(request.sobrenome());
		professor.setBio(request.bio());
		professor.setEndereco(resolveEndereco(request.enderecoId()));
		professor.setAtivo(request.ativo());

		return DomainEntityMapper.toResponse(professorRepository.save(professor));
	}

	public ProfessorResponse update(UUID id, ProfessorRequest request) {
		var professor = professorRepository.findById(id)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Professor nao encontrado."));

		professor.setAuthUserId(request.authUserId());
		professor.setNome(request.nome());
		professor.setSobrenome(request.sobrenome());
		professor.setBio(request.bio());
		professor.setEndereco(resolveEndereco(request.enderecoId()));
		professor.setAtivo(request.ativo());

		return DomainEntityMapper.toResponse(professorRepository.save(professor));
	}

	public void delete(UUID id) {
		if (!professorRepository.existsById(id)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Professor nao encontrado.");
		}
		professorRepository.deleteById(id);
	}

	private Endereco resolveEndereco(UUID enderecoId) {
		if (enderecoId == null) {
			return null;
		}
		return enderecoRepository.findById(enderecoId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Endereco nao encontrado."));
	}
}
