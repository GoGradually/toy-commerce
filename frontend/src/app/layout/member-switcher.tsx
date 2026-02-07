import {FormEvent, useEffect, useState} from 'react';
import {useMember} from '../../shared/member/member-context';
import {Button} from '../../shared/ui/button';
import {Input} from '../../shared/ui/input';

const quickMembers = [1, 2, 1001];

export function MemberSwitcher() {
    const {memberId, setMemberId} = useMember();
    const [draftValue, setDraftValue] = useState(String(memberId));

    useEffect(() => {
        setDraftValue(String(memberId));
    }, [memberId]);

    const submit = (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        const nextValue = Number.parseInt(draftValue, 10);
        if (Number.isFinite(nextValue) && nextValue >= 1) {
            setMemberId(nextValue);
        }
    };

    return (
        <div className="rounded-2xl border border-slate-200 bg-white p-3 shadow-panel">
            <p className="text-xs font-semibold uppercase tracking-[0.2em] text-slate-500">회원 헤더</p>
            <form className="mt-2 flex items-center gap-2" onSubmit={submit}>
                <Input
                    className="w-24"
                    min={1}
                    type="number"
                    value={draftValue}
                    onChange={(event) => setDraftValue(event.target.value)}
                />
                <Button type="submit" variant="primary">
                    적용
                </Button>
            </form>
            <div className="mt-2 flex gap-2">
                {quickMembers.map((id) => (
                    <Button key={id} type="button" variant={id === memberId ? 'primary' : 'secondary'}
                            onClick={() => setMemberId(id)}>
                        {id}
                    </Button>
                ))}
            </div>
        </div>
    );
}
