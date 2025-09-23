import { Info } from "lucide-react";

export default function Features() {
    const features = [
        {
            title: "Secure & Private Workflow",
            description: "Sign in to your private dashboard to access your financial insights. No bank linking required — simply upload your statements and review your data securely.",
            color: "bg-green-50",
            iconLink: "/features/security",
        },
        {
            title: "Smart Categorisation",
            description: "Our AI-powered engine intelligently classifies every transaction into accurate categories — groceries, dining, bills, and more with zero manual input needed.",
            color: "bg-blue-50",
            iconLink: "/features/categorisation",
        },
        {
            title: "Visual Budgeting",
            description: "Instantly grasp your spending patterns through visuals. Savo highlights where you’re overspending the most — like that sneaky dining budget — so you can quickly adjust.",
            color: "bg-purple-50",
            iconLink: "/features/visuals",
        },
    ];

    return (
        <section className="bg-white">
            <div className="max-w-7xl mx-auto px-6">
                <h2 className="text-5xl font-bold text-center">Why Choose Savo?</h2>

                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-8 py-15">
                    {features.map((feature, index) => (
                        <div
                            key={index}
                            className={`${feature.color} rounded-xl shadow-sm p-6 relative min-h-[220px] flex flex-col justify-between`}
                        >
                            {/* Top-right Icon */}
                            <a href={feature.iconLink} className="absolute top-4.5 right-5 text-gray-400 hover:text-gray-600">
                                <Info size={40} />
                            </a>

                            {/* Title + Description */}
                            <div>
                                <h3 className="text-xl font-semibold mb-6">{feature.title}</h3>
                                <p className="text-gray-600">{feature.description}</p>
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </section>
    );
}