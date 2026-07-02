export function ratingColor(rating: number) {
  if (rating >= 4) return "text-green-600";
  if (rating >= 2.5) return "text-yellow-500";
  return "text-red-500";
}
