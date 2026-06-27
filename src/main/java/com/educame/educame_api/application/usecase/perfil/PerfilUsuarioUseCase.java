package com.educame.educame_api.application.usecase.perfil;

import com.educame.educame_api.application.dto.perfil.AtualizarPerfilUsuarioRequest;
import com.educame.educame_api.application.dto.perfil.PerfilUsuarioResponse;
import com.educame.educame_api.domain.contract.PessoaRepository;
import com.educame.educame_api.domain.contract.ProfessorRepository;
import com.educame.educame_api.domain.endereco.Endereco;
import com.educame.educame_api.domain.pessoa.Pessoa;
import com.educame.educame_api.domain.professor.Professor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class PerfilUsuarioUseCase {
	private final PessoaRepository pessoaRepository;
	private final ProfessorRepository professorRepository;

	public PerfilUsuarioUseCase(PessoaRepository pessoaRepository, ProfessorRepository professorRepository) {
		this.pessoaRepository = pessoaRepository;
		this.professorRepository = professorRepository;
	}

	public PerfilUsuarioResponse buscarMeuPerfil(UUID authUserId) {
		var pessoa = findPessoa(authUserId);
		var diploma = professorRepository.findByAuthUserId(authUserId)
			.map(Professor::getDiploma)
			.orElse(null);
		return toResponse(pessoa, diploma);
	}

	@Transactional
	public PerfilUsuarioResponse atualizarMeuPerfil(UUID authUserId, AtualizarPerfilUsuarioRequest request) {
		var pessoa = findPessoa(authUserId);
		pessoa.setNome(request.nome().trim());
		pessoa.setSobrenome(request.sobrenome().trim());
		pessoa.setDataNascimento(request.dataNascimento());
		pessoa.setGenero(request.genero());
		pessoa.setCpf(normalizeText(request.cpf()));
		pessoa.setFotoPerfil(normalizeText(request.fotoPerfil()));

		var savedPessoa = pessoaRepository.saveProfile(pessoa);
		var professor = professorRepository.findByAuthUserId(authUserId).orElseGet(() -> {
			var newProfessor = new Professor();
			newProfessor.setPessoa(savedPessoa);
			newProfessor.setAtivo(true);
			newProfessor.setStatusVerificacao("PENDENTE");
			return newProfessor;
		});
		professor.setPessoa(savedPessoa);
		professor.setDiploma(normalizeText(request.diploma()));
		var savedProfessor = professorRepository.save(professor);

		return toResponse(savedPessoa, savedProfessor.getDiploma());
	}

	private Pessoa findPessoa(UUID authUserId) {
		if (authUserId == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "O usuario autenticado e obrigatorio.");
		}
		return pessoaRepository.findByAuthUserId(authUserId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pessoa nao encontrada para o usuario autenticado."));
	}

	private String normalizeText(String value) {
		return value != null && !value.isBlank() ? value.trim() : null;
	}

	private PerfilUsuarioResponse toResponse(Pessoa pessoa, String diploma) {
		return new PerfilUsuarioResponse(
			pessoa.getId(),
			pessoa.getAuthUserId(),
			pessoa.getNome(),
			pessoa.getSobrenome(),
			pessoa.getDataNascimento(),
			pessoa.getGenero(),
			pessoa.getCpf(),
			pessoa.getFotoPerfil(),
			diploma,
			toEnderecoResponse(pessoa.getEndereco())
		);
	}

	private PerfilUsuarioResponse.EnderecoResponse toEnderecoResponse(Endereco endereco) {
		if (endereco == null) {
			return null;
		}
		return new PerfilUsuarioResponse.EnderecoResponse(
			endereco.getId(),
			endereco.getRua(),
			endereco.getNumero(),
			endereco.getComplemento(),
			endereco.getBairro(),
			endereco.getCidade(),
			endereco.getEstado(),
			endereco.getCep(),
			endereco.getPais()
		);
	}
}
