const moneyFormatter = new Intl.NumberFormat('ko-KR', {
    maximumFractionDigits: 0
});

export function formatMoney(value: number) {
    return `${moneyFormatter.format(value)}원`;
}
