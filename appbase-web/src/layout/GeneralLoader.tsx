import { useAtom } from 'jotai';
import { loaderAtom } from '../atoms/loaderAtom';

export const GeneralLoader = ({ forceVisible = false }: { forceVisible?: boolean }) => {
    const [isLoading] = useAtom(loaderAtom);

    if (!isLoading && !forceVisible) return null;

    return (
        <div style={{
            position: 'fixed',
            top: 0,
            left: 0,
            width: '100vw',
            height: '100vh',
            backgroundColor: 'rgba(0,0,0,0.5)',
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            zIndex: 9999
        }}>
            <div style={{
                width: '60px',
                height: '60px',
                border: '8px solid #fff',
                borderTop: '8px solid #000',
                borderRadius: '50%',
                animation: 'spin 1s linear infinite'
            }} />
            <style>
                {`
          @keyframes spin {
            0% { transform: rotate(0deg);}
            100% { transform: rotate(360deg);}
          }
        `}
            </style>
        </div>
    );
};