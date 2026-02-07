import {QueryClient, QueryClientProvider} from '@tanstack/react-query';
import {ReactElement} from 'react';
import {MemoryRouter, Route, Routes} from 'react-router-dom';
import {render} from '@testing-library/react';
import {MemberProvider} from '../shared/member/member-context';

interface ExtraRoute {
    path: string;
    element: ReactElement;
}

interface RenderPageOptions {
    path: string;
    element: ReactElement;
    initialEntry: string;
    extraRoutes?: ExtraRoute[];
}

export function renderPage({path, element, initialEntry, extraRoutes = []}: RenderPageOptions) {
    const queryClient = new QueryClient({
        defaultOptions: {
            queries: {
                retry: false,
                refetchOnWindowFocus: false
            },
            mutations: {
                retry: false
            }
        }
    });

    return render(
        <QueryClientProvider client={queryClient}>
            <MemberProvider>
                <MemoryRouter initialEntries={[initialEntry]}>
                    <Routes>
                        <Route path={path} element={element}/>
                        {extraRoutes.map((route) => (
                            <Route key={route.path} path={route.path} element={route.element}/>
                        ))}
                    </Routes>
                </MemoryRouter>
            </MemberProvider>
        </QueryClientProvider>
    );
}
