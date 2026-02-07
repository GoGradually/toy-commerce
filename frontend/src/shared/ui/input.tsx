import {forwardRef, InputHTMLAttributes} from 'react';
import {cn} from '../lib/cn';

export const Input = forwardRef<HTMLInputElement, InputHTMLAttributes<HTMLInputElement>>(
    ({className, ...props}, ref) => {
        return (
            <input
                ref={ref}
                className={cn(
                    'h-10 w-full rounded-xl border border-slate-300 bg-white px-3 text-sm text-slate-900 shadow-sm placeholder:text-slate-400 focus:border-accent-500 focus:outline-none',
                    className
                )}
                {...props}
            />
        );
    }
);

Input.displayName = 'Input';
