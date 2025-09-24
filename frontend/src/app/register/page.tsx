"use client";

import Image from "next/image";
import Link from "next/link";
import { useState } from "react";
import { FcGoogle } from "react-icons/fc";

export default function RegisterPage() {
    const [form, setForm] = useState({
        firstName: "",
        lastName: "",
        email: "",
        password: "",
    });

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setForm({ ...form, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        console.log(form);
    };

    return (
        <div className="flex items-center justify-center min-h-screen bg-slate-200 px-4">
            <div className="w-full max-w-md bg-white p-10 md:shadow-lg rounded-xl text-center">
                {/* Logo */}
                <div className="mb-8">
                    <Image src="/logo.png" alt="Savo Logo" width={60} height={60} className="mx-auto" />
                    <h1 className="text-2xl font-bold mt-4">Create Your Account</h1>
                    <p className="text-sm text-gray-600 mt-2">Start turning your bank statements into instant insights</p>
                </div>

                <form onSubmit={handleSubmit} className="space-y-4 text-left">
                    <div className="flex gap-4">
                        {/* First Name */}
                        <div className="w-1/2">
                            <label className="block text-sm font-medium text-gray-700">First Name</label>
                            <input
                                type="text"
                                name="firstName"
                                placeholder="Jacob"
                                onChange={handleChange}
                                required
                                className="w-full mt-1 px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-slate-300"
                            />
                        </div>

                        {/* Last Name */}
                        <div className="w-1/2">
                            <label className="block text-sm font-medium text-gray-700">Last Name</label>
                            <input
                                type="text"
                                name="lastName"
                                placeholder="Loh"
                                onChange={handleChange}
                                className="w-full mt-1 px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-slate-300"
                            />
                        </div>
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700">Email</label>
                        <input
                            type="email"
                            name="email"
                            placeholder="jacobloh@gmail.com"
                            onChange={handleChange}
                            required
                            className="w-full mt-1 px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-slate-300"
                        />
                    </div>
                    
                    <div>
                        <label className="block text-sm font-medium text-gray-700">Password</label>
                        <input
                            type="password"
                            name="password"
                            placeholder="********"
                            onChange={handleChange}
                            required
                            className="w-full mt-1 px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-slate-300"
                        />
                    </div>
                    

                    <button
                        type="submit"
                        className="w-full bg-slate-950 text-white cursor-pointer py-2 rounded-lg font-semibold hover:bg-slate-800 transition"
                    >
                        Sign Up
                    </button>
                </form>

                {/* Divider */}
                <div className="flex items-center my-6">
                    <div className="flex-grow h-px bg-gray-200" />
                        <span className="px-4 text-gray-500 text-sm font-medium">or continue with</span>
                    <div className="flex-grow h-px bg-gray-200" />
                </div>

                {/* Google Sign In */}
                <button
                    className="flex items-center justify-center gap-3 bg-white border cursor-pointer border-gray-300 text-gray-700 px-6 py-2 rounded-lg w-full font-medium hover:shadow-md transition"
                >
                    <FcGoogle className="text-xl" />
                        Sign in with Google
                </button>

                {/* Redirect to Login */}
                <p className="mt-6 text-sm text-center text-gray-500">
                    Already have an account?{" "}
                    <Link href="/login" className="text-slate-950 font-medium hover:underline">
                        Log in
                    </Link>
                </p>
            </div>
        </div>
    );
}