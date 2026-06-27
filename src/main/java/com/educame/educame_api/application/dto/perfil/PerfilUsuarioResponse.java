package com.educame.educame_api.application.dto.perfil;

import com.educame.educame_api.domain.enums.GeneroTipo;

import java.time.LocalDate;
import java.util.UUID;

public record PerfilUsuarioResponse(
	UUID id,
	UUID authUserId,
	String nome,
	String sobrenome,
	LocalDate dataNascimento,
	GeneroTipo genero,
	String cpf,
	String fotoPerfil,
	String diploma,
	EnderecoResponse endereco
) {
	public record EnderecoResponse(
		UUID id,
		String rua,
		String numero,
		String complemento,
		String bairro,
		String cidade,
		String estado,
		String cep,
		String pais
	) {
	}
}
