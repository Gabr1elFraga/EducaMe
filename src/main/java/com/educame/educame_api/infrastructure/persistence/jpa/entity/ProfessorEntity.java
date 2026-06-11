package com.educame.educame_api.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "professores")
public class ProfessorEntity extends BaseJpaEntity {
	@Column(name = "auth_user_id", unique = true)
	private UUID authUserId;
	@Column(nullable = false)
	private String nome;
	@Column(nullable = false)
	private String sobrenome;
	@Column(unique = true)
	private String cpf;
	@Column(name = "data_nascimento")
	private LocalDate dataNascimento;
	@Column(columnDefinition = "text")
	private String bio;
	@ManyToOne
	@JoinColumn(name = "endereco_id")
	private EnderecoEntity endereco;
	@Column(nullable = false)
	private boolean ativo = true;

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
	public EnderecoEntity getEndereco() { return endereco; }
	public void setEndereco(EnderecoEntity endereco) { this.endereco = endereco; }
	public boolean isAtivo() { return ativo; }
	public void setAtivo(boolean ativo) { this.ativo = ativo; }
}
