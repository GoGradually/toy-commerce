import {useEffect, useState} from 'react';
import type {ProductResponse} from '../../../shared/api/generated/schema';
import {
    defaultAdminProductFormValues,
    toUpdateProductRequest,
    toUpdateProductStockRequest
} from '../model/admin-product-form';

export function useAdminProductForm(product: ProductResponse | undefined) {
    const [name, setName] = useState(defaultAdminProductFormValues.name);
    const [price, setPrice] = useState(defaultAdminProductFormValues.price);
    const [status, setStatus] = useState(defaultAdminProductFormValues.status);
    const [stock, setStock] = useState(defaultAdminProductFormValues.stock);

    useEffect(() => {
        if (!product) {
            return;
        }

        setName(product.name);
        setPrice(String(product.price));
        setStatus(product.status);
        setStock(String(product.stock));
    }, [product]);

    return {
        values: {
            name,
            price,
            status,
            stock
        },
        setName,
        setPrice,
        setStatus,
        setStock,
        buildProductRequest: () =>
            toUpdateProductRequest({
                name,
                price,
                status,
                stock
            }),
        buildStockRequest: () =>
            toUpdateProductStockRequest({
                name,
                price,
                status,
                stock
            })
    };
}
