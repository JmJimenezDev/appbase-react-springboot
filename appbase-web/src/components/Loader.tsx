import React from 'react';

interface LoaderProps {
    isLoading: boolean;
    minHeight?: string | number;
    children: React.ReactNode;
}

export const Loader = ({ isLoading, minHeight = '200px', children }: LoaderProps) => {
    if (isLoading) {
        return (
            <div
                style={{
                    position: 'relative',
                    minHeight,
                    display: 'flex',
                    justifyContent: 'center',
                    alignItems: 'center',
                }}
            >
                <div
                    style={{
                        width: '50px',
                        height: '50px',
                        border: '6px solid #ccc',
                        borderTop: '6px solid #333',
                        borderRadius: '50%',
                        animation: 'spin 1s linear infinite',
                    }}
                />
                <style>
                    {`
            @keyframes spin {
              0% { transform: rotate(0deg); }
              100% { transform: rotate(360deg); }
            }
          `}
                </style>
            </div>
        );
    }

    return <>{children}</>;
};