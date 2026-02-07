import {FormEvent} from 'react';
import type {ProductStatus} from '../../../shared/api/generated/schema';
import {Button} from '../../../shared/ui/button';
import {Card} from '../../../shared/ui/card';
import {Input} from '../../../shared/ui/input';
import {Select} from '../../../shared/ui/select';

interface AdminProductEditFormProps {
    name: string;
    price: string;
    status: ProductStatus;
    onNameChange: (value: string) => void;
    onPriceChange: (value: string) => void;
    onStatusChange: (value: ProductStatus) => void;
    onSubmit: () => void;
}

export function AdminProductEditForm({
                                         name,
                                         price,
                                         status,
                                         onNameChange,
                                         onPriceChange,
                                         onStatusChange,
                                         onSubmit
                                     }: AdminProductEditFormProps) {
    const submit = (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        onSubmit();
    };

    return (
        <Card>
            <form className="grid gap-3 md:grid-cols-2" onSubmit={submit}>
                <label className="space-y-1 text-sm font-medium text-slate-700">
                    이름
                    <Input required value={name} onChange={(event) => onNameChange(event.target.value)}/>
                </label>
                <label className="space-y-1 text-sm font-medium text-slate-700">
                    가격
                    <Input
                        min={0}
                        required
                        type="number"
                        value={price}
                        onChange={(event) => onPriceChange(event.target.value)}
                    />
                </label>
                <label className="space-y-1 text-sm font-medium text-slate-700 md:col-span-2">
                    상태
                    <Select value={status} onChange={(event) => onStatusChange(event.target.value as ProductStatus)}>
                        <option value="ACTIVE">활성</option>
                        <option value="INACTIVE">비활성</option>
                    </Select>
                </label>
                <div className="md:col-span-2">
                    <Button type="submit" variant="primary">
                        상품 정보 저장
                    </Button>
                </div>
            </form>
        </Card>
    );
}
