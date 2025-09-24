import { FcGoogle } from "react-icons/fc";
import Image from "next/image";
import Link from "next/link";

export default function LoginPage() {

    return (
        <div className="flex items-center justify-center min-h-screen md:bg-slate-200">
            <div className="bg-white md:shadow-lg rounded-xl p-10 w-full max-w-md text-center">
                {/* Logo */}
                <div className="mb-8">
                    <Image src="/logo.png" alt="Savo Logo" width={60} height={60} className="mx-auto" />
                    <h1 className="text-2xl font-bold mt-4">Welcome Back!</h1>
                    <p className="text-sm text-gray-600">Sign in to continue</p>
                </div>

                {/* Manual Sign In */}
                <form className="text-left">
                    <div className="mb-4">
                        <label className="block text-sm font-medium text-gray-700">Email or Username</label>
                            <input
                                type="text"
                                className="w-full mt-1 px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-slate-500"
                                placeholder="Enter your email or username"
                        />
                    </div>

                    <div className="mb-2">
                        <label className="block text-sm font-medium text-gray-700">Password</label>
                        <input
                            type="password"
                            className="w-full mt-1 px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-slate-500"
                            placeholder="Enter your password"
                        />
                    </div>

                    <div className="text-right mb-6 underline">
                        <Link href="/reset" className="text-sm">
                            Forgot password?
                        </Link>
                    </div>

                    <button
                        type="submit"
                        className="w-full bg-slate-950 text-white cursor-pointer py-2 rounded-lg font-semibold hover:bg-slate-800 transition"
                    >
                        Sign In
                    </button>
                </form>

                {/* Divider */}
                <div className="flex items-center gap-4 my-6">
                    <div className="flex-grow h-px bg-gray-300" />
                        <span className="text-sm text-gray-400">or continue with</span>
                    <div className="flex-grow h-px bg-gray-300" />
                </div>

                {/* Google Sign In */}
                <button
                    className="flex items-center justify-center gap-3 bg-white border cursor-pointer border-gray-300 text-gray-700 px-6 py-2 rounded-lg w-full font-medium hover:shadow-md transition"
                >
                    <FcGoogle className="text-xl" />
                    Sign in with Google
                </button>

                {/* Sign Up Link */}
                <p className="mt-6 text-sm text-gray-500">
                    Don't have an account?{" "}
                    <Link href="/signup" className="text-slate-950 font-medium hover:underline">
                        Sign up
                    </Link>
                </p>

                {/* Disclaimer */}
                <p className="mt-6 text-xs text-gray-400">
                    By continuing, you agree to our Terms & Privacy Policy.
                </p>
            </div>
        </div>
  );
}