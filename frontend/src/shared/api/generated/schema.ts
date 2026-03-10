/*
 * Snapshot contracts for Toy Commerce API.
 * Regenerate with: npm run generate:api
 */

export type ProductStatus = 'ACTIVE' | 'INACTIVE';
export type OrderStatus = 'CREATED' | 'INFO_COMPLETED' | 'PAID' | 'PAYMENT_FAILED';
export type PaymentMethod = 'CARD' | 'BANK_TRANSFER';
export type PaymentResult = 'SUCCESS' | 'FAILED';

export interface ApiErrorBody {
    code: string;
    message: string;
}

export interface ApiEnvelope<T> {
    success: boolean;
    data: T | null;
    error: ApiErrorBody | null;
}

export interface ProductResponse {
    id: number;
    name: string;
    price: number;
    stock: number;
    status: ProductStatus;
}

export interface ProductListResponse {
    products: ProductResponse[];
    page: number;
    size: number;
    totalElements: number;
    totalPages: number;
    hasNext: boolean;
}

export interface CartItemResponse {
    productId: number;
    name: string;
    price: number;
    quantity: number;
    lineTotal: number;
}

export interface CartResponse {
    items: CartItemResponse[];
    cartTotal: number;
}

export interface OrderItemResponse {
    productId: number;
    productName: string;
    unitPrice: number;
    quantity: number;
    lineTotal: number;
}

export interface CheckoutOrderResponse {
    orderId: number;
    status: OrderStatus;
    totalAmount: number;
    items: OrderItemResponse[];
}

export interface OrderDetailsSnapshotResponse {
    receiverName: string | null;
    receiverPhone: string | null;
    zipCode: string | null;
    addressLine1: string | null;
    addressLine2: string | null;
    couponCode: string | null;
    paymentMethod: PaymentMethod | null;
}

export interface OrderDetailResponse {
    orderId: number;
    memberId: number;
    status: OrderStatus;
    originalAmount: number;
    discountAmount: number;
    totalAmount: number;
    items: OrderItemResponse[];
    orderDetails: OrderDetailsSnapshotResponse;
    createdAt: string;
}

export interface CompleteOrderDetailsResponse {
    orderId: number;
    status: OrderStatus;
    originalAmount: number;
    discountAmount: number;
    totalAmount: number;
    couponCode: string | null;
    paymentMethod: PaymentMethod | null;
}

export interface PayOrderResponse {
    orderId: number;
    status: OrderStatus;
    paid: boolean;
    paymentResult: PaymentResult;
}

export interface WishlistPopularRankingItemResponse {
    rank: number;
    productId: number;
    name: string;
    price: number;
    status: ProductStatus;
    wishlistCount: number;
}

export interface WishlistPopularRankingResponse {
    limit: number;
    rankings: WishlistPopularRankingItemResponse[];
}

export interface AddCartItemRequest {
    productId: number;
    quantity: number;
}

export interface UpdateCartItemQuantityRequest {
    quantity: number;
}

export interface PayOrderRequest {
    paymentToken: string;
}

export interface CompleteOrderDetailsRequest {
    receiverName: string;
    receiverPhone: string;
    zipCode: string;
    addressLine1: string;
    addressLine2?: string;
    couponCode?: string;
    paymentMethod: PaymentMethod;
}

export interface CreateProductRequest {
    name: string;
    price: number;
    stock: number;
    status: ProductStatus;
}

export interface UpdateProductRequest {
    name: string;
    price: number;
    status: ProductStatus;
}

export interface UpdateProductStockRequest {
    stock: number;
}
