package com.educame.educame_api.domain.aula;

import com.educame.educame_api.domain.aluno.Aluno;
import com.educame.educame_api.domain.anuncio.AnuncioAula;
import com.educame.educame_api.domain.disciplina.Disciplina;
import com.educame.educame_api.domain.disponibilidade.Disponibilidade;
import com.educame.educame_api.domain.enums.AulaStatus;
import com.educame.educame_api.domain.professor.Professor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

public class Aula {
	private UUID id;
	private AnuncioAula anuncio;
	private Disponibilidade disponibilidade;
	private Aluno aluno;
	private Professor professor;
	private Disciplina disciplina;
	private OffsetDateTime inicio;
	private OffsetDateTime fim;
	private AulaStatus status;
	private String modalidade;
	private BigDecimal valorAula;
	private String observacao;

	public Aula() {
	}

	public Aula(UUID id, AnuncioAula anuncio, Disponibilidade disponibilidade, Aluno aluno, Professor professor, Disciplina disciplina, OffsetDateTime inicio, OffsetDateTime fim, AulaStatus status, String modalidade, BigDecimal valorAula, String observacao) {
		this.id = id;
		this.anuncio = anuncio;
		this.disponibilidade = disponibilidade;
		this.aluno = aluno;
		this.professor = professor;
		this.disciplina = disciplina;
		this.inicio = inicio;
		this.fim = fim;
		this.status = status;
		this.modalidade = modalidade;
		this.valorAula = valorAula;
		this.observacao = observacao;
	}

	public UUID getId() { return id; }
	public void setId(UUID id) { this.id = id; }
	public AnuncioAula getAnuncio() { return anuncio; }
	public void setAnuncio(AnuncioAula anuncio) { this.anuncio = anuncio; }
	public Disponibilidade getDisponibilidade() { return disponibilidade; }
	public void setDisponibilidade(Disponibilidade disponibilidade) { this.disponibilidade = disponibilidade; }
	public Aluno getAluno() { return aluno; }
	public void setAluno(Aluno aluno) { this.aluno = aluno; }
	public Professor getProfessor() { return professor; }
	public void setProfessor(Professor professor) { this.professor = professor; }
	public Disciplina getDisciplina() { return disciplina; }
	public void setDisciplina(Disciplina disciplina) { this.disciplina = disciplina; }
	public OffsetDateTime getInicio() { return inicio; }
	public void setInicio(OffsetDateTime inicio) { this.inicio = inicio; }
	public OffsetDateTime getFim() { return fim; }
	public void setFim(OffsetDateTime fim) { this.fim = fim; }
	public AulaStatus getStatus() { return status; }
	public void setStatus(AulaStatus status) { this.status = status; }
	public String getModalidade() { return modalidade; }
	public void setModalidade(String modalidade) { this.modalidade = modalidade; }
	public BigDecimal getValorAula() { return valorAula; }
	public void setValorAula(BigDecimal valorAula) { this.valorAula = valorAula; }
	public String getObservacao() { return observacao; }
	public void setObservacao(String observacao) { this.observacao = observacao; }

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Aula aula = (Aula) o;
		return Objects.equals(id, aula.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
