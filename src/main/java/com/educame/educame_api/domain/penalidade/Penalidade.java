package com.educame.educame_api.domain.penalidade;

import com.educame.educame_api.domain.aluno.Aluno;
import com.educame.educame_api.domain.aula.Aula;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

public class Penalidade {
	private UUID id;
	private Aluno aluno;
	private Aula aula;
	private String motivo;
	private BigDecimal valor;
	private OffsetDateTime aplicadaEm;

	public Penalidade() {
	}

	public Penalidade(UUID id, Aluno aluno, Aula aula, String motivo, BigDecimal valor, OffsetDateTime aplicadaEm) {
		this.id = id;
		this.aluno = aluno;
		this.aula = aula;
		this.motivo = motivo;
		this.valor = valor;
		this.aplicadaEm = aplicadaEm;
	}

	public UUID getId() { return id; }
	public void setId(UUID id) { this.id = id; }
	public Aluno getAluno() { return aluno; }
	public void setAluno(Aluno aluno) { this.aluno = aluno; }
	public Aula getAula() { return aula; }
	public void setAula(Aula aula) { this.aula = aula; }
	public String getMotivo() { return motivo; }
	public void setMotivo(String motivo) { this.motivo = motivo; }
	public BigDecimal getValor() { return valor; }
	public void setValor(BigDecimal valor) { this.valor = valor; }
	public OffsetDateTime getAplicadaEm() { return aplicadaEm; }
	public void setAplicadaEm(OffsetDateTime aplicadaEm) { this.aplicadaEm = aplicadaEm; }

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Penalidade that = (Penalidade) o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
