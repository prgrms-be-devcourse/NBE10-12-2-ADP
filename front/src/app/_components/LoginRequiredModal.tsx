"use client";

import RoughButton from "@/app/_components/RoughButton";
import RoughFrame from "@/app/_components/RoughFrame";

type LoginRequiredModalProps = {
  onCancel: () => void;
  onLogin: () => void;
};

export default function LoginRequiredModal({
  onCancel,
  onLogin,
}: LoginRequiredModalProps) {
  return (
    <div
      className="rough-modal-backdrop"
      role="dialog"
      aria-modal="true"
      aria-labelledby="login-required-title"
    >
      <div className="rough-modal-card">
        <RoughFrame className="rough-overlay" variant="card" />
        <h2 id="login-required-title" className="text-xl font-bold">
          로그인이 필요한 기능입니다
        </h2>
        <p className="mt-2 text-sm text-gray-600">
          이 기능을 사용하려면 로그인이 필요합니다. 로그인 페이지로
          이동하시겠습니까?
        </p>
        <div className="mt-5 flex justify-end gap-2">
          <RoughButton
            roughSize="sm"
            tone="cancel"
            type="button"
            onClick={onCancel}
          >
            취소
          </RoughButton>
          <RoughButton
            roughSize="sm"
            tone="history"
            type="button"
            onClick={onLogin}
          >
            로그인 하러 가기
          </RoughButton>
        </div>
      </div>
    </div>
  );
}
