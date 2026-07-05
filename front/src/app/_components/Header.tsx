"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";

import { useState } from "react";

import { useAuth } from "@/lib/auth/AuthProvider";

import RoughButton from "@/app/_components/RoughButton";
import RoughDivider from "@/app/_components/RoughDivider";
import { RoughInput } from "@/app/_components/RoughInput";

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
    <header className="relative">
      <nav className="mx-auto flex w-full max-w-4xl items-center justify-between gap-2 px-4 py-2">
        <Link href="/" className="shrink-0 text-xl font-bold">
          READTHEM.md
        </Link>

        <form
          className="flex max-w-xs flex-1 items-center gap-1"
          onSubmit={handleSearch}
        >
          <label className="flex-1">
            <RoughInput
              type="text"
              placeholder="제목/저자 검색"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              maxLength={100}
            />
          </label>
          <RoughButton className="shrink-0 px-2" roughSize="sm" type="submit">
            검색
          </RoughButton>
        </form>

        <div className="flex items-center gap-2 shrink-0">
          <Link href="/" className="p-2 rounded hover:bg-gray-100">
            도서 목록
          </Link>

          {isLoginMemberPending && <span className="px-2 py-1">...</span>}

          {!isLoginMemberPending && !isLogin && (
            <>
              <a
                href={`${process.env.NEXT_PUBLIC_API_BASE_URL}/oauth2/authorization/github?redirectUrl=${process.env.NEXT_PUBLIC_FRONTEND_BASE_URL}`}
                className="rough-github-link"
                aria-label="GitHub로 로그인"
              >
                {/* eslint-disable-next-line @next/next/no-img-element */}
                <img className="rough-github-image" src="/github.svg" alt="" />
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
      <div className="mx-auto w-full max-w-4xl px-1">
        <RoughDivider list={false} />
      </div>
    </header>
  );
}
