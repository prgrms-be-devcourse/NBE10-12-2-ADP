"use client";

import Link from "next/link";
import { useParams } from "next/navigation";

import { useEffect, useState } from "react";

import { API_BASE_URL, apiFetch } from "@/lib/backend/client";

import type { components } from "@/lib/backend/apiV1/schema";
import { ratingColor } from "@/lib/ratingColor";

import Avatar from "@/app/_components/Avatar";
import RatingHistogram from "@/app/_components/RatingHistogram";
import RatingValue from "@/app/_components/RatingValue";
import RoughDivider from "@/app/_components/RoughDivider";
import RoughFrame from "@/app/_components/RoughFrame";

type MemberDto = components["schemas"]["MemberDto"];
type ReviewsByMemberDto = components["schemas"]["ReviewsByMemberDto"];

export default function Page() {
  const { id } = useParams<{ id: string }>();

  const [member, setMember] = useState<MemberDto | null>(null);
  const [reviewData, setReviewData] = useState<ReviewsByMemberDto | null>(null);
  const [bookTitles, setBookTitles] = useState<Record<number, string>>({});
  const [loadError, setLoadError] = useState<string | null>(null);

  useEffect(() => {
    apiFetch(`/api/v1/members/${id}`)
      .then((data) => {
        setLoadError(null);
        setMember(data);
      })
      .catch((error) => {
        setLoadError(`${error.resultCode} : ${error.message}`);
      });

    apiFetch(`/api/v1/reviews/member/${id}`)
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
  }, [id]);

  if (loadError != null) {
    return (
      <div>오류가 발생했습니다: {loadError} (백엔드 확인이 필요합니다)</div>
    );
  }

  if (member == null || reviewData == null) return <div>로딩중...</div>;

  const average = reviewData.rating?.["average"];
  const averageNumber = typeof average === "number" ? average : null;

  return (
    <div className="flex gap-8">
      <div className="flex flex-col items-center gap-1 w-48 shrink-0">
        <Avatar label={member.githubId} size="lg" />
        <div className="mt-2 text-sm font-semibold">{member.githubId}</div>
        {member.githubLink && (
          <a
            className="text-sm text-blue-600 underline"
            href={member.githubLink}
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
            <RatingValue rating={averageNumber} />
          </div>
        )}
        <div className="text-xs text-gray-500">
          {member.githubId}님이 준 평균 별점
        </div>
        <RatingHistogram
          rating={reviewData.rating}
          className="mt-3 w-full"
        />
      </div>

      <div className="flex-1 flex flex-col gap-4">
        {member.githubId && (
          <div>
            <h2 className="font-bold">위젯 미리보기</h2>
            <div className="rough-panel-border mt-1 p-2">
              <RoughFrame className="rough-overlay" variant="card" />
              {/* eslint-disable-next-line @next/next/no-img-element */}
              <img
                src={`${API_BASE_URL}/api/v1/widgets/${member.githubId}`}
                alt="위젯 미리보기"
              />
            </div>
          </div>
        )}

        <div>
          <div className="flex gap-2 pb-1">
            <h2 className="px-3 py-2 text-sm font-bold border-b-2 border-black">
              작성한 리뷰 {reviewData.results.length}
            </h2>
          </div>

          {reviewData.results.length === 0 && (
            <div className="text-sm text-gray-500 mt-2">
              작성한 리뷰가 없습니다.
            </div>
          )}

          <ul className="flex w-full flex-col">
            {reviewData.results.map((review) => (
              <li
                key={review.id}
                className="relative flex items-start gap-3 py-3"
              >
                <RoughDivider fullWidth />
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
                </div>

                <span
                  className={`font-bold shrink-0 ${ratingColor(review.rating)}`}
                >
                  <RatingValue rating={review.rating} />
                </span>
              </li>
            ))}
          </ul>
        </div>
      </div>
    </div>
  );
}
