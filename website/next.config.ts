import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  // Phones must receive the APK as an Android app download, not as a text page.
  async headers() {
    return [
      {
        source: "/:file(.+\\.apk)",
        headers: [
          { key: "Content-Type", value: "application/vnd.android.package-archive" },
          { key: "Content-Disposition", value: "attachment" },
          { key: "Cache-Control", value: "public, max-age=0, must-revalidate" },
        ],
      },
    ];
  },
};

export default nextConfig;
