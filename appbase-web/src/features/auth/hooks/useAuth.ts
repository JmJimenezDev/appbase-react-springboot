import { useAtom } from "jotai";
import { useCallback, useRef, useState } from "react";
import { api } from "../../../utils/axios";
import type { LoggedUserDTO, LoginRequestDTO, RegisterRequestDTO } from "../types/auth.types";
import { authAtom } from "../../../atoms/authAtom";

export const useAuth = () => {
  const [auth, setAuth] = useAtom(authAtom);
  const [loading, setLoading] = useState(true);

  const initializePromise = useRef<Promise<void> | null>(null);

  const initialize = useCallback(async () => {
    if (!initializePromise.current) {
      setLoading(true);
      initializePromise.current = (async () => {
        try {
          const response = await api.get<LoggedUserDTO>("/auth/profile");
          setAuth({ isAuthenticated: true, user: response.data });
        } catch {
          setAuth({ isAuthenticated: false, user: null });
        } finally {
          setLoading(false);
        }
      })();
    }
    return initializePromise.current;
  }, [setAuth]);

  const login = useCallback(
    async (credentials: LoginRequestDTO) => {
      await api.post("/auth/login", credentials);

      const response = await api.get<LoggedUserDTO>("/auth/profile");
      setAuth({ isAuthenticated: true, user: response.data });
    },
    [setAuth],
  );

  const register = useCallback(
    async (data: RegisterRequestDTO) => {
      await api.post("/auth/register", data);

      const response = await api.get<LoggedUserDTO>("/auth/profile");
      setAuth({ isAuthenticated: true, user: response.data });
    },
    [setAuth],
  );

  const logout = useCallback(async () => {
    try {
      await api.post("/auth/logout");
    } finally {
      setAuth({ isAuthenticated: false, user: null });
    }
  }, [setAuth]);

  const refresh = useCallback(async () => {
    try {
      await api.post("/auth/refresh");
      const response = await api.get<LoggedUserDTO>("/auth/profile");
      setAuth({ isAuthenticated: true, user: response.data });
      return true;
    } catch {
      setAuth({ isAuthenticated: false, user: null });
      return false;
    }
  }, [setAuth]);

  return {
    ...auth,
    setAuth,
    loading,
    login,
    initialize,
    logout,
    register,
    refresh,
  };
};
