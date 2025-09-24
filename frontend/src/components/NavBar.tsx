import Link from "next/link";
import Image from "next/image";

export default function NavBar() {
    return (
        <nav className="w-full shadow-md">
            <div className="max-w-7xl mx-auto px-6 py-4 flex items-center justify-between">
                {/* Logo */}
                <div className="flex items-center">
                    <Link href="/">
                        <Image
                        src="/logo.png"
                        alt="Savo Logo"
                        width={60}
                        height={60}
                        className="cursor-pointer"
                        />
                    </Link>
                </div>

                <div className="hidden md:flex gap-8 text-sm font-medium text-slate-950">
                    <Link className="hover:underline" href="/why-savo">Why Savo?</Link>
                    <Link className="hover:underline" href="/contact">Contact</Link>
                    <Link className="hover:underline" href="/pricing">Pricing</Link>
                </div>

                <div className="flex gap-8 text-sm items-center">
                    <Link href="/login">
                        <button className="text-slate-950 font-medium hover:underline cursor-pointer">Login</button>
                    </Link>
                    <Link href="/signup">
                        <button className="bg-slate-950 text-white px-4 py-2 rounded-lg font-semibold cursor-pointer">
                            Sign Up
                        </button>
                    </Link>
                </div>
            </div>
        </nav>
  );
}