import {HTMLAttributes} from 'react';
import {cn} from '../lib/cn';

type CardProps = HTMLAttributes<HTMLDivElement>;

export function Card({className, ...props}: CardProps) {
    return (
        <div
            className={cn(
                'rounded-2xl border border-slate-200 bg-white p-5 shadow-panel transition hover:-translate-y-0.5',
                className
            )}
            {...props}
        />
    );
}
