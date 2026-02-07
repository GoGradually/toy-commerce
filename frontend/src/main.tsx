import './shared/api/generated-client-setup';
import ReactDOM from 'react-dom/client';
import {RouterProvider} from 'react-router-dom';
import {AppProviders} from './app/providers';
import {router} from './app/router';
import './app/styles.css';

ReactDOM.createRoot(document.getElementById('root')!).render(
    <AppProviders>
        <RouterProvider router={router}/>
    </AppProviders>
);
