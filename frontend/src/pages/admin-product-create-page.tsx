import {useMutation, useQueryClient} from '@tanstack/react-query';
import {FormEvent, useState} from 'react';
import {Link, useNavigate} from 'react-router-dom';
import {createProduct} from '../shared/api/admin-products';
import {toErrorMessage} from '../shared/api/core';
import {queryKeys} from '../shared/api/query-keys';
import type {ProductStatus} from '../shared/api/generated/schema';
import {Button} from '../shared/ui/button';
import {Card} from '../shared/ui/card';
import {Input} from '../shared/ui/input';
import {Select} from '../shared/ui/select';

export function AdminProductCreatePage() {
    const queryClient = useQueryClient();
    const navigate = useNavigate();

    const [name, setName] = useState('');
    const [price, setPrice] = useState('15900');
    const [stock, setStock] = useState('50');
    const [status, setStatus] = useState<ProductStatus>('ACTIVE');

    const createMutation = useMutation({
        mutationFn: () =>
            createProduct({
                name,
                price: Number.parseFloat(price),
                stock: Number.parseInt(stock, 10),
                status
            }),
        onSuccess: (created) => {
            queryClient.invalidateQueries({queryKey: queryKeys.admin.products()});
            queryClient.invalidateQueries({queryKey: queryKeys.products.list(0, 20, 'createdAt', 'desc')});
            navigate(`/admin/products/${created.id}/edit`);
        }
    });

    const submit = (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        createMutation.mutate();
    };

    return (
        <div className="space-y-4">
            <div
                className="flex items-center justify-between rounded-2xl border border-slate-200 bg-white p-4 shadow-panel">
                <div>
                    <h2 className="text-xl font-bold">상품 등록</h2>
                    <p className="text-sm text-slate-600">POST /api/admin/products</p>
                </div>
                <Link to="/admin/products">
                    <Button variant="secondary">관리자 목록으로</Button>
                </Link>
            </div>

            <Card>
                <form className="grid gap-3 md:grid-cols-2" onSubmit={submit}>
                    <label className="space-y-1 text-sm font-medium text-slate-700">
                        이름
                        <Input required value={name} onChange={(event) => setName(event.target.value)}/>
                    </label>
                    <label className="space-y-1 text-sm font-medium text-slate-700">
                        가격
                        <Input
                            min={0}
                            required
                            type="number"
                            value={price}
                            onChange={(event) => setPrice(event.target.value)}
                        />
                    </label>
                    <label className="space-y-1 text-sm font-medium text-slate-700">
                        재고
                        <Input
                            min={0}
                            required
                            type="number"
                            value={stock}
                            onChange={(event) => setStock(event.target.value)}
                        />
                    </label>
                    <label className="space-y-1 text-sm font-medium text-slate-700">
                        상태
                        <Select value={status} onChange={(event) => setStatus(event.target.value as ProductStatus)}>
                            <option value="ACTIVE">활성</option>
                            <option value="INACTIVE">비활성</option>
                        </Select>
                    </label>
                    <div className="md:col-span-2 flex gap-2">
                        <Button type="submit" variant="primary">
                            등록
                        </Button>
                        <Button type="button" variant="secondary" onClick={() => navigate('/admin/products')}>
                            취소
                        </Button>
                    </div>
                </form>
                {createMutation.isError ?
                    <p className="mt-3 text-sm text-red-700">{toErrorMessage(createMutation.error)}</p> : null}
            </Card>
        </div>
    );
}
