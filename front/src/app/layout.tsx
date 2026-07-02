import type { Metadata } from "next";

import { AuthProvider } from "@/lib/auth/AuthProvider";

import Header from "@/app/_components/Header";

import "./globals.css";

export const metadata: Metadata = {
  title: "READTHEM.md",
  description: "스프링부트, Next.js 연동",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="ko">
      <head>
        <link rel="preconnect" href="https://fonts.googleapis.com" />
        <link
          rel="preconnect"
          href="https://fonts.gstatic.com"
          crossOrigin="anonymous"
        />
        {/* eslint-disable-next-line @next/next/no-page-custom-font -- this is the root layout, applies to every page */}
        <link
          href="https://fonts.googleapis.com/css2?family=Gaegu:wght@400;700&display=swap"
          rel="stylesheet"
        />
      </head>
      <body className="flex flex-col min-h-screen antialiased">
        <AuthProvider>
          <Header />
          <main className="flex-grow flex flex-col p-4 mx-auto w-full max-w-4xl">
            {children}
          </main>
          <footer className="text-center p-2">푸터</footer>
        </AuthProvider>
      </body>
    </html>
  );
}
