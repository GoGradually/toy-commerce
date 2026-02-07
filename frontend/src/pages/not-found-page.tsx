import {Link} from 'react-router-dom';
import {Button} from '../shared/ui/button';
import {StatePanel} from '../shared/ui/state-panel';

export function NotFoundPage() {
    return (
        <div className="space-y-4">
            <StatePanel title="페이지를 찾을 수 없습니다" description="요청한 경로가 없습니다."/>
            <Link to="/">
                <Button variant="secondary">홈으로 돌아가기</Button>
            </Link>
        </div>
    );
}
