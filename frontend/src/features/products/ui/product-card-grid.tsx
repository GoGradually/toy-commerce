import {Link} from 'react-router-dom';
import type {ProductResponse} from '../../../shared/api/generated/schema';
import {formatMoney} from '../../../shared/lib/format';
import {Button} from '../../../shared/ui/button';
import {Card} from '../../../shared/ui/card';
import {StatusBadge} from '../../../shared/ui/status-badge';

interface ProductCardGridProps {
    products: ProductResponse[];
    onAddToCart: (productId: number) => void;
    onAddToWishlist: (productId: number) => void;
    onRemoveFromWishlist: (productId: number) => void;
}

export function ProductCardGrid({
                                    products,
                                    onAddToCart,
                                    onAddToWishlist,
                                    onRemoveFromWishlist
                                }: ProductCardGridProps) {
    return (
        <section className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
            {products.map((product) => (
                <Card key={product.id} className="flex flex-col gap-4">
                    <div className="space-y-1">
                        <div className="flex items-center justify-between gap-2">
                            <h3 className="text-lg font-semibold">{product.name}</h3>
                            <StatusBadge status={product.status}/>
                        </div>
                        <p className="text-sm text-slate-600">ID: {product.id}</p>
                        <p className="text-base font-semibold text-slate-900">{formatMoney(product.price)}</p>
                        <p className="text-sm text-slate-600">재고: {product.stock}</p>
                    </div>
                    <div className="mt-auto flex flex-wrap gap-2">
                        <Link to={`/products/${product.id}`}>
                            <Button variant="secondary">상세</Button>
                        </Link>
                        <Button variant="primary" onClick={() => onAddToCart(product.id)}>
                            장바구니 추가
                        </Button>
                        <Button variant="secondary" onClick={() => onAddToWishlist(product.id)}>
                            위시리스트 추가
                        </Button>
                        <Button variant="secondary" onClick={() => onRemoveFromWishlist(product.id)}>
                            위시리스트 제거
                        </Button>
                    </div>
                </Card>
            ))}
        </section>
    );
}
