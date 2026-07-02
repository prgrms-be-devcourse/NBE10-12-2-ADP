import Link from "next/link";

import type { components } from "@/lib/backend/apiV1/schema";
import { ratingColor } from "@/lib/ratingColor";

type BookDto = components["schemas"]["BookDto"];

export default function BookGrid({ books }: { books: BookDto[] }) {
  if (books.length === 0) return <div>도서가 없습니다.</div>;

  return (
    <ul className="grid grid-cols-2 sm:grid-cols-4 gap-4 p-2">
      {books.map((book) => (
        <li key={book.id}>
          <Link href={`/books/${book.id}`} className="flex flex-col gap-1">
            <div className="w-full h-48 border rounded bg-gray-50 flex items-center justify-center overflow-hidden">
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
            <span>{book.title}</span>
            <span className={`font-bold ${ratingColor(book.averageRating)}`}>
              ⭐ {book.averageRating}
            </span>
          </Link>
        </li>
      ))}
    </ul>
  );
}
