import {Link, useSearchParams} from 'react-router-dom';
import {useHomeProductActions} from '../features/products/hooks/use-home-product-actions';
import {useHomeProductsQuery} from '../features/products/hooks/use-home-products-query';
import {
    parseProductListParams,
    productSortFields,
    withProductListSearchParams
} from '../features/products/model/product-list-params';
import {ProductCardGrid} from '../features/products/ui/product-card-grid';
import {toErrorMessage} from '../shared/api/core';
import {useMember} from '../shared/member/member-context';
import {Button} from '../shared/ui/button';
import {Select} from '../shared/ui/select';
import {StatePanel} from '../shared/ui/state-panel';

export function HomePage() {
    const {memberId} = useMember();
    const [searchParams, setSearchParams] = useSearchParams();

    const sorting = parseProductListParams(searchParams);
    const productQuery = useHomeProductsQuery(sorting);
    const actions = useHomeProductActions(memberId);

    if (productQuery.isLoading) {
        return <StatePanel title="상품을 불러오는 중..."/>;
    }

    if (productQuery.isError) {
        return <StatePanel title="상품을 불러오지 못했습니다" description={toErrorMessage(productQuery.error)}/>;
    }

    if (!productQuery.data) {
        return <StatePanel title="상품 데이터가 없습니다"/>;
    }

    const productPage = productQuery.data;
    const products = productPage.products;

    return (
        <div className="space-y-6">
            <section className="rounded-2xl border border-slate-200 bg-white p-5 shadow-panel">
                <div className="flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
                    <div>
                        <h2 className="text-xl font-bold">상품 목록</h2>
                        <p className="text-sm text-slate-600">현재 회원 헤더: {memberId}</p>
                    </div>
                    <div className="flex flex-wrap gap-2">
                        <Select
                            value={sorting.sortBy}
                            onChange={(event) =>
                                setSearchParams(
                                    withProductListSearchParams(searchParams, {
                                        sortBy: event.target.value,
                                        resetPage: true
                                    })
                                )
                            }
                        >
                            {productSortFields.map((field) => (
                                <option key={field} value={field}>
                                    정렬: {field === 'createdAt' ? '생성일' : field === 'id' ? 'ID' : field === 'name' ? '이름' : '가격'}
                                </option>
                            ))}
                        </Select>
                        <Select
                            value={sorting.direction}
                            onChange={(event) =>
                                setSearchParams(
                                    withProductListSearchParams(searchParams, {
                                        direction: event.target.value === 'asc' ? 'asc' : 'desc',
                                        resetPage: true
                                    })
                                )
                            }
                        >
                            <option value="desc">정렬 방향: 내림차순</option>
                            <option value="asc">정렬 방향: 오름차순</option>
                        </Select>
                        <Link to="/wishlist/ranking">
                            <Button variant="secondary">랭킹 보기</Button>
                        </Link>
                    </div>
                </div>
                {actions.feedback ?
                    <p className="mt-3 text-sm font-medium text-slate-700">{actions.feedback}</p> : null}
            </section>

            {products.length === 0 ? (
                <StatePanel title="활성 상품이 없습니다" description="관리자 메뉴에서 상품을 등록해 주세요."/>
            ) : (
                <ProductCardGrid
                    products={products}
                    onAddToCart={actions.addToCart}
                    onAddToWishlist={actions.addToWishlist}
                    onRemoveFromWishlist={actions.removeFromWishlist}
                />
            )}

            <section
                className="flex items-center justify-between rounded-2xl border border-slate-200 bg-white p-4 shadow-panel">
                <Button
                    disabled={sorting.page <= 0}
                    variant="secondary"
                    onClick={() =>
                        setSearchParams(
                            withProductListSearchParams(searchParams, {
                                page: Math.max(0, sorting.page - 1)
                            })
                        )
                    }
                >
                    이전
                </Button>
                <p className="text-sm text-slate-700">
                    페이지 {productPage.page + 1} / {Math.max(productPage.totalPages, 1)}
                </p>
                <Button
                    disabled={!productPage.hasNext}
                    variant="secondary"
                    onClick={() =>
                        setSearchParams(
                            withProductListSearchParams(searchParams, {
                                page: sorting.page + 1
                            })
                        )
                    }
                >
                    다음
                </Button>
            </section>
        </div>
    );
}
