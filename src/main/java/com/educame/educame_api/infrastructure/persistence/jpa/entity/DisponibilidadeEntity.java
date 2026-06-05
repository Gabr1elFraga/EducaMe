package com.educame.educame_api.infrastructure.persistence.jpa.entity;

import com.educame.educame_api.domain.enums.DisponibilidadeStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "disponibilidades")
public class DisponibilidadeEntity extends BaseJpaEntity {
	@ManyToOne(optional = false)
	@JoinColumn(name = "professor_id", nullable = false)
	private ProfessorEntity professor;
	@Column(nullable = false)
	private OffsetDateTime inicio;
	@Column(nullable = false)
	private OffsetDateTime fim;
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private DisponibilidadeStatus status;
	@Column(columnDefinition = "text")
	private String observacao;

	public ProfessorEntity getProfessor() { return professor; }
	public void setProfessor(ProfessorEntity professor) { this.professor = professor; }
	public OffsetDateTime getInicio() { return inicio; }
	public void setInicio(OffsetDateTime inicio) { this.inicio = inicio; }
	public OffsetDateTime getFim() { return fim; }
	public void setFim(OffsetDateTime fim) { this.fim = fim; }
	public DisponibilidadeStatus getStatus() { return status; }
	public void setStatus(DisponibilidadeStatus status) { this.status = status; }
	public String getObservacao() { return observacao; }
	public void setObservacao(String observacao) { this.observacao = observacao; }
}
