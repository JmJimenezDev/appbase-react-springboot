import { Navigate, Outlet } from "react-router-dom";
import { useEffect } from "react";
import { useAuth } from "../features/auth/hooks/useAuth";
import { GeneralLoader } from "../layout/GeneralLoader";

export const PrivateRoute = () => {
    const { isAuthenticated, initialize, loading } = useAuth();

    useEffect(() => {
        initialize();
    }, [initialize]);

    if (loading) return <GeneralLoader forceVisible={true} />;

    if (!isAuthenticated) return <Navigate to="/login" replace />;

    return <Outlet />;
};