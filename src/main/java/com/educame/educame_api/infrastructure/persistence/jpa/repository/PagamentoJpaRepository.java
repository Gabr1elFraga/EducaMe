package com.educame.educame_api.infrastructure.persistence.jpa.repository;

import com.educame.educame_api.infrastructure.persistence.jpa.entity.PagamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface PagamentoJpaRepository extends JpaRepository<PagamentoEntity, UUID> {
	@Query(value = """
		select *
		from pagamentos
		where status = cast(:status as public.pagamento_status)
		""", nativeQuery = true)
	List<PagamentoEntity> findByStatusValue(@Param("status") String status);

	@Query(value = """
		select count(*)
		from pagamentos
		where status = cast(:status as public.pagamento_status)
		""", nativeQuery = true)
	long countByStatusValue(@Param("status") String status);

	@Query(value = """
		select coalesce(sum(valor), 0)
		from pagamentos
		where status = cast(:status as public.pagamento_status)
		""", nativeQuery = true)
	BigDecimal sumValorByStatusValue(@Param("status") String status);

	@Query(value = """
		select count(*)
		from pagamentos
		where status = cast(:status as public.pagamento_status)
		  and data_vencimento is not null
		  and data_vencimento < :today
		""", nativeQuery = true)
	long countOverdueByStatusValue(@Param("status") String status, @Param("today") LocalDate today);
}
