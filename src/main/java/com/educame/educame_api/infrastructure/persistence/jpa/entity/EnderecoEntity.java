package com.educame.educame_api.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "enderecos")
public class EnderecoEntity extends BaseJpaEntity {
	@Column(nullable = false)
	private String rua;
	@Column(nullable = false)
	private String numero;
	private String complemento;
	@Column(nullable = false)
	private String bairro;
	@Column(nullable = false)
	private String cidade;
	@Column(nullable = false)
	private String estado;
	@Column(nullable = false)
	private String cep;
	@Column(nullable = false)
	private String pais;

	public String getRua() { return rua; }
	public void setRua(String rua) { this.rua = rua; }
	public String getNumero() { return numero; }
	public void setNumero(String numero) { this.numero = numero; }
	public String getComplemento() { return complemento; }
	public void setComplemento(String complemento) { this.complemento = complemento; }
	public String getBairro() { return bairro; }
	public void setBairro(String bairro) { this.bairro = bairro; }
	public String getCidade() { return cidade; }
	public void setCidade(String cidade) { this.cidade = cidade; }
	public String getEstado() { return estado; }
	public void setEstado(String estado) { this.estado = estado; }
	public String getCep() { return cep; }
	public void setCep(String cep) { this.cep = cep; }
	public String getPais() { return pais; }
	public void setPais(String pais) { this.pais = pais; }
}
