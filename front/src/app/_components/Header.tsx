"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";

import { useState } from "react";

import { useAuth } from "@/lib/auth/AuthProvider";

export default function Header() {
  const router = useRouter();
  const { loginMember, isLogin, isLoginMemberPending, logout } = useAuth();
  const [searchTerm, setSearchTerm] = useState("");

  const handleLogout = () => {
    logout().then(() => {
      router.replace(`/`);
    });
  };

  const handleSearch = (e: React.SyntheticEvent<HTMLFormElement>) => {
    e.preventDefault();

    const term = searchTerm.trim();
    if (term.length === 0) return;

    router.push(`/books/search?searchTerm=${encodeURIComponent(term)}`);
  };

  return (
    <header className="border-b">
      <nav className="flex items-center justify-between gap-2 mx-auto w-full max-w-4xl px-4 py-2">
        <Link href="/" className="text-xl font-bold shrink-0">
          READTHEM.md
        </Link>

        <form className="flex gap-1 flex-1 max-w-xs" onSubmit={handleSearch}>
          <input
            className="border rounded px-2 py-1 text-sm w-full"
            type="text"
            placeholder="제목/저자 검색"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            maxLength={100}
          />
          <button
            className="border rounded px-2 py-1 text-sm shrink-0"
            type="submit"
          >
            검색
          </button>
        </form>

        <div className="flex items-center gap-2 shrink-0">
          <Link href="/" className="p-2 rounded hover:bg-gray-100">
            도서 목록
          </Link>

          {isLoginMemberPending && <span className="p-2">...</span>}

          {!isLoginMemberPending && !isLogin && (
            <>
              <a
                href={`${process.env.NEXT_PUBLIC_API_BASE_URL}/oauth2/authorization/github?redirectUrl=${process.env.NEXT_PUBLIC_FRONTEND_BASE_URL}`}
              >
                깃허브
              </a>
              <Link
                href="/members/login"
                className="p-2 rounded hover:bg-gray-100"
              >
                로그인
              </Link>
              <Link
                href="/members/join"
                className="p-2 rounded hover:bg-gray-100"
              >
                회원가입
              </Link>
            </>
          )}

          {!isLoginMemberPending && isLogin && (
            <>
              <span className="p-2">{loginMember?.username}님</span>
              <Link href="/mypage" className="p-2 rounded hover:bg-gray-100">
                마이페이지
              </Link>
              <button
                type="button"
                className="p-2 rounded hover:bg-gray-100"
                onClick={handleLogout}
              >
                로그아웃
              </button>
            </>
          )}
        </div>
      </nav>
    </header>
  );
}
