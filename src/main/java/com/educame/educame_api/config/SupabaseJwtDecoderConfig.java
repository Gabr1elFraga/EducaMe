package com.educame.educame_api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.util.StringUtils;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
@Profile("supabase")
public class SupabaseJwtDecoderConfig {

	@Bean
	JwtDecoder jwtDecoder(
		@Value("${SUPABASE_JWT_ISSUER}") String issuer,
		@Value("${SUPABASE_JWKS_URI}") String jwksUri,
		@Value("${SUPABASE_JWT_SECRET:}") String jwtSecret
	) {
		var decoder = StringUtils.hasText(jwksUri)
			? jwksDecoder(jwksUri)
			: hmacDecoder(jwtSecret);
		decoder.setJwtValidator(jwtValidator(issuer));
		return decoder;
	}

	private NimbusJwtDecoder jwksDecoder(String jwksUri) {
		return NimbusJwtDecoder.withJwkSetUri(jwksUri)
			.jwsAlgorithm(SignatureAlgorithm.ES256)
			.jwsAlgorithm(SignatureAlgorithm.RS256)
			.build();
	}

	private NimbusJwtDecoder hmacDecoder(String jwtSecret) {
		var secretKey = new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
		return NimbusJwtDecoder.withSecretKey(secretKey)
			.macAlgorithm(MacAlgorithm.HS256)
			.build();
	}

	private OAuth2TokenValidator<Jwt> jwtValidator(String issuer) {
		return new DelegatingOAuth2TokenValidator<>(
			JwtValidators.createDefaultWithIssuer(issuer),
			new JwtTimestampValidator()
		);
	}
}
