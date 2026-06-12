package com.educame.educame_api.application.dto.autenticacao;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record CadastroProfessorRequest(
	@NotNull(message = "O identificador do usuario e obrigatorio.")
	UUID authUserId,
	@NotBlank(message = "O nome e obrigatorio.")
	@Size(max = 120, message = "O nome deve ter no maximo 120 caracteres.")
	String nome,
	@NotBlank(message = "O sobrenome e obrigatorio.")
	@Size(max = 120, message = "O sobrenome deve ter no maximo 120 caracteres.")
	String sobrenome,
	@NotBlank(message = "O CPF e obrigatorio.")
	@Pattern(regexp = "\\d{11}|\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}", message = "CPF invalido.")
	String cpf,
	@NotNull(message = "A data de nascimento e obrigatoria.")
	@Past(message = "A data de nascimento deve estar no passado.")
	LocalDate dataNascimento
) {
}
