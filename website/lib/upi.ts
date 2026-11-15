import QRCode from "qrcode";
import { site } from "@/site.config";

/** A UPI payment link. Opening it on a phone shows the user's UPI apps (GPay, PhonePe, Paytm…). */
export function upiLink(amount?: number): string {
  const params: [string, string][] = [
    ["pa", site.support.upiId],
    ["pn", site.support.upiName],
    ["cu", "INR"],
    ["tn", "A tea for ScrollStop"],
  ];
  if (amount) params.push(["am", String(amount)]);
  // encodeURIComponent writes spaces as %20 (some UPI apps show "+" literally)
  return "upi://pay?" + params.map(([k, v]) => `${k}=${encodeURIComponent(v)}`).join("&");
}

/** The same link as a QR code (SVG markup), for scanning from another phone. Made at build time. */
export async function upiQrSvg(): Promise<string> {
  return QRCode.toString(upiLink(), {
    type: "svg",
    margin: 1,
    errorCorrectionLevel: "M",
    color: { dark: "#0E0F13", light: "#FFFFFF" },
  });
}
