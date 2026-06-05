package com.educame.educame_api.domain.avaliacao;

import com.educame.educame_api.domain.aluno.Aluno;
import com.educame.educame_api.domain.aula.Aula;
import com.educame.educame_api.domain.professor.Professor;

import java.util.Objects;
import java.util.UUID;

public class Avaliacao {
	private UUID id;
	private Aula aula;
	private Aluno aluno;
	private Professor professor;
	private Integer nota;
	private String comentario;

	public Avaliacao() {
	}

	public Avaliacao(UUID id, Aula aula, Aluno aluno, Professor professor, Integer nota, String comentario) {
		this.id = id;
		this.aula = aula;
		this.aluno = aluno;
		this.professor = professor;
		this.nota = nota;
		this.comentario = comentario;
	}

	public UUID getId() { return id; }
	public void setId(UUID id) { this.id = id; }
	public Aula getAula() { return aula; }
	public void setAula(Aula aula) { this.aula = aula; }
	public Aluno getAluno() { return aluno; }
	public void setAluno(Aluno aluno) { this.aluno = aluno; }
	public Professor getProfessor() { return professor; }
	public void setProfessor(Professor professor) { this.professor = professor; }
	public Integer getNota() { return nota; }
	public void setNota(Integer nota) { this.nota = nota; }
	public String getComentario() { return comentario; }
	public void setComentario(String comentario) { this.comentario = comentario; }

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Avaliacao avaliacao = (Avaliacao) o;
		return Objects.equals(id, avaliacao.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
