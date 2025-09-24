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
                    <p className="mb-4">
                        Privacy-first insights from your statements. No bank linking needed.
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
                <h4 className="text-lg font-bold mb-3">Quick Links</h4>
                <ul className="space-y-2">
                    <li><a href="/why" >Why Savo?</a></li>
                    <li><a href="/features" >Features</a></li>
                    <li><a href="/pricing" >Pricing</a></li>
                    <li><a href="/contact" >Contact</a></li>
                </ul>
                </div>

                {/* Legal / Support */}
                <div>
                <h4 className="text-lg font-bold mb-3">Help & Legal</h4>
                <ul className="space-y-2">
                    <li><a href="/faq">FAQ</a></li>
                    <li><a href="/support" >Support</a></li>
                    <li><a href="/privacy">Privacy Policy</a></li>
                    <li><a href="/terms">Terms of Service</a></li>
                </ul>
                </div>

            </div>

            <div className="mt-10 border-t border-slate-600 pt-6 text-center text-sm">
                &copy; {new Date().getFullYear()} Savo. All rights reserved.
            </div>
        </footer>
    )
    
}