"use client";

import {
  createContext,
  useContext,
  useEffect,
  useMemo,
  useSyncExternalStore,
} from "react";

type Theme = "light" | "dark";

type ThemeContextType = {
  theme: Theme;
  toggleTheme: () => void;
};

const THEME_STORAGE_KEY = "readthem-theme";
const THEME_CHANGE_EVENT = "readthem-theme-change";
const ThemeContext = createContext<ThemeContextType | null>(null);

function applyTheme(theme: Theme) {
  document.documentElement.dataset.theme = theme;
}

function getPreferredTheme(): Theme {
  if (typeof window === "undefined") {
    return "light";
  }

  const storedTheme = window.localStorage.getItem(THEME_STORAGE_KEY);
  if (storedTheme === "light" || storedTheme === "dark") {
    return storedTheme;
  }

  return window.matchMedia("(prefers-color-scheme: dark)").matches
    ? "dark"
    : "light";
}

function getThemeSnapshot(): Theme {
  return getPreferredTheme();
}

function subscribe(callback: () => void) {
  window.addEventListener("storage", callback);
  window.addEventListener(THEME_CHANGE_EVENT, callback);

  const mediaQuery = window.matchMedia("(prefers-color-scheme: dark)");
  mediaQuery.addEventListener("change", callback);

  return () => {
    window.removeEventListener("storage", callback);
    window.removeEventListener(THEME_CHANGE_EVENT, callback);
    mediaQuery.removeEventListener("change", callback);
  };
}

export function ThemeProvider({ children }: { children: React.ReactNode }) {
  const theme = useSyncExternalStore(
    subscribe,
    getThemeSnapshot,
    (): Theme => "light",
  );

  useEffect(() => {
    applyTheme(theme);
  }, [theme]);

  const value = useMemo(
    () => ({
      theme,
      toggleTheme: () => {
        const nextTheme = theme === "light" ? "dark" : "light";
        window.localStorage.setItem(THEME_STORAGE_KEY, nextTheme);
        window.dispatchEvent(new Event(THEME_CHANGE_EVENT));
      },
    }),
    [theme],
  );

  return (
    <ThemeContext.Provider value={value}>{children}</ThemeContext.Provider>
  );
}

export function useTheme() {
  const context = useContext(ThemeContext);

  if (context == null) {
    throw new Error("useTheme는 ThemeProvider 내부에서만 사용할 수 있습니다.");
  }

  return context;
}
