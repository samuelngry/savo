import { Key } from "lucide-react";
import Image from "next/image";

export default function HowItWorks() {
    const steps = [
        {
            title: "No Bank Linking Required",
            description: "Most finance apps force you to connect your bank - we don't. Just upload any PDF statement, and you're good to go.",
            bullets: [
                "> Upload statements without logging in to your bank",
                "> Supports PDFs from all Singapore banks",
                "> Your data stays local and private",
            ],
            image: "/how1.jpg",
            reverse: false,
        },
        {
            title: "Get Insights in Seconds",
            description: "Our AI engine instantly reads your transactions, categorises them smartly, and shows you where your money goes.",
            bullets: [
                "> Auto-categorisation, no setup needed",
                "> See budgets, trends, and top categories",
                "> Built for busy people who hate spreadsheets",
            ],
            image: "/how2.jpg",
            reverse: true,
        },
    ];

    return (
        <section className="py-10">
            <div className="max-w-7xl mx-auto px-6">

                {steps.map((step, index) => (
                    <div
                        key={index}
                        className={`flex flex-col-reverse md:flex-row ${step.reverse ? "md:flex-row-reverse" : "" } items-center gap-20 mb-24`}
                    >
                        <div className="w-full md:w-1/2">
                            <Image
                                src={step.image}
                                alt={step.title}
                                className="w-full h-auto rounded-xl shadow-md"
                                width={200}
                                height={100}
                            />
                        </div>

                        <div className="w-full md:w-1/2">
                            <h3 className="text-4xl font-semibold mb-4">{step.title}</h3>
                            <p className="text-lg leading-relaxed text-gray-600">
                                {step.description}
                            </p>
                            <ul className="mt-8 space-y-2 text-xl font-medium">
                                {step.bullets.map((point, index) => (
                                    <li key={index}>{point}</li>
                                ))}
                            </ul>
                        </div>
                    </div>
                ))}
            </div>
        </section>
    )
}