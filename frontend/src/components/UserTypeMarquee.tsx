'use client';

export default function UserTypeMarquee() {
  const userTypes = [
    "👫 Couples",
    "👨‍👩‍👧‍👦 Families",
    "🌏 Cross-border Banking Users",
    "🧑‍💻 Engineers",
    "🎨 Designers",
    "🚀 Startup Founders",
    "💰 High Earners",
    "📊 Financial Planners",
    "💳 Credit Card Users",
    "🏦 Business Owners",
    "🧠 Smart Spenders",
    "🧍 People like you!",
  ];

  const content = userTypes.map((type, index) => (
    <span key={index} className="inline-block mx-8 font-medium whitespace-nowrap text-base">
      {type}
    </span>
  ));

  return (
    <div className="relative w-full overflow-hidden bg-gray-100 bg-slate-950 text-white py-3">
      <div className="flex gap-12 animate-marqueeLoop ">
        {content}
        {content}
      </div>
    </div>
  );
}