import Image from "next/image";

export default function Hero() {
    return (
        <section className="w-full min-h-screen flex items-center justify-center bg-white px-6 md:px-16">
            <div className="max-w-7xl w-full flex flex-col md:flex-row items-center gap-10">

                {/* Text */}
                <div className="flex-1">
                    <h1 className="text-7xl md:text-6xl font-bold mb-6 mt-2">
                        Upload Statements, Instant Insights
                    </h1>
                    <p className="text-lg mb-6 text-gray-600">
                        Upload your bank statements in seconds. Get instant insights into your spending, and no account linking needed.
                    </p>
                    <button className="bg-slate-950 text-white font-bold px-6 py-3 rounded-lg cursor-pointer">Get Started</button>
                </div>

                {/* Image */}
                <div className="flex-1">
                    <Image 
                        src="/landing.png"
                        alt="Savo App"
                        width={500}
                        height={400}
                        priority
                    />
                </div>
            </div>

        </section>
    )
}