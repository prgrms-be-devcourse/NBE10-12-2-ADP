"use client";

import { createContext, useContext, useEffect, useState } from "react";

import { apiFetch } from "@/lib/backend/client";

import type { components } from "@/lib/backend/apiV1/schema";

type LoginMember = components["schemas"]["MemberWithUsernameAndWidgetLinkDto"];

type AuthContextType = {
  loginMember: LoginMember | null;
  isLogin: boolean;
  isLoginMemberPending: boolean;
  refresh: () => Promise<void>;
  logout: () => Promise<void>;
};

const AuthContext = createContext<AuthContextType | null>(null);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [loginMember, setLoginMember] = useState<LoginMember | null>(null);
  const [isLoginMemberPending, setIsLoginMemberPending] = useState(true);

  const refresh = () => {
    return apiFetch(`/api/v1/members/me`)
      .then((data) => setLoginMember(data))
      .catch(() => setLoginMember(null))
      .finally(() => setIsLoginMemberPending(false));
  };

  useEffect(() => {
    refresh();
  }, []);

  const logout = () => {
    return apiFetch(`/api/v1/members/logout`, { method: "DELETE" }).then(() => {
      setLoginMember(null);
    });
  };

  return (
    <AuthContext.Provider
      value={{
        loginMember,
        isLogin: loginMember != null,
        isLoginMemberPending,
        refresh,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);

  if (context == null) {
    throw new Error("useAuth는 AuthProvider 내부에서만 사용할 수 있습니다.");
  }

  return context;
}
