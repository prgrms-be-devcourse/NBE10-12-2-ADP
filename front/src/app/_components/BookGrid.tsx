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
      ? "book-scroll-list flex gap-4 overflow-x-auto py-2 pb-4"
      : "grid grid-cols-2 gap-4 p-2 sm:grid-cols-4";

  const itemClassName =
    layout === "horizontal"
      ? "group rough-book-card w-40 shrink-0 rounded-xl sm:w-44"
      : "group rough-book-card rounded-xl";

  const list = (
    <ul className={listClassName}>
      {books.map((book) => (
        <li key={book.id} className={itemClassName}>
          <RoughFrame
            className="rough-overlay rough-card-line"
            variant="card"
          />
          <Link
            href={`/books/detail?id=${book.id}`}
            className="relative z-10 flex flex-col gap-1 p-3"
          >
            <div className="flex h-48 w-full items-center justify-center overflow-hidden rounded-lg bg-transparent">
              {book.imgUrl ? (
                // eslint-disable-next-line @next/next/no-img-element
                <img
                  src={book.imgUrl}
                  alt={book.title}
                  className="h-full w-full object-cover"
                />
              ) : (
                <span className="text-sm text-gray-400">표지 없음</span>
              )}
            </div>
            <span>{book.title}</span>
            <span className={`font-bold ${ratingColor(book.averageRating)}`}>
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
