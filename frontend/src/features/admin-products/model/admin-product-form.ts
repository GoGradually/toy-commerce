import type {
  ProductStatus,
  UpdateProductRequest,
  UpdateProductStockRequest
} from '../../../shared/api/generated/schema';

export interface AdminProductFormValues {
    name: string;
    price: string;
    status: ProductStatus;
    stock: string;
}

export const defaultAdminProductFormValues: AdminProductFormValues = {
    name: '',
    price: '0',
    status: 'ACTIVE',
    stock: '0'
};

export function toUpdateProductRequest(values: AdminProductFormValues): UpdateProductRequest {
    return {
        name: values.name,
        price: Number.parseFloat(values.price),
        status: values.status
    };
}

export function toUpdateProductStockRequest(values: AdminProductFormValues): UpdateProductStockRequest {
    return {
        stock: Number.parseInt(values.stock, 10)
    };
}
