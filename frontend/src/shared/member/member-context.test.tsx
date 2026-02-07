import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {describe, expect, it} from 'vitest';
import {MemberProvider, useMember} from './member-context';

function Probe() {
    const {memberId, setMemberId} = useMember();

    return (
        <div>
            <span data-testid="member-id">{memberId}</span>
            <button type="button" onClick={() => setMemberId(7)}>
                update
            </button>
        </div>
    );
}

describe('MemberProvider', () => {
    it('defaults to member id 1 and persists update', async () => {
        const user = userEvent.setup();
        render(
            <MemberProvider>
                <Probe/>
            </MemberProvider>
        );

        expect(screen.getByTestId('member-id')).toHaveTextContent('1');

        await user.click(screen.getByRole('button', {name: 'update'}));

        expect(screen.getByTestId('member-id')).toHaveTextContent('7');
        expect(localStorage.getItem('toy_member_id')).toBe('7');
    });

    it('reads stored member id from localStorage', () => {
        localStorage.setItem('toy_member_id', '13');

        render(
            <MemberProvider>
                <Probe/>
            </MemberProvider>
        );

        expect(screen.getByTestId('member-id')).toHaveTextContent('13');
    });
});
