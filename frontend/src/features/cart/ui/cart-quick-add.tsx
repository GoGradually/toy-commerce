import {Button} from '../../../shared/ui/button';
import {Card} from '../../../shared/ui/card';
import {Input} from '../../../shared/ui/input';

interface CartQuickAddProps {
    productIdInput: string;
    quantityInput: string;
    onProductIdInputChange: (value: string) => void;
    onQuantityInputChange: (value: string) => void;
    onAddItem: () => void;
}

export function CartQuickAdd({
                                 productIdInput,
                                 quantityInput,
                                 onProductIdInputChange,
                                 onQuantityInputChange,
                                 onAddItem
                             }: CartQuickAddProps) {
    return (
        <Card className="space-y-3">
            <h3 className="text-lg font-semibold">상품 ID로 빠른 추가</h3>
            <div className="flex flex-wrap gap-2">
                <Input
                    className="w-40"
                    min={1}
                    placeholder="상품ID"
                    type="number"
                    value={productIdInput}
                    onChange={(event) => onProductIdInputChange(event.target.value)}
                />
                <Input
                    className="w-32"
                    min={1}
                    placeholder="수량"
                    type="number"
                    value={quantityInput}
                    onChange={(event) => onQuantityInputChange(event.target.value)}
                />
                <Button variant="primary" onClick={onAddItem}>
                    상품 추가
                </Button>
            </div>
        </Card>
    );
}
