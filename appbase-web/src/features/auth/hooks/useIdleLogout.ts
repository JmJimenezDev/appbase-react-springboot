import { useEffect, useRef, useCallback } from "react";
import { useAuth } from "../hooks/useAuth";
import { useAtom } from "jotai";
import { toast } from "react-toastify";
import { authAtom } from "../../../atoms/authAtom";

export const useIdleLogout = (
  timeoutMs: number = 15 * 60 * 1000,
  warningMs: number = 60 * 1000
) => {
  const { logout } = useAuth();
  const [auth] = useAtom(authAtom);

  const logoutTimerRef = useRef<number | null>(null);
  const warningTimerRef = useRef<number | null>(null);
  const toastIdRef = useRef<string | number | null>(null);

  const clearTimers = () => {
    if (logoutTimerRef.current) clearTimeout(logoutTimerRef.current);
    if (warningTimerRef.current) clearTimeout(warningTimerRef.current);
  };

  const showWarning = () => {
    if (toastIdRef.current) return;

    toastIdRef.current = toast.warn(
      "Tu sesión está a punto de expirar por inactividad",
      {
        autoClose: false,
        closeOnClick: true,
        draggable: true,
      }
    );
  };

  const resetTimer = useCallback(() => {
    clearTimers();

    if (toastIdRef.current) {
      toast.dismiss(toastIdRef.current);
      toastIdRef.current = null;
    }

    warningTimerRef.current = window.setTimeout(() => {
      showWarning();
    }, timeoutMs - warningMs);

    logoutTimerRef.current = window.setTimeout(() => {
      logout();
      toast.dismiss();
    }, timeoutMs);
  }, [logout, timeoutMs, warningMs]);

  useEffect(() => {
    if (!auth.isAuthenticated) return;

    const events = ["mousemove", "keydown", "scroll", "click", "touchstart"];

    events.forEach((e) => window.addEventListener(e, resetTimer));

    resetTimer();

    return () => {
      events.forEach((e) => window.removeEventListener(e, resetTimer));
      clearTimers();
    };
  }, [auth.isAuthenticated, resetTimer]);
};