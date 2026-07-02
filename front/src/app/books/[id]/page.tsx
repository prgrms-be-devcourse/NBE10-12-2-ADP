"use client";

import Link from "next/link";
import { useParams } from "next/navigation";

import { useEffect, useState } from "react";

import { apiFetch } from "@/lib/backend/client";

import { useAuth } from "@/lib/auth/AuthProvider";
import type { components } from "@/lib/backend/apiV1/schema";
import { ratingColor } from "@/lib/ratingColor";

import Avatar from "@/app/_components/Avatar";

type BookDetailDto = components["schemas"]["BookDetailDto"];
type ReviewDto = components["schemas"]["ReviewDto"];

const RATING_BUCKETS = [
  "5.0",
  "4.5",
  "4.0",
  "3.5",
  "3.0",
  "2.5",
  "2.0",
  "1.5",
  "1.0",
  "0.5",
];

function RatingHistogram({ rating }: { rating: Record<string, unknown> }) {
  const counts = RATING_BUCKETS.map((bucket) => Number(rating[bucket] ?? 0));
  const maxCount = Math.max(1, ...counts);

  return (
    <div className="flex flex-col gap-1 max-w-xs mt-1">
      {RATING_BUCKETS.map((bucket, i) => (
        <div key={bucket} className="flex items-center gap-2 text-xs">
          <span className="w-7 text-right text-gray-500">{bucket}</span>
          <div className="flex-1 h-4 bg-gray-100 rounded-r">
            <div
              className="h-full bg-blue-500 rounded-r"
              style={{ width: `${(counts[i] / maxCount) * 100}%` }}
            />
          </div>
          <span className="w-4 text-gray-500">{counts[i]}</span>
        </div>
      ))}
    </div>
  );
}

export default function Page() {
  const { id } = useParams<{ id: string }>();
  const { loginMember, isLogin } = useAuth();

  const [book, setBook] = useState<BookDetailDto | null>(null);
  const [reviews, setReviews] = useState<ReviewDto[] | null>(null);
  const [editingReviewId, setEditingReviewId] = useState<number | null>(null);
  const [loadError, setLoadError] = useState<string | null>(null);
  const [showWriteForm, setShowWriteForm] = useState(false);

  const loadBook = () => {
    apiFetch(`/api/v1/books/${id}`)
      .then((data) => {
        setLoadError(null);
        setBook(data);
      })
      .catch((error) => {
        setLoadError(`${error.resultCode} : ${error.message}`);
      });
  };

  const loadReviews = () => {
    apiFetch(`/api/v1/reviews/book/${id}`)
      .then((data) => {
        setLoadError(null);
        setReviews(data);
      })
      .catch((error) => {
        setLoadError(`${error.resultCode} : ${error.message}`);
      });
  };

  useEffect(() => {
    loadBook();
    loadReviews();
  }, [id]);

  const extractReviewFields = (form: HTMLFormElement) => {
    const ratingInput = form.elements.namedItem("rating") as HTMLInputElement;
    const contentInput = form.elements.namedItem(
      "content",
    ) as HTMLTextAreaElement;
    const tagsInput = form.elements.namedItem("tags") as HTMLInputElement;

    contentInput.value = contentInput.value.trim();

    if (contentInput.value.length < 2) {
      alert("리뷰 내용을 2자 이상 입력해주세요.");
      contentInput.focus();
      return null;
    }

    if (contentInput.value.length > 30) {
      alert("리뷰 내용은 30자 이하로 입력해주세요.");
      contentInput.focus();
      return null;
    }

    const tags = tagsInput.value
      .split(",")
      .map((tag) => tag.trim())
      .filter((tag) => tag.length > 0);

    return {
      rating: Number(ratingInput.value),
      content: contentInput.value,
      tags,
    };
  };

  const handleWriteSubmit = (e: React.SyntheticEvent<HTMLFormElement>) => {
    e.preventDefault();

    const form = e.currentTarget;
    const body = extractReviewFields(form);
    if (body == null) return;

    apiFetch(`/api/v1/reviews/book/${id}`, {
      method: "POST",
      body: JSON.stringify(body),
    })
      .then((data) => {
        alert(data.message);
        form.reset();
        setShowWriteForm(false);
        loadReviews();
        loadBook();
      })
      .catch((error) => {
        alert(`${error.resultCode} : ${error.message}`);
      });
  };

  const handleEditSubmit = (
    e: React.SyntheticEvent<HTMLFormElement>,
    reviewId: number,
  ) => {
    e.preventDefault();

    const form = e.currentTarget;
    const body = extractReviewFields(form);
    if (body == null) return;

    apiFetch(`/api/v1/reviews/${reviewId}`, {
      method: "PUT",
      body: JSON.stringify(body),
    })
      .then((data) => {
        alert(data.message);
        setEditingReviewId(null);
        loadReviews();
        loadBook();
      })
      .catch((error) => {
        alert(`${error.resultCode} : ${error.message}`);
      });
  };

  const handleToggleWish = () => {
    if (book == null) return;

    apiFetch(`/api/v1/wishes/book/${id}`, {
      method: book.isWished ? "DELETE" : "POST",
    })
      .then((data) => {
        alert(data.message);
        loadBook();
      })
      .catch((error) => {
        alert(`${error.resultCode} : ${error.message}`);
      });
  };

  const handleDelete = (reviewId: number) => {
    if (!confirm("리뷰를 삭제하시겠습니까?")) return;

    apiFetch(`/api/v1/reviews/${reviewId}`, {
      method: "DELETE",
    })
      .then((data) => {
        alert(data.message);
        loadReviews();
        loadBook();
      })
      .catch((error) => {
        alert(`${error.resultCode} : ${error.message}`);
      });
  };

  if (loadError != null) {
    return (
      <div>오류가 발생했습니다: {loadError} (백엔드 확인이 필요합니다)</div>
    );
  }

  if (book == null || reviews == null) return <div>로딩중...</div>;

  const average = book.rating?.["average"];
  const averageNumber = typeof average === "number" ? average : null;

  return (
    <div className="flex flex-col gap-6 p-4 max-w-3xl mx-auto w-full">
      <div className="flex gap-6">
        <div className="w-40 h-56 shrink-0 border rounded bg-gray-50 flex items-center justify-center overflow-hidden">
          {book.imgUrl ? (
            // eslint-disable-next-line @next/next/no-img-element
            <img
              src={book.imgUrl}
              alt={book.title}
              className="w-full h-full object-cover"
            />
          ) : (
            <span className="text-gray-400 text-sm">표지 없음</span>
          )}
        </div>

        <div className="flex flex-col gap-2">
          <h1 className="text-2xl font-bold">{book.title}</h1>
          <div className="text-sm text-gray-600">
            {book.authors.join(", ") || "-"} · {book.publisher} ·{" "}
            {book.publishedDate}
          </div>

          <div className="flex items-center gap-2">
            <span
              className={`text-lg font-bold ${
                averageNumber != null ? ratingColor(averageNumber) : ""
              }`}
            >
              ⭐ {averageNumber != null ? averageNumber : "-"}
            </span>
            <span className="text-sm text-gray-500">
              리뷰 {book.reviewCount}개
            </span>
          </div>

          {book.rating && <RatingHistogram rating={book.rating} />}

          <div className="flex gap-2 flex-wrap text-sm text-blue-600">
            {book.tags.map((tag) => (
              <span key={tag}>#{tag}</span>
            ))}
          </div>

          <p className="text-sm text-gray-700 mt-1">{book.description}</p>

          {isLogin ? (
            <button
              className={`self-start border rounded-full px-3 py-1 text-sm mt-2 ${
                book.isWished
                  ? "bg-rose-100 border-rose-300 text-rose-700"
                  : "hover:bg-gray-100"
              }`}
              type="button"
              onClick={handleToggleWish}
            >
              {book.isWished ? "🔖 보고 싶어요 취소" : "🔖 보고 싶어요"}
            </button>
          ) : (
            <Link
              href="/members/login"
              className="self-start border rounded-full px-3 py-1 text-sm mt-2 hover:bg-gray-100"
            >
              🔖 보고 싶어요
            </Link>
          )}
        </div>
      </div>

      <div>
        <h2 className="font-bold text-lg">리뷰 {reviews.length}개</h2>

        {isLogin ? (
          <>
            <button
              className="border rounded px-3 py-1 text-sm mt-2 hover:bg-gray-100"
              type="button"
              onClick={() => setShowWriteForm((prev) => !prev)}
            >
              {showWriteForm ? "리뷰 작성 취소" : "✏️ 리뷰 작성"}
            </button>

            {showWriteForm && (
              <form
                className="flex flex-col gap-2 p-3 border rounded max-w-md mt-2"
                onSubmit={handleWriteSubmit}
              >
                <label className="flex items-center gap-2">
                  평점
                  <input
                    className="border p-1 rounded w-20"
                    type="number"
                    name="rating"
                    min={0.5}
                    max={5}
                    step={0.5}
                    defaultValue={5}
                    required
                  />
                </label>
                <textarea
                  className="border p-2 rounded"
                  name="content"
                  placeholder="리뷰 내용 (2~30자)"
                  maxLength={30}
                  rows={2}
                />
                <input
                  className="border p-2 rounded"
                  type="text"
                  name="tags"
                  placeholder="태그 (쉼표로 구분)"
                />
                <button className="border p-2 rounded" type="submit">
                  리뷰 작성
                </button>
              </form>
            )}
          </>
        ) : (
          <Link
            href="/members/login"
            className="inline-block border rounded px-3 py-1 text-sm mt-2 hover:bg-gray-100"
          >
            리뷰 쓰고 내 독서 이력에 추가하기
          </Link>
        )}

        {reviews.length === 0 && (
          <div className="mt-2 text-sm text-gray-500">
            아직 리뷰가 없습니다.
          </div>
        )}

        <ul className="flex flex-col mt-2 max-w-md">
          {reviews.map((review) => (
            <li key={review.id} className="border-b py-3">
              {editingReviewId === review.id ? (
                <form
                  className="flex flex-col gap-2"
                  onSubmit={(e) => handleEditSubmit(e, review.id)}
                >
                  <label className="flex items-center gap-2">
                    평점
                    <input
                      className="border p-1 rounded w-20"
                      type="number"
                      name="rating"
                      min={0.5}
                      max={5}
                      step={0.5}
                      defaultValue={review.rating}
                      required
                    />
                  </label>
                  <textarea
                    className="border p-2 rounded"
                    name="content"
                    defaultValue={review.content}
                    maxLength={30}
                    rows={2}
                  />
                  <input
                    className="border p-2 rounded"
                    type="text"
                    name="tags"
                    defaultValue={review.tags.join(", ")}
                  />
                  <div className="flex gap-2">
                    <button className="border p-2 rounded" type="submit">
                      수정 완료
                    </button>
                    <button
                      className="border p-2 rounded"
                      type="button"
                      onClick={() => setEditingReviewId(null)}
                    >
                      취소
                    </button>
                  </div>
                </form>
              ) : (
                <div className="flex items-start gap-3">
                  <Link href={`/members/${review.reviewer.id}`}>
                    <Avatar label={review.reviewer.githubId} />
                  </Link>

                  <div className="flex-1 min-w-0">
                    <div className="flex items-center gap-1">
                      <Link
                        className="font-semibold hover:underline"
                        href={`/members/${review.reviewer.id}`}
                      >
                        {review.reviewer.githubId ?? "탈퇴한 사용자"}
                      </Link>
                      <span className="text-xs text-gray-400">🐙</span>
                    </div>

                    <div className="flex gap-1 flex-wrap text-xs text-blue-600">
                      {review.tags.map((tag) => (
                        <span key={tag}>#{tag}</span>
                      ))}
                    </div>

                    <div className="text-sm mt-1">{review.content}</div>
                    <div className="text-xs text-gray-400 mt-1">
                      {review.createdDate}
                    </div>

                    {loginMember?.id === review.reviewer.id && (
                      <div className="flex gap-2 mt-1">
                        <button
                          className="border p-1 rounded text-xs"
                          type="button"
                          onClick={() => setEditingReviewId(review.id)}
                        >
                          수정
                        </button>
                        <button
                          className="border p-1 rounded text-xs"
                          type="button"
                          onClick={() => handleDelete(review.id)}
                        >
                          삭제
                        </button>
                      </div>
                    )}
                  </div>

                  <span
                    className={`font-bold shrink-0 ${ratingColor(review.rating)}`}
                  >
                    ⭐ {review.rating}
                  </span>
                </div>
              )}
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
}
