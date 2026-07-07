import Link from "next/link";

import type { components } from "@/lib/backend/apiV1/schema";
import { ratingColor } from "@/lib/ratingColor";

import RatingValue from "@/app/_components/RatingValue";
import RoughFrame from "@/app/_components/RoughFrame";

type BookDto = components["schemas"]["BookDto"];

export default function BookGrid({ books }: { books: BookDto[] }) {
  if (books.length === 0) {
    return <div>도서가 없습니다.</div>;
  }

  return (
    <ul className="grid grid-cols-2 gap-4 p-2 sm:grid-cols-4">
      {books.map((book) => (
        <li key={book.id} className="group rough-book-card rounded-xl">
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
}
