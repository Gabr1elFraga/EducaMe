package com.educame.educame_api.infrastructure.persistence.jpa.entity;

import com.educame.educame_api.domain.enums.PagamentoStatus;
import com.educame.educame_api.infrastructure.persistence.jpa.converter.PagamentoStatusConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "pagamentos")
public class PagamentoEntity extends BaseJpaEntity {
	@ManyToOne
	@JoinColumn(name = "aula_id")
	private AulaEntity aula;
	@ManyToOne(optional = false)
	@JoinColumn(name = "aluno_id", nullable = false)
	private AlunoEntity aluno;
	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal valor;
	@Convert(converter = PagamentoStatusConverter.class)
	@Column(nullable = false)
	private PagamentoStatus status;
	@Column(name = "data_vencimento")
	private LocalDate dataVencimento;
	@Column(name = "data_pagamento")
	private OffsetDateTime dataPagamento;
	@Column(name = "metodo_pagamento")
	private String metodoPagamento;
	@Column(name = "referencia_externa")
	private String referenciaExterna;

	public AulaEntity getAula() { return aula; }
	public void setAula(AulaEntity aula) { this.aula = aula; }
	public AlunoEntity getAluno() { return aluno; }
	public void setAluno(AlunoEntity aluno) { this.aluno = aluno; }
	public BigDecimal getValor() { return valor; }
	public void setValor(BigDecimal valor) { this.valor = valor; }
	public PagamentoStatus getStatus() { return status; }
	public void setStatus(PagamentoStatus status) { this.status = status; }
	public LocalDate getDataVencimento() { return dataVencimento; }
	public void setDataVencimento(LocalDate dataVencimento) { this.dataVencimento = dataVencimento; }
	public OffsetDateTime getDataPagamento() { return dataPagamento; }
	public void setDataPagamento(OffsetDateTime dataPagamento) { this.dataPagamento = dataPagamento; }
	public String getMetodoPagamento() { return metodoPagamento; }
	public void setMetodoPagamento(String metodoPagamento) { this.metodoPagamento = metodoPagamento; }
	public String getReferenciaExterna() { return referenciaExterna; }
	public void setReferenciaExterna(String referenciaExterna) { this.referenciaExterna = referenciaExterna; }
}
