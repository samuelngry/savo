'use client';
import { useState } from "react";
import Link from "next/link";

export default function FAQ() {
    const faqs = [
    {
      question: "Do I need to link my bank account?",
      answer:
        "Nope! Savo is privacy-first. You only need to upload your PDF statement — no bank logins or linking required.",
    },
    {
      question: "Which banks are supported?",
      answer:
        "Savo supports all major Singapore banks as long as you have a PDF statement. This includes DBS, UOB, OCBC, and more.",
    },
    {
      question: "Will my data be shared or stored?",
      answer:
        "Your data stays local in your browser unless you choose to sign in and save it. We never share or sell your data.",
    },
    {
      question: "Can I use Savo on mobile?",
      answer:
        "Yes! Savo is fully responsive and works seamlessly on both desktop and mobile devices.",
    },
    {
      question: "How fast do I get my insights?",
      answer:
        "Within seconds. Once you upload your statement, our AI categorizes your data and shows insights instantly.",
    },
    {
      question: "Is Savo free to use?",
      answer:
        "We offer a free tier with core features. Premium features (like advanced trends, exports, or multi-account views) may be introduced later.",
    },
  ];

  const [openIndex, setOpenIndex] = useState<number | null>(null);

  const toggle = (index: number) => {
    setOpenIndex(openIndex === index ? null : index);
  };

  return (
    <section>
      <div className="max-w-7xl mx-auto px-6">
        <h2 className="text-5xl font-bold text-center">Frequently Asked Questions</h2>

        <div className="flex flex-col md:flex-row items-stretch gap-6 py-15">
            {/* FAQ List */}
            <div className="flex-[2_2_0%] space-y-4">
                {faqs.map((faq, index) => (
                    <div
                    key={index}
                    className="border border-gray-200 rounded-xl shadow-sm p-4 transition-all"
                    >
                    <div
                        className="flex justify-between items-center cursor-pointer"
                        onClick={() => toggle(index)}
                    >
                        <h3 className="font-semibold">{faq.question}</h3>
                        <span className="text-2xl font-bold text-slate-500">
                        {openIndex === index ? "–" : "+"}
                        </span>
                    </div>

                    {openIndex === index && (
                        <p className="text-gray-600 mt-4 transition-all">{faq.answer}</p>
                    )}
                    </div>
                ))}
            </div>

            {/* Contact Box */}
            <div className="flex-[1_1_0%]">
                <div className="text-center p-6 bg-stone-200 border rounded-xl shadow-sm h-full flex flex-col justify-between border-stone-200">
                    <div className="flex-grow flex flex-col justify-center">
                        <h4 className="text-lg font-semibold mb-2">
                            Can't find the answer to your question?
                        </h4>
                        <p className="mb-6">
                            We are here to help - feel free to reach out and we will get back to you ASAP.
                        </p>
                        <Link href="/contact">
                            <button className="bg-slate-950 text-white px-5 py-2 rounded-lg font-bold cursor-pointer">
                                Contact Us
                            </button>
                        </Link>
                    </div>
                </div>
            </div>
            
        </div>
        
      </div>
    </section>
  );
}