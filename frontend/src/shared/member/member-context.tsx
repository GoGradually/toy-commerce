import {createContext, ReactNode, useContext, useMemo, useState} from 'react';

const STORAGE_KEY = 'toy_member_id';
const DEFAULT_MEMBER_ID = 1;

interface MemberContextValue {
    memberId: number;
    setMemberId: (nextMemberId: number) => void;
}

const MemberContext = createContext<MemberContextValue | null>(null);

function normalizeMemberId(value: number): number {
    if (!Number.isFinite(value) || value < 1) {
        return DEFAULT_MEMBER_ID;
    }

    return Math.floor(value);
}

function readInitialMemberId(): number {
    if (typeof window === 'undefined') {
        return DEFAULT_MEMBER_ID;
    }

    const raw = localStorage.getItem(STORAGE_KEY);
    if (!raw) {
        return DEFAULT_MEMBER_ID;
    }

    const parsed = Number.parseInt(raw, 10);
    return normalizeMemberId(parsed);
}

interface MemberProviderProps {
    children: ReactNode;
}

export function MemberProvider({children}: MemberProviderProps) {
    const [memberId, setMemberIdState] = useState(readInitialMemberId);

    const setMemberId = (nextMemberId: number) => {
        const normalized = normalizeMemberId(nextMemberId);
        setMemberIdState(normalized);
        if (typeof window !== 'undefined') {
            localStorage.setItem(STORAGE_KEY, String(normalized));
        }
    };

    const value = useMemo(
        () => ({
            memberId,
            setMemberId
        }),
        [memberId]
    );

    return <MemberContext.Provider value={value}>{children}</MemberContext.Provider>;
}

export function useMember() {
    const context = useContext(MemberContext);
    if (!context) {
        throw new Error('useMember must be used within MemberProvider.');
    }

    return context;
}
