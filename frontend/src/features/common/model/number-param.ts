export function parseIntegerParam(raw: string | null, fallback: number): number {
    if (!raw) {
        return fallback;
    }

    const parsed = Number.parseInt(raw, 10);
    if (!Number.isFinite(parsed)) {
        return fallback;
    }

    return parsed;
}

export function parsePositiveIntOrNull(raw: string): number | null {
    const parsed = Number.parseInt(raw, 10);
    if (!Number.isFinite(parsed) || parsed < 1) {
        return null;
    }

    return parsed;
}

export function parseBoundedIntOrNull(raw: string, options: { min: number; max: number }): number | null {
    const parsed = Number.parseInt(raw, 10);
    if (!Number.isFinite(parsed)) {
        return null;
    }

    if (parsed < options.min || parsed > options.max) {
        return null;
    }

    return parsed;
}
