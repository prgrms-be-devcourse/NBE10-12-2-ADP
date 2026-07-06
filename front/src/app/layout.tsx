import type { Metadata } from "next";

import { AuthProvider } from "@/lib/auth/AuthProvider";
import { ThemeProvider } from "@/lib/theme/ThemeProvider";

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
    <html lang="ko" suppressHydrationWarning>
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
      <body className="flex min-h-screen flex-col antialiased">
        <ThemeProvider>
          <AuthProvider>
            <Header />
            <main className="mx-auto flex w-full max-w-4xl flex-grow flex-col p-4">
              <div className="relative flex flex-1 flex-col">{children}</div>
            </main>
            <footer className="p-2 text-center">푸터</footer>
          </AuthProvider>
        </ThemeProvider>
      </body>
    </html>
  );
}
