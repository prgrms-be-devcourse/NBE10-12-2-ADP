const SIZE_CLASSES = {
  sm: "w-10 h-10 text-base",
  lg: "w-24 h-24 text-3xl",
};

export default function Avatar({
  label,
  size = "sm",
}: {
  label: string | null;
  size?: "sm" | "lg";
}) {
  return (
    <div
      className={`${SIZE_CLASSES[size]} shrink-0 rounded-full bg-gray-200 flex items-center justify-center font-bold text-gray-600`}
    >
      {label ? label.charAt(0).toUpperCase() : "?"}
    </div>
  );
}
