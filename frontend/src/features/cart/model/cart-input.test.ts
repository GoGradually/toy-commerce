import {describe, expect, it} from 'vitest';
import {parseCartAddItemInput, parseCartQuantityInput} from './cart-input';

describe('parseCartAddItemInput', () => {
    it('parses product id and quantity when both are valid positive integers', () => {
        expect(parseCartAddItemInput('15', '3')).toEqual({productId: 15, quantity: 3});
    });

    it('returns null when any value is invalid', () => {
        expect(parseCartAddItemInput('x', '3')).toBeNull();
        expect(parseCartAddItemInput('4', '0')).toBeNull();
    });
});

describe('parseCartQuantityInput', () => {
    it('returns parsed quantity for valid input', () => {
        expect(parseCartQuantityInput('7')).toBe(7);
    });

    it('returns null for invalid input', () => {
        expect(parseCartQuantityInput('-1')).toBeNull();
        expect(parseCartQuantityInput('abc')).toBeNull();
    });
});
