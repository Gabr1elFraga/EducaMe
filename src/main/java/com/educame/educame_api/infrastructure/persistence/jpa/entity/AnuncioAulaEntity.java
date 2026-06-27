package com.educame.educame_api.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "anuncios_aula")
public class AnuncioAulaEntity extends BaseJpaEntity {
	@ManyToOne(optional = false)
	@JoinColumn(name = "professor_id", nullable = false)
	private ProfessorEntity professor;
	@ManyToOne(optional = false)
	@JoinColumn(name = "disciplina_id", nullable = false)
	private DisciplinaEntity disciplina;
	@Column(nullable = false)
	private String titulo;
	@Column(columnDefinition = "text")
	private String descricao;
	@Column(name = "valor_hora", nullable = false, precision = 12, scale = 2)
	private BigDecimal valorHora;
	@Column(nullable = false)
	private String modalidade = "online";
	@Column(nullable = false)
	private boolean ativo = true;

	public ProfessorEntity getProfessor() { return professor; }
	public void setProfessor(ProfessorEntity professor) { this.professor = professor; }
	public DisciplinaEntity getDisciplina() { return disciplina; }
	public void setDisciplina(DisciplinaEntity disciplina) { this.disciplina = disciplina; }
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
}
