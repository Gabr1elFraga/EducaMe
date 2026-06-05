package com.educame.educame_api.application.usecase.aluno;

import com.educame.educame_api.application.dto.aluno.AlunoRequest;
import com.educame.educame_api.application.dto.aluno.AlunoResponse;
import com.educame.educame_api.infrastructure.persistence.jpa.entity.AlunoEntity;
import com.educame.educame_api.infrastructure.persistence.jpa.entity.EnderecoEntity;
import com.educame.educame_api.infrastructure.persistence.jpa.repository.AlunoJpaRepository;
import com.educame.educame_api.infrastructure.persistence.jpa.repository.EnderecoJpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class AlunoUseCase {

	private final AlunoJpaRepository alunoRepository;
	private final EnderecoJpaRepository enderecoRepository;

	public AlunoUseCase(AlunoJpaRepository alunoRepository, EnderecoJpaRepository enderecoRepository) {
		this.alunoRepository = alunoRepository;
		this.enderecoRepository = enderecoRepository;
	}

	public List<AlunoResponse> list() {
		return alunoRepository.findAll()
			.stream()
			.map(this::toResponse)
			.toList();
	}

	public AlunoResponse findById(UUID id) {
		return alunoRepository.findById(id)
			.map(this::toResponse)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não encontrado."));
	}

	public AlunoResponse create(AlunoRequest request) {
		var entity = new AlunoEntity();
		entity.setAuthUserId(request.authUserId());
		entity.setNome(request.nome());
		entity.setSobrenome(request.sobrenome());
		entity.setDataNascimento(request.dataNascimento());
		entity.setGenero(request.genero());
		entity.setEndereco(resolveEndereco(request.enderecoId()));

		return toResponse(alunoRepository.save(entity));
	}

	public AlunoResponse update(UUID id, AlunoRequest request) {
		var entity = alunoRepository.findById(id)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não encontrado."));

		entity.setAuthUserId(request.authUserId());
		entity.setNome(request.nome());
		entity.setSobrenome(request.sobrenome());
		entity.setDataNascimento(request.dataNascimento());
		entity.setGenero(request.genero());
		entity.setEndereco(resolveEndereco(request.enderecoId()));

		return toResponse(alunoRepository.save(entity));
	}

	public void delete(UUID id) {
		if (!alunoRepository.existsById(id)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não encontrado.");
		}
		alunoRepository.deleteById(id);
	}

	private EnderecoEntity resolveEndereco(UUID enderecoId) {
		if (enderecoId == null) {
			return null;
		}
		return enderecoRepository.findById(enderecoId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Endereço não encontrado."));
	}

	private AlunoResponse toResponse(AlunoEntity entity) {
		var endereco = entity.getEndereco();
		return new AlunoResponse(
			entity.getId(),
			entity.getAuthUserId(),
			entity.getNome(),
			entity.getSobrenome(),
			entity.getDataNascimento(),
			entity.getGenero(),
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
}
