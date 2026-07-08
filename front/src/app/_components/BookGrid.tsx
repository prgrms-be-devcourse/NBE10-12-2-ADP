import Link from "next/link";

import type { components } from "@/lib/backend/apiV1/schema";
import { ratingColor } from "@/lib/ratingColor";

import RatingValue from "@/app/_components/RatingValue";
import RoughFrame from "@/app/_components/RoughFrame";

type BookDto = components["schemas"]["BookDto"];

type BookGridProps = {
  books: BookDto[];
  layout?: "grid" | "horizontal";
};

export default function BookGrid({ books, layout = "grid" }: BookGridProps) {
  if (books.length === 0) {
    return <div>도서가 없습니다.</div>;
  }

  const listClassName =
    layout === "horizontal"
      ? "book-scroll-list flex gap-5 overflow-x-auto py-2 pb-4"
      : "grid grid-cols-2 gap-4 p-2 sm:grid-cols-4";

  const itemClassName =
    layout === "horizontal"
      ? "group flex w-44 shrink-0 flex-col sm:w-[185px]"
      : "group flex flex-col";

  const coverClassName =
    layout === "horizontal"
      ? "relative flex aspect-[2/3] w-full items-center justify-center overflow-hidden rounded-xl bg-transparent"
      : "relative flex aspect-[2/3] w-full items-center justify-center overflow-hidden rounded-xl bg-transparent";

  const list = (
    <ul className={listClassName}>
      {books.map((book, index) => (
        <li key={book.id} className={itemClassName}>
          <Link
            href={`/books/detail?id=${book.id}`}
            className="flex h-full flex-col gap-2"
          >
            <div className="rough-book-card rounded-xl">
              <RoughFrame
                className="rough-overlay rough-card-line rough-book-cover-line"
                variant="card"
              />
              <div className={coverClassName}>
                {book.imgUrl ? (
                  // eslint-disable-next-line @next/next/no-img-element
                  <img
                    src={book.imgUrl}
                    alt={book.title}
                    className="relative z-0 h-full w-full object-cover"
                  />
                ) : (
                  <span className="text-sm text-gray-400">표지 없음</span>
                )}
                {layout === "horizontal" && (
                  <>
                    <span className="book-rank-overlay" />
                    <span className="book-rank-number">
                      {index + 1}
                    </span>
                  </>
                )}
              </div>
            </div>
            <span className="book-title">{book.title}</span>
            <span
              className={`book-rating font-bold ${ratingColor(book.averageRating)}`}
            >
              <RatingValue rating={book.averageRating} />
            </span>
          </Link>
        </li>
      ))}
    </ul>
  );

  if (layout === "horizontal") {
    return <div className="book-scroll-fade">{list}</div>;
  }

  return list;
}
