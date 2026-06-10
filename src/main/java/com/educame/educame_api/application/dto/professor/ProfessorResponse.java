package com.educame.educame_api.application.dto.professor;

import java.util.UUID;

public record ProfessorResponse(
	UUID id,
	UUID authUserId,
	String nome,
	String sobrenome,
	String bio,
	EnderecoResponse endereco,
	boolean ativo
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
