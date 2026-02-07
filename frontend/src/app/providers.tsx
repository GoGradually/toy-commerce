import {QueryClient, QueryClientProvider} from '@tanstack/react-query';
import {ReactNode} from 'react';
import {MemberProvider} from '../shared/member/member-context';

const queryClient = new QueryClient({
    defaultOptions: {
        queries: {
            staleTime: 10_000,
            retry: 1,
            refetchOnWindowFocus: false
        }
    }
});

interface AppProvidersProps {
    children: ReactNode;
}

export function AppProviders({children}: AppProvidersProps) {
    return (
        <QueryClientProvider client={queryClient}>
            <MemberProvider>{children}</MemberProvider>
        </QueryClientProvider>
    );
}
