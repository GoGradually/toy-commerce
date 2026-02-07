interface StatePanelProps {
    title: string;
    description?: string;
}

export function StatePanel({title, description}: StatePanelProps) {
    return (
        <div className="rounded-2xl border border-dashed border-slate-300 bg-white/80 p-6 text-center">
            <p className="text-base font-semibold text-slate-800">{title}</p>
            {description ? <p className="mt-1 text-sm text-slate-600">{description}</p> : null}
        </div>
    );
}
