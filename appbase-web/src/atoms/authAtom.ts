import { atom } from "jotai";
import type { LoggedUserDTO } from "../features/auth/types/auth.types";

export const authAtom = atom<{
  isAuthenticated: boolean;
  user: LoggedUserDTO | null;
}>({
  isAuthenticated: false,
  user: null,
});
