package com.educame.educame_api.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "disciplinas")
public class DisciplinaEntity extends BaseJpaEntity {
	@Column(nullable = false, unique = true)
	private String nome;
	@Column(columnDefinition = "text")
	private String descricao;
	@Column(nullable = false)
	private boolean ativo = true;

	public String getNome() { return nome; }
	public void setNome(String nome) { this.nome = nome; }
	public String getDescricao() { return descricao; }
	public void setDescricao(String descricao) { this.descricao = descricao; }
	public boolean isAtivo() { return ativo; }
	public void setAtivo(boolean ativo) { this.ativo = ativo; }
}
