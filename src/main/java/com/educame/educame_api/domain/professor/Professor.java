package com.educame.educame_api.domain.professor;

import com.educame.educame_api.domain.endereco.Endereco;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class Professor {
	private UUID id;
	private UUID authUserId;
	private String nome;
	private String sobrenome;
	private String cpf;
	private LocalDate dataNascimento;
	private String bio;
	private Endereco endereco;
	private boolean ativo;

	public Professor() {
	}

	public Professor(UUID id, UUID authUserId, String nome, String sobrenome, String cpf, LocalDate dataNascimento, String bio, Endereco endereco, boolean ativo) {
		this.id = id;
		this.authUserId = authUserId;
		this.nome = nome;
		this.sobrenome = sobrenome;
		this.cpf = cpf;
		this.dataNascimento = dataNascimento;
		this.bio = bio;
		this.endereco = endereco;
		this.ativo = ativo;
	}

	public UUID getId() { return id; }
	public void setId(UUID id) { this.id = id; }
	public UUID getAuthUserId() { return authUserId; }
	public void setAuthUserId(UUID authUserId) { this.authUserId = authUserId; }
	public String getNome() { return nome; }
	public void setNome(String nome) { this.nome = nome; }
	public String getSobrenome() { return sobrenome; }
	public void setSobrenome(String sobrenome) { this.sobrenome = sobrenome; }
	public String getCpf() { return cpf; }
	public void setCpf(String cpf) { this.cpf = cpf; }
	public LocalDate getDataNascimento() { return dataNascimento; }
	public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
	public String getBio() { return bio; }
	public void setBio(String bio) { this.bio = bio; }
	public Endereco getEndereco() { return endereco; }
	public void setEndereco(Endereco endereco) { this.endereco = endereco; }
	public boolean isAtivo() { return ativo; }
	public void setAtivo(boolean ativo) { this.ativo = ativo; }

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Professor professor = (Professor) o;
		return Objects.equals(id, professor.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
