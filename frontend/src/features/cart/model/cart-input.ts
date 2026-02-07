import {parsePositiveIntOrNull} from '../../common/model/number-param';

export interface CartAddItemInput {
    productId: number;
    quantity: number;
}

export function parseCartAddItemInput(productIdRaw: string, quantityRaw: string): CartAddItemInput | null {
    const productId = parsePositiveIntOrNull(productIdRaw);
    const quantity = parsePositiveIntOrNull(quantityRaw);

    if (productId === null || quantity === null) {
        return null;
    }

    return {productId, quantity};
}

export function parseCartQuantityInput(quantityRaw: string): number | null {
    return parsePositiveIntOrNull(quantityRaw);
}
