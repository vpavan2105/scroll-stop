import { createHash } from "node:crypto";
import { existsSync, readFileSync } from "node:fs";
import path from "node:path";

export type ApkInfo = { href: string; sizeLabel: string; sha256: string };

/**
 * Reads an APK from /public when the site is built, so the size and the
 * SHA-256 checksum shown always match the real file.
 * Returns null if the file isn't there.
 */
export function getApkInfo(file: string): ApkInfo | null {
  const full = path.join(process.cwd(), "public", file);
  if (!existsSync(full)) return null;
  const bytes = readFileSync(full);
  return {
    href: `/${file}`,
    sizeLabel: `${(bytes.length / (1024 * 1024)).toFixed(1)} MB`,
    sha256: createHash("sha256").update(bytes).digest("hex"),
  };
}
