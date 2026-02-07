import {FormEvent} from 'react';
import {Button} from '../../../shared/ui/button';
import {Card} from '../../../shared/ui/card';
import {Input} from '../../../shared/ui/input';

interface AdminProductStockFormProps {
    stock: string;
    onStockChange: (value: string) => void;
    onSubmit: () => void;
}

export function AdminProductStockForm({stock, onStockChange, onSubmit}: AdminProductStockFormProps) {
    const submit = (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        onSubmit();
    };

    return (
        <Card>
            <form className="flex flex-wrap items-end gap-2" onSubmit={submit}>
                <label className="space-y-1 text-sm font-medium text-slate-700">
                    재고
                    <Input min={0} required type="number" value={stock}
                           onChange={(event) => onStockChange(event.target.value)}/>
                </label>
                <Button type="submit" variant="secondary">
                    재고 수정
                </Button>
            </form>
        </Card>
    );
}
