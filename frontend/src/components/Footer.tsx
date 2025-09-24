import Image from "next/image"
import { FaInstagram, FaLinkedin, FaTiktok } from "react-icons/fa"

export default function Footer() {
    return (
        <footer className="bg-stone-200 py-20 px-6 md:px-16">
            <div className="max-w-7xl mx-auto grid grid-cols-1 md:grid-cols-3 gap-10">

                {/* Brand */}
                <div>
                    <Image 
                        src="/logo.png"
                        alt="Logo"
                        width={60}
                        height={60}
                        className="cursor-pointer mb-4"
                    />
                    <p className="mb-4 text-sm">
                        Privacy-first insights from your statements.
                    </p>
                    <div className="flex gap-4">
                        <a href="#" className="text-xl" aria-label="Instagram">
                            <FaInstagram />
                        </a>
                        <a href="#" className="text-xl" aria-label="Tiktok">
                            <FaTiktok />
                        </a>
                        <a href="#" className="text-xl" aria-label="Linkedin">
                            <FaLinkedin />
                        </a>
                    </div>
                </div>

                {/* Quick Links */}
                <div>
                    <h4 className="font-bold mb-3">Quick Links</h4>
                    <ul className="space-y-2">
                        <li><a href="/why" className="text-sm">Why Savo?</a></li>
                        <li><a href="/features" className="text-sm">Features</a></li>
                        <li><a href="/pricing" className="text-sm">Pricing</a></li>
                        <li><a href="/contact" className="text-sm">Contact</a></li>
                    </ul>
                </div>

                {/* Legal / Support */}
                <div>
                    <h4 className="font-bold mb-3">Help & Legal</h4>
                    <ul className="space-y-2">
                        <li><a href="/faq" className="text-sm">FAQ</a></li>
                        <li><a href="/support" className="text-sm">Support</a></li>
                        <li><a href="/privacy" className="text-sm">Privacy Policy</a></li>
                        <li><a href="/terms" className="text-sm">Terms of Service</a></li>
                    </ul>
                </div>

            </div>

            <div className="mt-10 border-t border-slate-600 pt-6 text-center text-xs">
                &copy; {new Date().getFullYear()} Savo. All rights reserved.
            </div>
        </footer>
    )
    
}