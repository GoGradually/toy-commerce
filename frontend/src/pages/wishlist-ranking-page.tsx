import {useQuery} from '@tanstack/react-query';
import {useState} from 'react';
import {Link} from 'react-router-dom';
import {toErrorMessage} from '../shared/api/core';
import {queryKeys} from '../shared/api/query-keys';
import {getWishlistRanking} from '../shared/api/wishlist';
import {formatMoney} from '../shared/lib/format';
import {Button} from '../shared/ui/button';
import {Card} from '../shared/ui/card';
import {Input} from '../shared/ui/input';
import {StatePanel} from '../shared/ui/state-panel';
import {StatusBadge} from '../shared/ui/status-badge';

export function WishlistRankingPage() {
    const [limit, setLimit] = useState(10);

    const rankingQuery = useQuery({
        queryKey: queryKeys.wishlist.ranking(limit),
        queryFn: () => getWishlistRanking(limit)
    });

    const ranking = rankingQuery.data?.rankings ?? [];

    return (
        <div className="space-y-4">
            <section
                className="flex flex-wrap items-center justify-between gap-2 rounded-2xl border border-slate-200 bg-white p-4 shadow-panel">
                <div>
                    <h2 className="text-xl font-bold">위시리스트 인기 랭킹</h2>
                    <p className="text-sm text-slate-600">Redis 점수 기반 랭킹 API</p>
                </div>
                <div className="flex items-center gap-2">
                    <Input
                        className="w-24"
                        max={100}
                        min={1}
                        type="number"
                        value={String(limit)}
                        onChange={(event) => {
                            const parsed = Number.parseInt(event.target.value, 10);
                            if (Number.isFinite(parsed) && parsed >= 1 && parsed <= 100) {
                                setLimit(parsed);
                            }
                        }}
                    />
                    <Link to="/">
                        <Button variant="secondary">돌아가기</Button>
                    </Link>
                </div>
            </section>

            {rankingQuery.isLoading ? <StatePanel title="랭킹을 불러오는 중..."/> : null}
            {rankingQuery.isError ? (
                <StatePanel title="랭킹을 불러오지 못했습니다" description={toErrorMessage(rankingQuery.error)}/>
            ) : null}

            {!rankingQuery.isLoading && !rankingQuery.isError ? (
                ranking.length === 0 ? (
                    <StatePanel title="랭킹 데이터가 없습니다"/>
                ) : (
                    <div className="grid gap-3">
                        {ranking.map((item) => (
                            <Card key={`${item.rank}-${item.productId}`}
                                  className="flex items-center justify-between gap-3">
                                <div className="space-y-1">
                                    <p className="text-xs font-semibold uppercase tracking-[0.16em] text-slate-500">순위
                                        #{item.rank}</p>
                                    <p className="text-lg font-semibold">{item.name}</p>
                                    <p className="text-sm text-slate-600">
                                        상품 #{item.productId} · {formatMoney(item.price)} · 위시리스트 수 {item.wishlistCount}
                                    </p>
                                </div>
                                <StatusBadge status={item.status}/>
                            </Card>
                        ))}
                    </div>
                )
            ) : null}
        </div>
    );
}
