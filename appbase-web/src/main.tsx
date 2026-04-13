import { Provider } from 'jotai';
import { Suspense } from 'react';
import { createRoot } from 'react-dom/client';
import { ToastContainer } from 'react-toastify';
import './styles/index.css';
import { AppRouter } from './router/AppRouter';
import { ReactQueryProvider } from './providers/ReactQueryProvider';
import { I18nextProvider } from 'react-i18next';
import i18n from './utils/i18n';
import { GeneralLoader } from './layout/GeneralLoader';

createRoot(document.getElementById('root')!).render(
  <Provider>
    <Suspense fallback={<GeneralLoader forceVisible={true} />}>
      <I18nextProvider i18n={i18n}>
        <ReactQueryProvider>
          <GeneralLoader />
          <AppRouter />
        </ReactQueryProvider>
        <ToastContainer limit={3} />
      </I18nextProvider>
    </Suspense>
  </Provider>
);