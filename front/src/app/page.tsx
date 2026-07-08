"use client";

import { useEffect, useState } from "react";

import { apiFetch } from "@/lib/backend/client";

import type { components } from "@/lib/backend/apiV1/schema";

import BookGrid from "@/app/_components/BookGrid";

type BookDto = components["schemas"]["BookDto"];

export default function Page() {
  const [books, setBooks] = useState<BookDto[] | null>(null);
  const [loadError, setLoadError] = useState<string | null>(null);

  useEffect(() => {
    apiFetch(`/api/v1/books/rank?type=reviewCount&page=0&size=10`)
      .then((data) => {
        setLoadError(null);
        setBooks(data);
      })
      .catch((error) => {
        setLoadError(`${error.resultCode} : ${error.message}`);
      });
  }, []);

  if (loadError != null) {
    return (
      <div>오류가 발생했습니다: {loadError} (백엔드 확인이 필요합니다)</div>
    );
  }

  if (books == null) return <div>로딩중...</div>;

  return (
    <>
      <h1 className="mt-4 mb-2 text-3xl font-bold">인기 도서</h1>
      <BookGrid books={books} layout="horizontal" />
    </>
  );
}
