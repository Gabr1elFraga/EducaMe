package com.educame.educame_api.infrastructure.persistence.jpa.entity;

import com.educame.educame_api.domain.enums.AulaStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "aulas")
public class AulaEntity extends BaseJpaEntity {
	@ManyToOne(optional = false)
	@JoinColumn(name = "aluno_id", nullable = false)
	private AlunoEntity aluno;
	@ManyToOne(optional = false)
	@JoinColumn(name = "professor_id", nullable = false)
	private ProfessorEntity professor;
	@ManyToOne(optional = false)
	@JoinColumn(name = "disciplina_id", nullable = false)
	private DisciplinaEntity disciplina;
	@Column(nullable = false)
	private OffsetDateTime inicio;
	@Column(nullable = false)
	private OffsetDateTime fim;
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AulaStatus status;
	@Column(nullable = false)
	private String modalidade = "online";
	@Column(columnDefinition = "text")
	private String observacao;

	public AlunoEntity getAluno() { return aluno; }
	public void setAluno(AlunoEntity aluno) { this.aluno = aluno; }
	public ProfessorEntity getProfessor() { return professor; }
	public void setProfessor(ProfessorEntity professor) { this.professor = professor; }
	public DisciplinaEntity getDisciplina() { return disciplina; }
	public void setDisciplina(DisciplinaEntity disciplina) { this.disciplina = disciplina; }
	public OffsetDateTime getInicio() { return inicio; }
	public void setInicio(OffsetDateTime inicio) { this.inicio = inicio; }
	public OffsetDateTime getFim() { return fim; }
	public void setFim(OffsetDateTime fim) { this.fim = fim; }
	public AulaStatus getStatus() { return status; }
	public void setStatus(AulaStatus status) { this.status = status; }
	public String getModalidade() { return modalidade; }
	public void setModalidade(String modalidade) { this.modalidade = modalidade; }
	public String getObservacao() { return observacao; }
	public void setObservacao(String observacao) { this.observacao = observacao; }
}
