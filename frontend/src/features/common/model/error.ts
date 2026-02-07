import {toErrorMessage} from '../../../shared/api/core';

export function firstErrorMessage(errors: Array<unknown | null | undefined>): string | null {
    const first = errors.find((error) => error !== null && error !== undefined);
    if (first === undefined) {
        return null;
    }

    return toErrorMessage(first);
}
