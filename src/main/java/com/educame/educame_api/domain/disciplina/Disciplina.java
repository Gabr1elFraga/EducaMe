package com.educame.educame_api.domain.disciplina;

import java.util.Objects;
import java.util.UUID;

public class Disciplina {
	private UUID id;
	private String nome;
	private String descricao;
	private boolean ativo;

	public Disciplina() {
	}

	public Disciplina(UUID id, String nome, String descricao, boolean ativo) {
		this.id = id;
		this.nome = nome;
		this.descricao = descricao;
		this.ativo = ativo;
	}

	public UUID getId() { return id; }
	public void setId(UUID id) { this.id = id; }
	public String getNome() { return nome; }
	public void setNome(String nome) { this.nome = nome; }
	public String getDescricao() { return descricao; }
	public void setDescricao(String descricao) { this.descricao = descricao; }
	public boolean isAtivo() { return ativo; }
	public void setAtivo(boolean ativo) { this.ativo = ativo; }

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Disciplina that = (Disciplina) o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
