import Image from "next/image";
import Hero from "@/components/Hero";
import Features from "@/components/Features";
import HowItWorks from "@/components/HowItWorks";
import CTA from "@/components/CTA";

export default function Home() {
  return (
    <main className="flex flex-col items-center">
      <Hero />
      <Features />
      <HowItWorks />
      <CTA />
    </main>
  );
}
