package com.educame.educame_api.infrastructure.persistence.jpa;

import com.educame.educame_api.application.mapper.DomainEntityMapper;
import com.educame.educame_api.domain.contract.ProfessorRepository;
import com.educame.educame_api.domain.enums.GeneroTipo;
import com.educame.educame_api.domain.professor.Professor;
import com.educame.educame_api.infrastructure.persistence.jpa.repository.ProfessorJpaRepository;
import com.educame.educame_api.infrastructure.persistence.jdbc.PessoaJdbcRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ProfessorJpaRepositoryAdapter implements ProfessorRepository {
	private final ProfessorJpaRepository professorJpaRepository;
	private final DomainEntityMapper domainEntityMapper;
	private final PessoaJdbcRepository pessoaJdbcRepository;

	public ProfessorJpaRepositoryAdapter(
		ProfessorJpaRepository professorJpaRepository,
		DomainEntityMapper domainEntityMapper,
		PessoaJdbcRepository pessoaJdbcRepository
	) {
		this.professorJpaRepository = professorJpaRepository;
		this.domainEntityMapper = domainEntityMapper;
		this.pessoaJdbcRepository = pessoaJdbcRepository;
	}

	@Override
	public List<Professor> findAll() {
		return professorJpaRepository.findAll()
			.stream()
			.map(domainEntityMapper::toDomain)
			.toList();
	}

	@Override
	public Optional<Professor> findById(UUID id) {
		return professorJpaRepository.findById(id)
			.map(domainEntityMapper::toDomain);
	}

	@Override
	public Optional<Professor> findByAuthUserId(UUID authUserId) {
		return professorJpaRepository.findByPessoa_AuthUserId(authUserId)
			.map(domainEntityMapper::toDomain);
	}

	@Override
	public Professor save(Professor professor) {
		var entity = professor.getId() != null
			? professorJpaRepository.findById(professor.getId()).orElseGet(com.educame.educame_api.infrastructure.persistence.jpa.entity.ProfessorEntity::new)
			: new com.educame.educame_api.infrastructure.persistence.jpa.entity.ProfessorEntity();

		var existingPessoaId = entity.getPessoa() != null ? entity.getPessoa().getId() : null;
		var genero = professor.getPessoa() != null && professor.getPessoa().getGenero() != null
			? professor.getPessoa().getGenero()
			: GeneroTipo.NAO_INFORMADO;
		var pessoaId = pessoaJdbcRepository.upsertPessoa(
			existingPessoaId,
			professor.getAuthUserId(),
			professor.getNome(),
			professor.getSobrenome(),
			professor.getDataNascimento(),
			genero,
			professor.getEndereco() != null ? professor.getEndereco().getId() : null,
			professor.getCpf(),
			professor.getPessoa() != null ? professor.getPessoa().getFotoPerfil() : null
		);

		if (entity.getPessoa() == null) {
			entity.setPessoa(new com.educame.educame_api.infrastructure.persistence.jpa.entity.PessoaEntity());
		}
		entity.getPessoa().setId(pessoaId);
		entity.setAuthUserId(professor.getAuthUserId());
		entity.setNome(professor.getNome());
		entity.setSobrenome(professor.getSobrenome());
		entity.setDataNascimento(professor.getDataNascimento());
		entity.setGenero(genero);
		entity.setEndereco(
			professor.getEndereco() != null
				? domainEntityMapper.toEntity(professor.getEndereco())
				: entity.getPessoa().getEndereco()
		);
		entity.setCpf(professor.getCpf());
		entity.setBio(professor.getBio());
		entity.setAtivo(professor.isAtivo());
		entity.setDiploma(professor.getDiploma());
		entity.setStatusVerificacao(professor.getStatusVerificacao() != null ? professor.getStatusVerificacao() : "PENDENTE");
		entity.setValorHoraAula(professor.getValorHoraAula());

		return domainEntityMapper.toDomain(professorJpaRepository.save(entity));
	}

	@Override
	public Professor updateProfile(Professor professor) {
		var entity = professorJpaRepository.findById(professor.getId())
			.orElseThrow(() -> new IllegalArgumentException("Professor nao encontrado."));
		entity.setBio(professor.getBio());
		entity.setAtivo(professor.isAtivo());
		entity.setDiploma(professor.getDiploma());
		entity.setValorHoraAula(professor.getValorHoraAula());
		return domainEntityMapper.toDomain(professorJpaRepository.save(entity));
	}

	@Override
	public boolean existsById(UUID id) {
		return professorJpaRepository.existsById(id);
	}

	@Override
	public void deleteById(UUID id) {
		professorJpaRepository.deleteById(id);
	}
}
