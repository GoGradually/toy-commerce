import {NavLink, Outlet} from 'react-router-dom';
import {MemberSwitcher} from './member-switcher';

const navItems = [
    {to: '/', label: '쇼핑'},
    {to: '/wishlist/ranking', label: '위시리스트 랭킹'},
    {to: '/cart', label: '장바구니'},
    {to: '/admin/products', label: '관리자'}
];

export function AppShell() {
    return (
        <div
            className="min-h-screen bg-slate-100 bg-[radial-gradient(circle_at_top_left,_rgba(143,173,51,0.2),_transparent_40%),radial-gradient(circle_at_bottom_right,_rgba(15,23,42,0.18),_transparent_45%)] text-slate-900">
            <header className="border-b border-slate-200 bg-white/80 backdrop-blur-md">
                <div
                    className="mx-auto flex w-full max-w-6xl flex-col gap-4 px-4 py-4 md:flex-row md:items-center md:justify-between">
                    <div>
                        <p className="text-xs font-semibold uppercase tracking-[0.22em] text-slate-500">토이 커머스</p>
                        <h1 className="text-2xl font-bold">실습 대시보드</h1>
                    </div>
                    <MemberSwitcher/>
                </div>
                <nav className="mx-auto flex w-full max-w-6xl gap-2 px-4 pb-4">
                    {navItems.map((item) => (
                        <NavLink
                            key={item.to}
                            to={item.to}
                            className={({isActive}) =>
                                `rounded-full px-4 py-2 text-sm font-medium transition ${
                                    isActive
                                        ? 'bg-slate-900 text-white'
                                        : 'bg-white text-slate-600 hover:bg-accent-100 hover:text-slate-900'
                                }`
                            }
                            end={item.to === '/'}
                        >
                            {item.label}
                        </NavLink>
                    ))}
                </nav>
            </header>
            <main className="mx-auto w-full max-w-6xl px-4 py-8">
                <Outlet/>
            </main>
        </div>
    );
}
