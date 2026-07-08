"use client";

import { useEffect, useState } from "react";

import { apiFetch } from "@/lib/backend/client";

import type { components } from "@/lib/backend/apiV1/schema";
import { ratingColor } from "@/lib/ratingColor";

import RatingValue from "@/app/_components/RatingValue";
import RoughButton from "@/app/_components/RoughButton";

type BookDto = components["schemas"]["BookDto"];
type ReviewDto = components["schemas"]["ReviewDto"];

export default function ReviewAdmin() {
  const [books, setBooks] = useState<BookDto[] | null>(null);
  const [selectedBookId, setSelectedBookId] = useState<number | null>(null);
  const [reviews, setReviews] = useState<ReviewDto[] | null>(null);

  useEffect(() => {
    apiFetch(`/api/v1/books`).then((data: BookDto[]) => {
      setBooks(data);
      if (data.length > 0) setSelectedBookId(data[0].id);
    });
  }, []);

  const loadReviews = (bookId: number) => {
    apiFetch(`/api/v1/reviews/book/${bookId}`).then(setReviews);
  };

  useEffect(() => {
    if (selectedBookId == null) return;
    loadReviews(selectedBookId);
  }, [selectedBookId]);

  const handleDelete = (reviewId: number) => {
    if (!confirm("이 리뷰를 삭제하시겠습니까?")) return;

    apiFetch(`/api/v1/reviews/${reviewId}`, { method: "DELETE" })
      .then((data) => {
        alert(data.message);
        if (selectedBookId != null) loadReviews(selectedBookId);
      })
      .catch((error) => {
        alert(`${error.resultCode} : ${error.message}`);
      });
  };

  if (books == null) return <div>로딩중...</div>;

  return (
    <div className="flex flex-col gap-4">
      <label className="flex items-center gap-2 text-sm">
        도서 선택
        <select
          className="sketch-input p-1"
          value={selectedBookId ?? ""}
          onChange={(e) => setSelectedBookId(Number(e.target.value))}
        >
          {books.map((book) => (
            <option key={book.id} value={book.id}>
              {book.title}
            </option>
          ))}
        </select>
      </label>

      {reviews == null && <div>로딩중...</div>}

      {reviews != null && reviews.length === 0 && (
        <div className="text-sm theme-muted">리뷰가 없습니다.</div>
      )}

      {reviews != null && reviews.length > 0 && (
        <ul className="sketch-panel flex flex-col p-2">
          {reviews.map((review) => (
            <li
              key={review.id}
              className="flex items-start gap-3 border-b py-2 last:border-b-0"
            >
              <div className="min-w-0 flex-1">
                <div className="text-sm font-semibold">
                  {review.reviewer.githubId ?? "탈퇴한 사용자"}
                </div>
                <div className="text-sm">{review.content}</div>
                <div className="flex flex-wrap gap-1 text-xs theme-tag">
                  {review.tags.map((tag) => (
                    <span key={tag}>#{tag}</span>
                  ))}
                </div>
              </div>
              <span
                className={`shrink-0 font-bold ${ratingColor(review.rating)}`}
              >
                <RatingValue rating={review.rating} />
              </span>
              <RoughButton
                roughSize="sm"
                tone="cancel"
                type="button"
                onClick={() => handleDelete(review.id)}
              >
                삭제
              </RoughButton>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
