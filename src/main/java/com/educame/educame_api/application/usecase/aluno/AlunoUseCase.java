package com.educame.educame_api.application.usecase.aluno;

import com.educame.educame_api.application.dto.aluno.AlunoRequest;
import com.educame.educame_api.application.dto.aluno.AlunoResponse;
import com.educame.educame_api.application.mapper.DomainEntityMapper;
import com.educame.educame_api.domain.aluno.Aluno;
import com.educame.educame_api.domain.contract.AlunoRepository;
import com.educame.educame_api.domain.contract.EnderecoRepository;
import com.educame.educame_api.domain.endereco.Endereco;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class AlunoUseCase {
	private final AlunoRepository alunoRepository;
	private final EnderecoRepository enderecoRepository;
	private final DomainEntityMapper domainEntityMapper;

	public AlunoUseCase(AlunoRepository alunoRepository, EnderecoRepository enderecoRepository, DomainEntityMapper domainEntityMapper) {
		this.alunoRepository = alunoRepository;
		this.enderecoRepository = enderecoRepository;
		this.domainEntityMapper = domainEntityMapper;
	}

	public List<AlunoResponse> list() {
		return alunoRepository.findAll()
			.stream()
			.map(domainEntityMapper::toResponse)
			.toList();
	}

	public AlunoResponse findById(UUID id) {
		return alunoRepository.findById(id)
			.map(domainEntityMapper::toResponse)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não encontrado."));
	}

	public AlunoResponse create(AlunoRequest request) {
		var aluno = new Aluno();
		aluno.setAuthUserId(request.authUserId());
		aluno.setNome(request.nome());
		aluno.setSobrenome(request.sobrenome());
		aluno.setDataNascimento(request.dataNascimento());
		aluno.setGenero(request.genero());
		aluno.setEndereco(resolveEndereco(request.enderecoId()));

		return domainEntityMapper.toResponse(alunoRepository.save(aluno));
	}

	public AlunoResponse update(UUID id, AlunoRequest request) {
		var aluno = alunoRepository.findById(id)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não encontrado."));

		aluno.setAuthUserId(request.authUserId());
		aluno.setNome(request.nome());
		aluno.setSobrenome(request.sobrenome());
		aluno.setDataNascimento(request.dataNascimento());
		aluno.setGenero(request.genero());
		aluno.setEndereco(resolveEndereco(request.enderecoId()));

		return domainEntityMapper.toResponse(alunoRepository.save(aluno));
	}

	public void delete(UUID id) {
		if (!alunoRepository.existsById(id)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não encontrado.");
		}
		alunoRepository.deleteById(id);
	}

	private Endereco resolveEndereco(UUID enderecoId) {
		if (enderecoId == null) {
			return null;
		}
		return enderecoRepository.findById(enderecoId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Endereço não encontrado."));
	}
}
