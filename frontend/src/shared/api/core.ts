import {env} from '../config/env';
import {ApiError} from './generated/client/core/ApiError';
import type {ErrorBody} from './generated/client/models/ErrorBody';
import type {ApiEnvelope} from './generated/schema';

interface RequestOptions extends Omit<RequestInit, 'headers' | 'body'> {
    memberId?: number;
    headers?: HeadersInit;
    body?: unknown;
}

interface ErrorPayload {
    code: string;
    message: string;
}

interface ApiEnvelopeLike<T> {
    success?: boolean;
    data?: T | null;
    error?: ErrorBody;
}

export class ApiClientError extends Error {
    readonly code: string;
    readonly status: number;

    constructor(message: string, status: number, code = 'UNKNOWN') {
        super(message);
        this.name = 'ApiClientError';
        this.code = code;
        this.status = status;
    }
}

function isEnvelope<T>(payload: unknown): payload is ApiEnvelope<T> {
    if (!payload || typeof payload !== 'object') {
        return false;
    }

    return 'success' in payload;
}

function extractErrorPayload(payload: unknown): ErrorPayload | null {
    if (!payload || typeof payload !== 'object') {
        return null;
    }

    if (!('error' in payload)) {
        return null;
    }

    const error = (payload as { error?: unknown }).error;
    if (!error || typeof error !== 'object') {
        return null;
    }

    const code = (error as { code?: unknown }).code;
    const message = (error as { message?: unknown }).message;

    if (typeof code !== 'string' || typeof message !== 'string') {
        return null;
    }

    return {code, message};
}

async function readJson(response: Response): Promise<unknown | null> {
    const contentType = response.headers.get('content-type');
    if (!contentType || !contentType.includes('application/json')) {
        return null;
    }

    return response.json();
}

export async function apiRequest<T>(path: string, options: RequestOptions = {}): Promise<T> {
    const headers = new Headers(options.headers);
    headers.set('Accept', 'application/json');

    if (options.memberId !== undefined) {
        headers.set('X-Member-Id', String(options.memberId));
    }

    let body: BodyInit | undefined;
    if (options.body !== undefined) {
        headers.set('Content-Type', 'application/json');
        body = JSON.stringify(options.body);
    }

    const response = await fetch(`${env.apiBaseUrl}${path}`, {
        ...options,
        headers,
        body
    });

    const payload = await readJson(response);

    if (!response.ok) {
        const errorPayload = extractErrorPayload(payload);
        if (errorPayload) {
            throw new ApiClientError(errorPayload.message, response.status, errorPayload.code);
        }

        throw new ApiClientError(`요청이 실패했습니다 (상태 코드: ${response.status}).`, response.status, `HTTP-${response.status}`);
    }

    if (!isEnvelope<T>(payload)) {
        throw new ApiClientError('응답 형식이 올바르지 않습니다.', response.status, 'INVALID_RESPONSE');
    }

    if (!payload.success) {
        const code = payload.error?.code ?? 'UNKNOWN';
        const message = payload.error?.message ?? '요청이 실패했습니다.';
        throw new ApiClientError(message, response.status, code);
    }

    return payload.data as T;
}

export function unwrapApiEnvelope<T>(
    payload: ApiEnvelopeLike<T> | undefined,
    options: { allowNullData?: boolean } = {}
): T {
    if (!payload?.success) {
        const code = payload?.error?.code ?? 'UNKNOWN';
        const message = payload?.error?.message ?? '요청이 실패했습니다.';
        throw new ApiClientError(message, 200, code);
    }

    const data = payload.data;
    if (!options.allowNullData && (data === undefined || data === null)) {
        throw new ApiClientError('응답 데이터가 비어 있습니다.', 200, 'INVALID_RESPONSE');
    }

    return data as T;
}

export function normalizeClientError(error: unknown): ApiClientError {
    if (error instanceof ApiClientError) {
        return error;
    }

    if (error instanceof ApiError) {
        const errorPayload = extractErrorPayload(error.body);
        if (errorPayload) {
            return new ApiClientError(errorPayload.message, error.status, errorPayload.code);
        }

        return new ApiClientError(error.message, error.status, `HTTP-${error.status}`);
    }

    if (error instanceof Error) {
        return new ApiClientError(error.message, 500, 'UNEXPECTED');
    }

    return new ApiClientError('알 수 없는 오류가 발생했습니다.', 500, 'UNEXPECTED');
}

export function toErrorMessage(error: unknown): string {
    const normalized = normalizeClientError(error);
    return `${normalized.code}: ${normalized.message}`;
}
