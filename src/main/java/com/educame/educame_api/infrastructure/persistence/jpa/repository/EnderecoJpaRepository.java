package com.educame.educame_api.infrastructure.persistence.jpa.repository;

import com.educame.educame_api.infrastructure.persistence.jpa.entity.EnderecoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EnderecoJpaRepository extends JpaRepository<EnderecoEntity, UUID> {
}
