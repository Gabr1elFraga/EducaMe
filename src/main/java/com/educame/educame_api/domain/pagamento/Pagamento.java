package com.educame.educame_api.domain.pagamento;

import com.educame.educame_api.domain.aluno.Aluno;
import com.educame.educame_api.domain.aula.Aula;
import com.educame.educame_api.domain.enums.PagamentoStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

public class Pagamento {
	private UUID id;
	private Aula aula;
	private Aluno aluno;
	private BigDecimal valor;
	private PagamentoStatus status;
	private LocalDate dataVencimento;
	private OffsetDateTime dataPagamento;
	private String metodoPagamento;
	private String referenciaExterna;

	public Pagamento() {
	}

	public Pagamento(UUID id, Aula aula, Aluno aluno, BigDecimal valor, PagamentoStatus status, LocalDate dataVencimento, OffsetDateTime dataPagamento, String metodoPagamento, String referenciaExterna) {
		this.id = id;
		this.aula = aula;
		this.aluno = aluno;
		this.valor = valor;
		this.status = status;
		this.dataVencimento = dataVencimento;
		this.dataPagamento = dataPagamento;
		this.metodoPagamento = metodoPagamento;
		this.referenciaExterna = referenciaExterna;
	}

	public UUID getId() { return id; }
	public void setId(UUID id) { this.id = id; }
	public Aula getAula() { return aula; }
	public void setAula(Aula aula) { this.aula = aula; }
	public Aluno getAluno() { return aluno; }
	public void setAluno(Aluno aluno) { this.aluno = aluno; }
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Pagamento pagamento = (Pagamento) o;
		return Objects.equals(id, pagamento.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
