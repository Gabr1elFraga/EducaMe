package com.educame.educame_api.application.dto.perfil;

import com.educame.educame_api.domain.enums.GeneroTipo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record AtualizarPerfilUsuarioRequest(
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
	GeneroTipo genero,
	@Size(max = 20, message = "O CPF deve ter no maximo 20 caracteres.")
	String cpf,
	@Size(max = 1000, message = "A foto de perfil deve ter no maximo 1000 caracteres.")
	String fotoPerfil,
	@Size(max = 1000, message = "O diploma deve ter no maximo 1000 caracteres.")
	String diploma
) {
}
