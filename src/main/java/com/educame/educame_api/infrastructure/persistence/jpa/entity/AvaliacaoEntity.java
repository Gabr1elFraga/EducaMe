package com.educame.educame_api.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "avaliacoes")
public class AvaliacaoEntity extends BaseJpaEntity {
	@ManyToOne(optional = false)
	@JoinColumn(name = "aula_id", nullable = false)
	private AulaEntity aula;
	@ManyToOne(optional = false)
	@JoinColumn(name = "aluno_id", nullable = false)
	private AlunoEntity aluno;
	@ManyToOne(optional = false)
	@JoinColumn(name = "professor_id", nullable = false)
	private ProfessorEntity professor;
	@Column(nullable = false)
	private Integer nota;
	@Column(columnDefinition = "text")
	private String comentario;

	public AulaEntity getAula() { return aula; }
	public void setAula(AulaEntity aula) { this.aula = aula; }
	public AlunoEntity getAluno() { return aluno; }
	public void setAluno(AlunoEntity aluno) { this.aluno = aluno; }
	public ProfessorEntity getProfessor() { return professor; }
	public void setProfessor(ProfessorEntity professor) { this.professor = professor; }
	public Integer getNota() { return nota; }
	public void setNota(Integer nota) { this.nota = nota; }
	public String getComentario() { return comentario; }
	public void setComentario(String comentario) { this.comentario = comentario; }
}
