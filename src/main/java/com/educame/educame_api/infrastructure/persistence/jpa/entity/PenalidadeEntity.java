package com.educame.educame_api.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "penalidades")
public class PenalidadeEntity extends BaseJpaEntity {
	@ManyToOne(optional = false)
	@JoinColumn(name = "aluno_id", nullable = false)
	private AlunoEntity aluno;
	@ManyToOne
	@JoinColumn(name = "aula_id")
	private AulaEntity aula;
	@Column(nullable = false)
	private String motivo;
	@Column(precision = 12, scale = 2)
	private BigDecimal valor;
	@Column(name = "aplicada_em", nullable = false)
	private OffsetDateTime aplicadaEm;

	public AlunoEntity getAluno() { return aluno; }
	public void setAluno(AlunoEntity aluno) { this.aluno = aluno; }
	public AulaEntity getAula() { return aula; }
	public void setAula(AulaEntity aula) { this.aula = aula; }
	public String getMotivo() { return motivo; }
	public void setMotivo(String motivo) { this.motivo = motivo; }
	public BigDecimal getValor() { return valor; }
	public void setValor(BigDecimal valor) { this.valor = valor; }
	public OffsetDateTime getAplicadaEm() { return aplicadaEm; }
	public void setAplicadaEm(OffsetDateTime aplicadaEm) { this.aplicadaEm = aplicadaEm; }
}
