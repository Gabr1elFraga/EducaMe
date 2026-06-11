package com.educame.educame_api.infrastructure.persistence.jpa.entity;

import com.educame.educame_api.domain.enums.GeneroTipo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "alunos")
public class AlunoEntity extends BaseJpaEntity {
	@OneToOne(optional = false, cascade = CascadeType.ALL)
	@JoinColumn(name = "pessoa_id", nullable = false, unique = true)
	private PessoaEntity pessoa;

	public PessoaEntity getPessoa() {
		return pessoa;
	}

	public void setPessoa(PessoaEntity pessoa) {
		this.pessoa = pessoa;
	}

	public UUID getAuthUserId() {
		return pessoa != null ? pessoa.getAuthUserId() : null;
	}

	public void setAuthUserId(UUID authUserId) {
		ensurePessoa().setAuthUserId(authUserId);
	}

	public String getNome() {
		return pessoa != null ? pessoa.getNome() : null;
	}

	public void setNome(String nome) {
		ensurePessoa().setNome(nome);
	}

	public String getSobrenome() {
		return pessoa != null ? pessoa.getSobrenome() : null;
	}

	public void setSobrenome(String sobrenome) {
		ensurePessoa().setSobrenome(sobrenome);
	}

	public java.time.LocalDate getDataNascimento() {
		return pessoa != null ? pessoa.getDataNascimento() : null;
	}

	public void setDataNascimento(java.time.LocalDate dataNascimento) {
		ensurePessoa().setDataNascimento(dataNascimento);
	}

	public GeneroTipo getGenero() {
		return pessoa != null ? pessoa.getGenero() : GeneroTipo.NAO_INFORMADO;
	}

	public void setGenero(GeneroTipo genero) {
		ensurePessoa().setGenero(genero);
	}

	public EnderecoEntity getEndereco() {
		return pessoa != null ? pessoa.getEndereco() : null;
	}

	public void setEndereco(EnderecoEntity endereco) {
		ensurePessoa().setEndereco(endereco);
	}

	private PessoaEntity ensurePessoa() {
		if (pessoa == null) {
			pessoa = new PessoaEntity();
		}
		return pessoa;
	}
}
