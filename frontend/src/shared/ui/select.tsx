import {forwardRef, SelectHTMLAttributes} from 'react';
import {cn} from '../lib/cn';

export const Select = forwardRef<HTMLSelectElement, SelectHTMLAttributes<HTMLSelectElement>>(
    ({className, children, ...props}, ref) => {
        return (
            <select
                ref={ref}
                className={cn(
                    'h-10 rounded-xl border border-slate-300 bg-white px-3 text-sm text-slate-900 shadow-sm focus:border-accent-500 focus:outline-none',
                    className
                )}
                {...props}
            >
                {children}
            </select>
        );
    }
);

Select.displayName = 'Select';
