package com.educame.educame_api.domain.anuncio;

import com.educame.educame_api.domain.disciplina.Disciplina;
import com.educame.educame_api.domain.professor.Professor;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class AnuncioAula {
	private UUID id;
	private Professor professor;
	private Disciplina disciplina;
	private String titulo;
	private String descricao;
	private BigDecimal valorHora;
	private String modalidade;
	private boolean ativo = true;

	public AnuncioAula() {
	}

	public AnuncioAula(UUID id, Professor professor, Disciplina disciplina, String titulo, String descricao, BigDecimal valorHora, String modalidade, boolean ativo) {
		this.id = id;
		this.professor = professor;
		this.disciplina = disciplina;
		this.titulo = titulo;
		this.descricao = descricao;
		this.valorHora = valorHora;
		this.modalidade = modalidade;
		this.ativo = ativo;
	}

	public UUID getId() { return id; }
	public void setId(UUID id) { this.id = id; }
	public Professor getProfessor() { return professor; }
	public void setProfessor(Professor professor) { this.professor = professor; }
	public Disciplina getDisciplina() { return disciplina; }
	public void setDisciplina(Disciplina disciplina) { this.disciplina = disciplina; }
	public String getTitulo() { return titulo; }
	public void setTitulo(String titulo) { this.titulo = titulo; }
	public String getDescricao() { return descricao; }
	public void setDescricao(String descricao) { this.descricao = descricao; }
	public BigDecimal getValorHora() { return valorHora; }
	public void setValorHora(BigDecimal valorHora) { this.valorHora = valorHora; }
	public String getModalidade() { return modalidade; }
	public void setModalidade(String modalidade) { this.modalidade = modalidade; }
	public boolean isAtivo() { return ativo; }
	public void setAtivo(boolean ativo) { this.ativo = ativo; }

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		AnuncioAula that = (AnuncioAula) o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
