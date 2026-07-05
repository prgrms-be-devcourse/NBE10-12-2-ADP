type RoughDividerProps = {
  className?: string;
  color?: string;
  fullWidth?: boolean;
  list?: boolean;
  strokeWidth?: number;
};

export default function RoughDivider({
  className = "",
  color = "#6b7280",
  fullWidth = false,
  list = true,
  strokeWidth = 1,
}: RoughDividerProps) {
  const classes = [
    list ? "rough-list-divider" : "rough-divider",
    fullWidth ? "rough-list-divider-full" : "",
    className,
  ]
    .filter(Boolean)
    .join(" ");

  return (
    <svg
      aria-hidden="true"
      className={classes}
      width="900"
      height="10"
      viewBox="-18 0 936 10"
      preserveAspectRatio="none"
    >
      <g>
        <path
          d="M-18 4.3 C210 2.8, 360 6.1, 450 5 C540 3.9, 690 6.2, 918 4.7"
          stroke={color}
          strokeWidth={strokeWidth}
          fill="none"
        />
        <path
          d="M-18 5.8 C210 7.2, 360 3.9, 450 5 C540 6.1, 690 3.8, 918 5.3"
          stroke={color}
          strokeWidth={strokeWidth * 0.6}
          fill="none"
        />
      </g>
    </svg>
  );
}
