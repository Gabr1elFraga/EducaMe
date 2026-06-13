package com.educame.educame_api.domain.pessoa;

import com.educame.educame_api.domain.endereco.Endereco;
import com.educame.educame_api.domain.enums.GeneroTipo;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class Pessoa {
	private UUID id;
	private UUID authUserId;
	private String nome;
	private String sobrenome;
	private LocalDate dataNascimento;
	private GeneroTipo genero;
	private Endereco endereco;
	private String cpf;
	private String fotoPerfil;

	public Pessoa() {
	}

	public Pessoa(UUID id, UUID authUserId, String nome, String sobrenome, LocalDate dataNascimento, GeneroTipo genero, Endereco endereco, String cpf, String fotoPerfil) {
		this.id = id;
		this.authUserId = authUserId;
		this.nome = nome;
		this.sobrenome = sobrenome;
		this.dataNascimento = dataNascimento;
		this.genero = genero;
		this.endereco = endereco;
		this.cpf = cpf;
		this.fotoPerfil = fotoPerfil;
	}

	public UUID getId() { return id; }
	public void setId(UUID id) { this.id = id; }
	public UUID getAuthUserId() { return authUserId; }
	public void setAuthUserId(UUID authUserId) { this.authUserId = authUserId; }
	public String getNome() { return nome; }
	public void setNome(String nome) { this.nome = nome; }
	public String getSobrenome() { return sobrenome; }
	public void setSobrenome(String sobrenome) { this.sobrenome = sobrenome; }
	public LocalDate getDataNascimento() { return dataNascimento; }
	public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
	public GeneroTipo getGenero() { return genero; }
	public void setGenero(GeneroTipo genero) { this.genero = genero; }
	public Endereco getEndereco() { return endereco; }
	public void setEndereco(Endereco endereco) { this.endereco = endereco; }
	public String getCpf() { return cpf; }
	public void setCpf(String cpf) { this.cpf = cpf; }
	public String getFotoPerfil() { return fotoPerfil; }
	public void setFotoPerfil(String fotoPerfil) { this.fotoPerfil = fotoPerfil; }

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Pessoa pessoa = (Pessoa) o;
		return Objects.equals(id, pessoa.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
