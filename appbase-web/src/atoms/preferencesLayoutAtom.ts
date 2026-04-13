import { atom } from "jotai";

export type SidebarSize = "EXPANDED" | "COLLAPSED";

interface PreferencesLayout {
  size: SidebarSize;
}

export const preferencesLayoutAtom = atom<PreferencesLayout>({
  size: "EXPANDED",
});
