import {describe, expect, it} from 'vitest';
import {ApiClientError} from '../../../shared/api/core';
import {firstErrorMessage} from './error';

describe('firstErrorMessage', () => {
    it('returns null when no error exists', () => {
        expect(firstErrorMessage([null, undefined])).toBeNull();
    });

    it('returns formatted message of the first available error', () => {
        const message = firstErrorMessage([
            null,
            new ApiClientError('Cart is empty', 400, 'CART-400'),
            new ApiClientError('Another', 500, 'NEXT')
        ]);

        expect(message).toBe('CART-400: Cart is empty');
    });
});
