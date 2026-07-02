"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";

import { useEffect, useState } from "react";

import { API_BASE_URL, apiFetch } from "@/lib/backend/client";

import { useAuth } from "@/lib/auth/AuthProvider";
import type { components } from "@/lib/backend/apiV1/schema";
import { ratingColor } from "@/lib/ratingColor";

import Avatar from "@/app/_components/Avatar";

type ReviewsByMemberDto = components["schemas"]["ReviewsByMemberDto"];
type BookDto = components["schemas"]["BookDto"];

export default function Page() {
  const router = useRouter();
  const { loginMember, isLogin, isLoginMemberPending, refresh } = useAuth();

  const [reviewData, setReviewData] = useState<ReviewsByMemberDto | null>(null);
  const [wishes, setWishes] = useState<BookDto[] | null>(null);
  const [bookTitles, setBookTitles] = useState<Record<number, string>>({});
  const [tab, setTab] = useState<"reviews" | "wishes">("reviews");
  const [loadError, setLoadError] = useState<string | null>(null);

  const loadReviews = () => {
    apiFetch(`/api/v1/reviews/member/mine`)
      .then((data: ReviewsByMemberDto) => {
        setLoadError(null);
        setReviewData(data);

        const bookIds = [...new Set(data.results.map((r) => r.bookId))];
        Promise.all(
          bookIds.map((bookId) =>
            apiFetch(`/api/v1/books/${bookId}`).then(
              (book) => [bookId, book.title] as const,
            ),
          ),
        )
          .then((entries) => setBookTitles(Object.fromEntries(entries)))
          .catch(() => {});
      })
      .catch((error) => {
        setLoadError(`${error.resultCode} : ${error.message}`);
      });
  };

  const loadWishes = () => {
    apiFetch(`/api/v1/wishes/mine`)
      .then((data) => {
        setLoadError(null);
        setWishes(data);
      })
      .catch((error) => {
        setLoadError(`${error.resultCode} : ${error.message}`);
      });
  };

  useEffect(() => {
    if (isLoginMemberPending) return;

    if (!isLogin) {
      router.replace(`/members/login`);
      return;
    }

    loadReviews();
    loadWishes();
  }, [isLoginMemberPending, isLogin]);

  const handleDeleteReview = (reviewId: number) => {
    if (!confirm("리뷰를 삭제하시겠습니까?")) return;

    apiFetch(`/api/v1/reviews/${reviewId}`, { method: "DELETE" })
      .then((data) => {
        alert(data.message);
        loadReviews();
      })
      .catch((error) => {
        alert(`${error.resultCode} : ${error.message}`);
      });
  };

  const handleRemoveWish = (bookId: number) => {
    apiFetch(`/api/v1/wishes/book/${bookId}`, { method: "DELETE" })
      .then((data) => {
        alert(data.message);
        loadWishes();
      })
      .catch((error) => {
        alert(`${error.resultCode} : ${error.message}`);
      });
  };

  const handleWithdraw = () => {
    if (!confirm("정말 탈퇴하시겠습니까? 되돌릴 수 없습니다.")) return;

    apiFetch(`/api/v1/members`, { method: "DELETE" })
      .then((data) => {
        alert(data.message);
        return refresh();
      })
      .then(() => {
        router.replace(`/`);
      })
      .catch((error) => {
        alert(`${error.resultCode} : ${error.message}`);
      });
  };

  const handleLogout = () => {
    apiFetch(`/api/v1/members/logout`, { method: "DELETE" }).then(() => {
      refresh().then(() => router.replace(`/`));
    });
  };

  if (loadError != null) {
    return (
      <div>오류가 발생했습니다: {loadError} (백엔드 확인이 필요합니다)</div>
    );
  }

  if (
    isLoginMemberPending ||
    !isLogin ||
    reviewData == null ||
    wishes == null
  ) {
    return <div>로딩중...</div>;
  }

  const average = reviewData.rating?.["average"];
  const averageNumber = typeof average === "number" ? average : null;

  return (
    <div className="flex gap-8">
      <div className="flex flex-col items-center gap-1 w-48 shrink-0">
        <Avatar
          label={loginMember?.githubId ?? loginMember?.username ?? null}
          size="lg"
        />
        <div className="mt-2 text-sm">아이디 : {loginMember?.username}</div>
        <div className="text-sm">닉네임 : {loginMember?.githubId}</div>
        {loginMember?.githubLink && (
          <a
            className="text-sm text-blue-600 underline"
            href={loginMember.githubLink}
            target="_blank"
            rel="noreferrer"
          >
            github 주소
          </a>
        )}

        {averageNumber != null && (
          <div
            className={`text-lg font-bold mt-2 ${ratingColor(averageNumber)}`}
          >
            ⭐ {averageNumber}
          </div>
        )}
        <div className="text-xs text-gray-500">내가 준 평균 별점</div>

        <button
          className="border rounded px-3 py-1 text-sm mt-4 hover:bg-gray-100"
          type="button"
          onClick={handleLogout}
        >
          로그아웃
        </button>
        <button
          className="text-xs text-gray-400 underline mt-2"
          type="button"
          onClick={handleWithdraw}
        >
          회원 탈퇴
        </button>
      </div>

      <div className="flex-1 flex flex-col gap-4">
        <div>
          <h2 className="font-bold">위젯 미리보기</h2>
          <div className="border rounded p-2 mt-1 min-h-24 flex items-center justify-center bg-gray-50">
            {loginMember?.githubId ? (
              // eslint-disable-next-line @next/next/no-img-element
              <img
                src={`${API_BASE_URL}/api/v1/widgets/${loginMember.githubId}`}
                alt="내 서재 위젯"
              />
            ) : (
              <span className="text-gray-400 text-sm">
                위젯 정보가 없습니다
              </span>
            )}
          </div>
          {loginMember?.widgetLink && (
            <input
              className="border p-1 rounded text-xs w-full mt-1"
              readOnly
              value={loginMember.widgetLink}
              onFocus={(e) => e.currentTarget.select()}
            />
          )}
        </div>

        <div className="flex gap-2 border-b">
          <button
            type="button"
            className={`px-3 py-2 text-sm ${
              tab === "reviews"
                ? "border-b-2 border-black font-bold"
                : "text-gray-400"
            }`}
            onClick={() => setTab("reviews")}
          >
            작성한 리뷰 {reviewData.results.length}
          </button>
          <button
            type="button"
            className={`px-3 py-2 text-sm ${
              tab === "wishes"
                ? "border-b-2 border-black font-bold"
                : "text-gray-400"
            }`}
            onClick={() => setTab("wishes")}
          >
            보고 싶어요 {wishes.length}
          </button>
        </div>

        {tab === "reviews" && (
          <>
            {reviewData.results.length === 0 && (
              <div className="text-sm text-gray-500">
                작성한 리뷰가 없습니다.
              </div>
            )}

            <ul className="flex flex-col">
              {reviewData.results.map((review) => (
                <li
                  key={review.id}
                  className="border-b py-3 flex items-start gap-3"
                >
                  <div className="flex-1 min-w-0">
                    <Link
                      className="font-semibold hover:underline"
                      href={`/books/${review.bookId}`}
                    >
                      {bookTitles[review.bookId] ?? `책 #${review.bookId}`}
                    </Link>

                    <div className="flex gap-1 flex-wrap text-xs text-blue-600">
                      {review.tags.map((tag) => (
                        <span key={tag}>#{tag}</span>
                      ))}
                    </div>

                    <div className="text-sm mt-1">{review.content}</div>
                    <div className="text-xs text-gray-400 mt-1">
                      {review.createdDate}
                    </div>

                    <button
                      className="border p-1 rounded text-xs mt-1"
                      type="button"
                      onClick={() => handleDeleteReview(review.id)}
                    >
                      삭제
                    </button>
                  </div>

                  <span
                    className={`font-bold shrink-0 ${ratingColor(review.rating)}`}
                  >
                    ⭐ {review.rating}
                  </span>
                </li>
              ))}
            </ul>
          </>
        )}

        {tab === "wishes" && (
          <>
            {wishes.length === 0 && (
              <div className="text-sm text-gray-500">
                보고 싶어요 한 도서가 없습니다.
              </div>
            )}

            <ul className="flex flex-col">
              {wishes.map((book) => (
                <li
                  key={book.id}
                  className="border-b py-3 flex items-center justify-between gap-3"
                >
                  <Link href={`/books/${book.id}`} className="font-semibold">
                    {book.title}
                  </Link>
                  <div className="flex items-center gap-3">
                    <span
                      className={`font-bold ${ratingColor(book.averageRating)}`}
                    >
                      ⭐ {book.averageRating}
                    </span>
                    <button
                      className="border p-1 rounded text-xs"
                      type="button"
                      onClick={() => handleRemoveWish(book.id)}
                    >
                      보고 싶어요 취소
                    </button>
                  </div>
                </li>
              ))}
            </ul>
          </>
        )}
      </div>
    </div>
  );
}
