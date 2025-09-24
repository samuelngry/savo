import Link from "next/link";

export default function CTA() {
    return (
        <section
            className="relative w-full h-[400px] bg-center bg-cover bg-no-repeat"
            style={{ backgroundImage: "url('/cta.jpg')" }} 
            >
            <div className="absolute inset-0 bg-black/100 z-0" />

            <div className="relative z-10 flex flex-col items-center justify-center text-center h-full px-6">
                <h2 className="text-5xl md:text-5xl font-bold text-white mb-4">
                    Ready to Take Control of Your Finances?
                </h2>
                <p className="text-lg md:text-xl text-white mb-8 max-w-2xl">
                    Upload your bank statements and get instant insights without linking any accounts.
                </p>
                <Link href="/register">
                    <button className="bg-white text-slate-950 cursor-pointer font-bold px-6 py-3 rounded-lg hover:bg-gray-100 transition">
                        Get Started
                    </button>
                </Link>
            </div>
        </section>
    );
}