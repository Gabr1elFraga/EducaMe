import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { API_BASE_URL } from '../tokens/api-base-url.token';

export const apiBaseUrlInterceptor: HttpInterceptorFn = (req, next) => {
  const baseUrl = inject(API_BASE_URL);
  const isRelative = req.url.startsWith('/') && !req.url.startsWith('//');

  if (!isRelative || req.url.startsWith(baseUrl)) {
    return next(req);
  }

  const normalizedBase = baseUrl.endsWith('/') ? baseUrl.slice(0, -1) : baseUrl;
  const normalizedPath = req.url.startsWith('/') ? req.url : `/${req.url}`;

  return next(req.clone({ url: `${normalizedBase}${normalizedPath}` }));
};
