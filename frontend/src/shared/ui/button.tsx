import {ButtonHTMLAttributes} from 'react';
import {cn} from '../lib/cn';

type ButtonVariant = 'primary' | 'secondary' | 'danger';
type ButtonSize = 'sm' | 'md';

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
    variant?: ButtonVariant;
    size?: ButtonSize;
}

const variantClasses: Record<ButtonVariant, string> = {
    primary: 'bg-slate-900 text-white hover:bg-slate-700 disabled:bg-slate-400',
    secondary: 'border border-slate-300 bg-white text-slate-800 hover:border-slate-400 disabled:text-slate-400',
    danger: 'bg-red-600 text-white hover:bg-red-500 disabled:bg-red-300'
};

const sizeClasses: Record<ButtonSize, string> = {
    sm: 'h-8 px-3 text-sm',
    md: 'h-10 px-4 text-sm'
};

export function Button({className, variant = 'secondary', size = 'md', ...props}: ButtonProps) {
    return (
        <button
            className={cn(
                'inline-flex items-center justify-center rounded-xl font-semibold transition focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-slate-600 disabled:cursor-not-allowed',
                variantClasses[variant],
                sizeClasses[size],
                className
            )}
            {...props}
        />
    );
}
