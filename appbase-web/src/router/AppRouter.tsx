import { useEffect, useLayoutEffect } from 'react';
import { createBrowserRouter, RouterProvider } from 'react-router-dom';
import { App } from '../features/app/App';
import { OnlyAdmin } from '../features/app/view-only-admin/OnlyAdmin';
import { OnlyUser } from '../features/app/view-only-user/OnlyUser';
import { Profile } from '../features/app/profile/Profile';
import { Users } from '../features/app/users/Users';
import { useIdleLogout } from '../features/auth/hooks/useIdleLogout';
import Login from '../features/auth/Login';
import Register from '../features/auth/Register';
import { ResetPassword } from '../features/auth/ResetPassword';
import { Home } from '../features/home/Home';
import i18n from '../utils/i18n';
import { PrivateRoute } from './PrivateRoute';
import { RoleRoute } from './RoleRoute';
import { ViewAdminUser } from '../features/app/view-admin-user/ViewAdminUser';

type ThemeOption = "light" | "dark" | "system";

export const AppRouter = () => {
  useIdleLogout();

  const applyTheme = (appliedTheme: ThemeOption) => {
    const finalTheme =
      appliedTheme === "system"
        ? window.matchMedia("(prefers-color-scheme: dark)").matches
          ? "dark"
          : "light"
        : appliedTheme;

    if (finalTheme === "dark") document.documentElement.classList.add("dark");
    else document.documentElement.classList.remove("dark");
  };

  useLayoutEffect(() => {
    const storedTheme = (localStorage.getItem("selectedTheme") as ThemeOption) || "system";
    applyTheme(storedTheme);
  }, []);

  useEffect(() => {
    const storedLang = localStorage.getItem("selectedLanguage") as "es_ES" | "en_GB";
    if (storedLang) i18n.changeLanguage(storedLang);
  }, []);

  const renderMultiRoutes = ({ element: Element, paths, ...rest }: { element: React.ReactElement; paths: string[];[key: string]: unknown }) => paths.map((path: string) => {
    return {
      ...rest,
      path, element: Element
    }
  });

  const router = createBrowserRouter([
    ...renderMultiRoutes({ paths: ["/home", "/"], element: <Home /> }),
    { path: "/login", element: <Login /> },
    { path: "/register", element: <Register /> },
    { path: "/reset-password", element: <ResetPassword /> },
    {
      element: <PrivateRoute />,
      children: [
        {
          element: <App />,
          children: [
            { path: "/profile", element: <Profile /> },
            {
              element: <RoleRoute allowedRoles={["ROLE_ADMIN"]} />,
              children: [
                { path: "/users", element: <Users /> },
                { path: "/only-admin", element: <OnlyAdmin /> },
              ]
            },
            {
              element: <RoleRoute allowedRoles={["ROLE_USER"]} />,
              children: [
                { path: "/only-user", element: <OnlyUser /> },
              ]
            },
            {
              element: <RoleRoute allowedRoles={["ROLE_ADMIN", "ROLE_USER"]} />,
              children: [
                { path: "/admin-user", element: <ViewAdminUser /> },
              ]
            },
          ]
        }
      ]
    }
  ]);

  return <RouterProvider router={router} />
}
