package com.educame.educame_api.application.dto.autenticacao;

import com.educame.educame_api.domain.enums.GeneroTipo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record CadastroPessoaRequest(
	@NotNull(message = "O identificador do usuario e obrigatorio.")
	UUID authUserId,
	@NotBlank(message = "O nome e obrigatorio.")
	@Size(max = 120, message = "O nome deve ter no maximo 120 caracteres.")
	String nome,
	@NotBlank(message = "O sobrenome e obrigatorio.")
	@Size(max = 120, message = "O sobrenome deve ter no maximo 120 caracteres.")
	String sobrenome,
	@NotNull(message = "A data de nascimento e obrigatoria.")
	@Past(message = "A data de nascimento deve estar no passado.")
	LocalDate dataNascimento,
	@NotNull(message = "O genero e obrigatorio.")
	GeneroTipo genero
) {
}
