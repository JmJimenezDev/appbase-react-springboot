import { Navigate, Outlet } from "react-router-dom";
import { useAuth } from "../features/auth/hooks/useAuth";

interface RoleRouteProps {
    allowedRoles: string[];
}

export const RoleRoute = ({ allowedRoles }: RoleRouteProps) => {
    const { user, isAuthenticated } = useAuth();

    if (!isAuthenticated || !user)
        return <Navigate to="/login" replace />;

    const hasRole = user.roles.some(role => allowedRoles.includes(role));

    if (!hasRole)
        return <Navigate to="/profile" replace />;

    return <Outlet />;
};