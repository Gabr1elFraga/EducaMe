package com.educame.educame_api.infrastructure.persistence.jpa.entity;

import com.educame.educame_api.domain.enums.DisponibilidadeStatus;
import com.educame.educame_api.infrastructure.persistence.jpa.converter.DisponibilidadeStatusConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "disponibilidades")
public class DisponibilidadeEntity extends BaseJpaEntity {
	@ManyToOne
	@JoinColumn(name = "anuncio_id")
	private AnuncioAulaEntity anuncio;
	@ManyToOne(optional = false)
	@JoinColumn(name = "professor_id", nullable = false)
	private ProfessorEntity professor;
	@Column(nullable = false)
	private OffsetDateTime inicio;
	@Column(nullable = false)
	private OffsetDateTime fim;
	@Convert(converter = DisponibilidadeStatusConverter.class)
	@Column(nullable = false)
	private DisponibilidadeStatus status;
	@Column(columnDefinition = "text")
	private String observacao;

	public AnuncioAulaEntity getAnuncio() { return anuncio; }
	public void setAnuncio(AnuncioAulaEntity anuncio) { this.anuncio = anuncio; }
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
