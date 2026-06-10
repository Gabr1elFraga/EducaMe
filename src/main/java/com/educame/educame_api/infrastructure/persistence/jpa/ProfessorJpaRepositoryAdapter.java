package com.educame.educame_api.infrastructure.persistence.jpa;

import com.educame.educame_api.application.mapper.DomainEntityMapper;
import com.educame.educame_api.domain.contract.ProfessorRepository;
import com.educame.educame_api.domain.professor.Professor;
import com.educame.educame_api.infrastructure.persistence.jpa.repository.ProfessorJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ProfessorJpaRepositoryAdapter implements ProfessorRepository {
	private final ProfessorJpaRepository professorJpaRepository;

	public ProfessorJpaRepositoryAdapter(ProfessorJpaRepository professorJpaRepository) {
		this.professorJpaRepository = professorJpaRepository;
	}

	@Override
	public List<Professor> findAll() {
		return professorJpaRepository.findAll()
			.stream()
			.map(DomainEntityMapper::toDomain)
			.toList();
	}

	@Override
	public Optional<Professor> findById(UUID id) {
		return professorJpaRepository.findById(id)
			.map(DomainEntityMapper::toDomain);
	}

	@Override
	public Optional<Professor> findByAuthUserId(UUID authUserId) {
		return professorJpaRepository.findByAuthUserId(authUserId)
			.map(DomainEntityMapper::toDomain);
	}

	@Override
	public Professor save(Professor professor) {
		return DomainEntityMapper.toDomain(professorJpaRepository.save(DomainEntityMapper.toEntity(professor)));
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
