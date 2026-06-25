import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

export const authTokenInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.accessToken;
  const currentUser = authService.currentUser;
  const authUserId = currentUser?.id;
  const isApiRequest = req.url.startsWith('/api');

  if (!isApiRequest) {
    return next(req);
  }

  const headers: Record<string, string> = {};

  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  if (authUserId) {
    headers['X-Auth-User-Id'] = authUserId;
  }

  const metadata = currentUser?.user_metadata as Record<string, unknown> | undefined;
  const fullName = resolveMetadataString(metadata, 'full_name', 'name');
  const firstName = resolveMetadataString(metadata, 'nome', 'first_name') ?? firstFromFullName(fullName);
  const lastName = resolveMetadataString(metadata, 'sobrenome', 'last_name') ?? lastFromFullName(fullName);
  const birthDate = resolveMetadataString(metadata, 'data_nascimento', 'dataNascimento');

  setEncodedHeader(headers, 'X-User-Nome', firstName);
  setEncodedHeader(headers, 'X-User-Sobrenome', lastName);
  setEncodedHeader(headers, 'X-User-Data-Nascimento', birthDate);
  setEncodedHeader(headers, 'X-User-Email', currentUser?.email);

  return next(
    req.clone({
      setHeaders: headers,
    }),
  );
};

function resolveMetadataString(
  metadata: Record<string, unknown> | undefined,
  ...keys: string[]
): string | null {
  for (const key of keys) {
    const value = metadata?.[key];

    if (typeof value === 'string' && value.trim().length > 0) {
      return value.trim();
    }
  }

  return null;
}

function firstFromFullName(fullName: string | null): string | null {
  return fullName?.split(/\s+/).filter(Boolean)[0] ?? null;
}

function lastFromFullName(fullName: string | null): string | null {
  const parts = fullName?.split(/\s+/).filter(Boolean) ?? [];
  return parts.length > 1 ? parts.slice(1).join(' ') : null;
}

function setEncodedHeader(headers: Record<string, string>, key: string, value: string | null | undefined): void {
  if (value && value.trim().length > 0) {
    headers[key] = encodeURIComponent(value.trim());
  }
}
