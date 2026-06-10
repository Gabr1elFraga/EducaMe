package com.educame.educame_api.application.usecase.autenticacao;

import com.educame.educame_api.application.dto.aluno.AlunoResponse;
import com.educame.educame_api.application.dto.autenticacao.CadastroAlunoRequest;
import com.educame.educame_api.application.dto.autenticacao.CadastroProfessorRequest;
import com.educame.educame_api.application.dto.professor.ProfessorResponse;
import com.educame.educame_api.application.mapper.DomainEntityMapper;
import com.educame.educame_api.domain.aluno.Aluno;
import com.educame.educame_api.domain.contract.ProfileCadastroRepository;
import com.educame.educame_api.domain.enums.GeneroTipo;
import com.educame.educame_api.domain.professor.Professor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;

@Service
public class AutenticacaoUseCase {
	private final ProfileCadastroRepository profileCadastroRepository;

	public AutenticacaoUseCase(ProfileCadastroRepository profileCadastroRepository) {
		this.profileCadastroRepository = profileCadastroRepository;
	}

	public AlunoResponse cadastrarAluno(CadastroAlunoRequest request) {
		var authUserId = requireAuthUserId(request.authUserId());

		profileCadastroRepository.findProfessorByAuthUserId(authUserId).ifPresent(existing -> {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Este usuario ja esta cadastrado como professor.");
		});

		var aluno = profileCadastroRepository.findAlunoByAuthUserId(authUserId).orElseGet(Aluno::new);
		aluno.setAuthUserId(authUserId);
		aluno.setNome(request.nome().trim());
		aluno.setSobrenome(request.sobrenome().trim());
		aluno.setDataNascimento(request.dataNascimento());
		if (aluno.getGenero() == null) {
			aluno.setGenero(GeneroTipo.NAO_INFORMADO);
		}

		return DomainEntityMapper.toResponse(profileCadastroRepository.saveAluno(aluno));
	}

	public ProfessorResponse cadastrarProfessor(CadastroProfessorRequest request) {
		var authUserId = requireAuthUserId(request.authUserId());

		profileCadastroRepository.findAlunoByAuthUserId(authUserId).ifPresent(existing -> {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Este usuario ja esta cadastrado como aluno.");
		});

		validateAdult(request.dataNascimento());

		var professor = profileCadastroRepository.findProfessorByAuthUserId(authUserId).orElseGet(Professor::new);
		professor.setAuthUserId(authUserId);
		professor.setNome(request.nome().trim());
		professor.setSobrenome(request.sobrenome().trim());
		professor.setCpf(normalizeCpf(request.cpf()));
		professor.setDataNascimento(request.dataNascimento());
		professor.setAtivo(true);

		return DomainEntityMapper.toResponse(profileCadastroRepository.saveProfessor(professor));
	}

	private UUID requireAuthUserId(UUID authUserId) {
		if (authUserId == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O usuario autenticado e obrigatorio.");
		}
		return authUserId;
	}

	private void validateAdult(LocalDate birthDate) {
		if (Period.between(birthDate, LocalDate.now()).getYears() < 18) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Professor menor de idade nao pode ministrar aulas.");
		}
	}

	private String normalizeCpf(String cpf) {
		return cpf.replaceAll("\\D", "");
	}
}
