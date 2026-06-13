package com.educame.educame_api.domain.professor;

import com.educame.educame_api.domain.endereco.Endereco;
import com.educame.educame_api.domain.pessoa.Pessoa;

import java.util.Objects;
import java.util.UUID;

public class Professor {
	private UUID id;
	private Pessoa pessoa;
	private String bio;
	private boolean ativo;
	private String diploma;
	private String statusVerificacao;
	private java.math.BigDecimal valorHoraAula;

	public Professor() {
	}

	public Professor(UUID id, Pessoa pessoa, String bio, boolean ativo, String diploma, String statusVerificacao, java.math.BigDecimal valorHoraAula) {
		this.id = id;
		this.pessoa = pessoa;
		this.bio = bio;
		this.ativo = ativo;
		this.diploma = diploma;
		this.statusVerificacao = statusVerificacao;
		this.valorHoraAula = valorHoraAula;
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
	public String getCpf() { return pessoa != null ? pessoa.getCpf() : null; }
	public void setCpf(String cpf) { ensurePessoa().setCpf(cpf); }
	public Endereco getEndereco() { return pessoa != null ? pessoa.getEndereco() : null; }
	public void setEndereco(Endereco endereco) { ensurePessoa().setEndereco(endereco); }
	public String getDiploma() { return diploma; }
	public void setDiploma(String diploma) { this.diploma = diploma; }
	public String getStatusVerificacao() { return statusVerificacao; }
	public void setStatusVerificacao(String statusVerificacao) { this.statusVerificacao = statusVerificacao; }
	public java.math.BigDecimal getValorHoraAula() { return valorHoraAula; }
	public void setValorHoraAula(java.math.BigDecimal valorHoraAula) { this.valorHoraAula = valorHoraAula; }
	public String getBio() { return bio; }
	public void setBio(String bio) { this.bio = bio; }
	public boolean isAtivo() { return ativo; }
	public void setAtivo(boolean ativo) { this.ativo = ativo; }

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
		Professor professor = (Professor) o;
		return Objects.equals(id, professor.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
