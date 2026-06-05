package com.educame.educame_api.infrastructure.persistence.jpa.entity;

import com.educame.educame_api.domain.enums.GeneroTipo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "alunos")
public class AlunoEntity extends BaseJpaEntity {
	@Column(name = "auth_user_id", unique = true)
	private UUID authUserId;
	@Column(nullable = false)
	private String nome;
	@Column(nullable = false)
	private String sobrenome;
	@Column(name = "data_nascimento", nullable = false)
	private LocalDate dataNascimento;
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private GeneroTipo genero;
	@ManyToOne
	@JoinColumn(name = "endereco_id")
	private EnderecoEntity endereco;

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
	public EnderecoEntity getEndereco() { return endereco; }
	public void setEndereco(EnderecoEntity endereco) { this.endereco = endereco; }
}
