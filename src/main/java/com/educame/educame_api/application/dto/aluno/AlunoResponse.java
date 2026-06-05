package com.educame.educame_api.application.dto.aluno;

import com.educame.educame_api.domain.enums.GeneroTipo;

import java.time.LocalDate;
import java.util.UUID;

public record AlunoResponse(
	UUID id,
	UUID authUserId,
	String nome,
	String sobrenome,
	LocalDate dataNascimento,
	GeneroTipo genero,
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
