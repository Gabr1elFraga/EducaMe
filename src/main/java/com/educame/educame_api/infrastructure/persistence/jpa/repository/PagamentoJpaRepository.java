package com.educame.educame_api.infrastructure.persistence.jpa.repository;

import com.educame.educame_api.infrastructure.persistence.jpa.entity.PagamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PagamentoJpaRepository extends JpaRepository<PagamentoEntity, UUID> {
	@Query(value = """
		select *
		from pagamentos
		where status = cast(:status as public.pagamento_status)
		""", nativeQuery = true)
	List<PagamentoEntity> findByStatusValue(@Param("status") String status);
}
