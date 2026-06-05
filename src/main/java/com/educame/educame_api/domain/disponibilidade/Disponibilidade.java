package com.educame.educame_api.domain.disponibilidade;

import com.educame.educame_api.domain.enums.DisponibilidadeStatus;
import com.educame.educame_api.domain.professor.Professor;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

public class Disponibilidade {
	private UUID id;
	private Professor professor;
	private OffsetDateTime inicio;
	private OffsetDateTime fim;
	private DisponibilidadeStatus status;
	private String observacao;

	public Disponibilidade() {
	}

	public Disponibilidade(UUID id, Professor professor, OffsetDateTime inicio, OffsetDateTime fim, DisponibilidadeStatus status, String observacao) {
		this.id = id;
		this.professor = professor;
		this.inicio = inicio;
		this.fim = fim;
		this.status = status;
		this.observacao = observacao;
	}

	public UUID getId() { return id; }
	public void setId(UUID id) { this.id = id; }
	public Professor getProfessor() { return professor; }
	public void setProfessor(Professor professor) { this.professor = professor; }
	public OffsetDateTime getInicio() { return inicio; }
	public void setInicio(OffsetDateTime inicio) { this.inicio = inicio; }
	public OffsetDateTime getFim() { return fim; }
	public void setFim(OffsetDateTime fim) { this.fim = fim; }
	public DisponibilidadeStatus getStatus() { return status; }
	public void setStatus(DisponibilidadeStatus status) { this.status = status; }
	public String getObservacao() { return observacao; }
	public void setObservacao(String observacao) { this.observacao = observacao; }

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Disponibilidade that = (Disponibilidade) o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
