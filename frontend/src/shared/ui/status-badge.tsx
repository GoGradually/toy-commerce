import {cn} from '../lib/cn';

interface StatusBadgeProps {
    status: string;
}

const statusClassMap: Record<string, string> = {
    ACTIVE: 'bg-emerald-100 text-emerald-800',
    INACTIVE: 'bg-slate-200 text-slate-700',
    CREATED: 'bg-sky-100 text-sky-800',
    INFO_COMPLETED: 'bg-amber-100 text-amber-800',
    PAID: 'bg-emerald-100 text-emerald-800',
    PAYMENT_FAILED: 'bg-red-100 text-red-800',
    SUCCESS: 'bg-emerald-100 text-emerald-800',
    FAILED: 'bg-red-100 text-red-800'
};

const statusLabelMap: Record<string, string> = {
    ACTIVE: '활성',
    INACTIVE: '비활성',
    CREATED: '생성됨',
    INFO_COMPLETED: '정보 입력 완료',
    PAID: '결제 완료',
    PAYMENT_FAILED: '결제 실패',
    SUCCESS: '성공',
    FAILED: '실패'
};

export function StatusBadge({status}: StatusBadgeProps) {
    return (
        <span
            className={cn('inline-flex rounded-full px-2 py-1 text-xs font-semibold', statusClassMap[status] ?? 'bg-slate-200 text-slate-700')}>
      {statusLabelMap[status] ?? status}
    </span>
    );
}
