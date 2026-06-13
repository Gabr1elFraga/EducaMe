package com.educame.educame_api.domain.aluno;

import com.educame.educame_api.domain.endereco.Endereco;
import com.educame.educame_api.domain.enums.GeneroTipo;
import com.educame.educame_api.domain.pessoa.Pessoa;

import java.util.Objects;
import java.util.UUID;

public class Aluno {
	private UUID id;
	private Pessoa pessoa;

	public Aluno() {
	}

	public Aluno(UUID id, Pessoa pessoa) {
		this.id = id;
		this.pessoa = pessoa;
	}

	public UUID getId() { return id; }
	public void setId(UUID id) { this.id = id; }
	public Pessoa getPessoa() { return pessoa; }
	public void setPessoa(Pessoa pessoa) { this.pessoa = pessoa; }
	public UUID getAuthUserId() { return pessoa != null ? pessoa.getAuthUserId() : null; }
	public void setAuthUserId(UUID authUserId) { ensurePessoa().setAuthUserId(authUserId); }
	public String getNome() { return pessoa != null ? pessoa.getNome() : null; }
	public void setNome(String nome) { ensurePessoa().setNome(nome); }
	public String getSobrenome() { return pessoa != null ? pessoa.getSobrenome() : null; }
	public void setSobrenome(String sobrenome) { ensurePessoa().setSobrenome(sobrenome); }
	public java.time.LocalDate getDataNascimento() { return pessoa != null ? pessoa.getDataNascimento() : null; }
	public void setDataNascimento(java.time.LocalDate dataNascimento) { ensurePessoa().setDataNascimento(dataNascimento); }
	public GeneroTipo getGenero() { return pessoa != null ? pessoa.getGenero() : null; }
	public void setGenero(GeneroTipo genero) { ensurePessoa().setGenero(genero); }
	public Endereco getEndereco() { return pessoa != null ? pessoa.getEndereco() : null; }
	public void setEndereco(Endereco endereco) { ensurePessoa().setEndereco(endereco); }

	private Pessoa ensurePessoa() {
		if (pessoa == null) {
			pessoa = new Pessoa();
		}
		return pessoa;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Aluno aluno = (Aluno) o;
		return Objects.equals(id, aluno.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
