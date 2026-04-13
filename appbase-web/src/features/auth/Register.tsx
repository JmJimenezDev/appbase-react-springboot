import { zodResolver } from "@hookform/resolvers/zod"
import { useForm } from "react-hook-form"
import { Link, useNavigate } from "react-router-dom"
import { z } from "zod"
import { showErrorToast, showSuccessToast } from "../../utils/toastUtils"
import type { RegisterRequestDTO } from "./types/auth.types"
import { useEffect } from "react"
import { useAuth } from "./hooks/useAuth"
import { useTranslation } from "react-i18next"

const registerSchema = z.object({
    name: z.string().min(2, { message: "register.nameMin" }),
    surnames: z.string().min(2, { message: "register.surnamesMin" }),
    email: z.email({ message: "register.emailInvalid" }),
    phone: z.string().min(9, { message: "register.phoneMin" }).max(15, { message: "register.phoneMax" }),
    password: z.string().min(8, { message: "register.passwordMin" }),
})

type RegisterFormValues = z.infer<typeof registerSchema>

export default function Register() {
    const { t } = useTranslation()
    const navigate = useNavigate()
    const { register: registerUser, isAuthenticated } = useAuth()

    useEffect(() => {
        if (isAuthenticated) navigate("/profile")
    }, [isAuthenticated, navigate])

    const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm<RegisterFormValues>({
        resolver: zodResolver(registerSchema),
    })

    const onSubmit = async (data: RegisterFormValues) => {
        try {
            const registerData: RegisterRequestDTO = {
                name: data.name,
                surnames: data.surnames,
                email: data.email,
                phone: data.phone,
                password: data.password,
            }

            await registerUser(registerData)
            showSuccessToast(t("register.success"))
        } catch (error: any) {
            console.error("Register error", error)

            if (error.response?.status === 400)
                showErrorToast(t("register.invalidData"))
            else if (error.response?.status === 409)
                showErrorToast(t("register.emailExists"))
            else if (error.response?.status === 500)
                showErrorToast(t("register.serverError"))
            else
                showErrorToast(t("register.unknownError"))
        }
    }

    return (
        <main className="min-h-screen flex items-center justify-center bg-gray-100 px-4">
            <form onSubmit={handleSubmit(onSubmit)} aria-label={t("register.formLabel")}
                className="bg-white p-8 rounded-lg shadow-md w-full max-w-md"
            >
                <h1 className="text-2xl font-bold mb-6 text-center">
                    {t("register.title")}
                </h1>

                {/* Nombre */}
                <div className="mb-4">
                    <label htmlFor="name" className="block mb-1 font-semibold">
                        {t("register.name")}
                    </label>
                    <input {...register("name")} id="name" type="text"
                        placeholder={t("register.namePlaceholder")}
                        className="w-full border rounded p-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                    {errors.name && <p className="text-red-500 text-sm mt-1">
                        {t(errors.name.message!)}
                    </p>}
                </div>

                {/* Apellidos */}
                <div className="mb-4">
                    <label htmlFor="surnames" className="block mb-1 font-semibold">
                        {t("register.surnames")}
                    </label>
                    <input {...register("surnames")} id="surnames" type="text"
                        placeholder={t("register.surnamesPlaceholder")}
                        className="w-full border rounded p-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                    {errors.surnames && <p className="text-red-500 text-sm mt-1">
                        {t(errors.surnames.message!)}
                    </p>}
                </div>

                {/* Email */}
                <div className="mb-4">
                    <label htmlFor="email" className="block mb-1 font-semibold">
                        {t("register.email")}
                    </label>
                    <input {...register("email")} id="email" type="email"
                        placeholder={t("register.emailPlaceholder")}
                        className="w-full border rounded p-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                    {errors.email && <p className="text-red-500 text-sm mt-1">
                        {t(errors.email.message!)}
                    </p>}
                </div>

                {/* Teléfono */}
                <div className="mb-4">
                    <label htmlFor="phone" className="block mb-1 font-semibold">
                        {t("register.phone")}
                    </label>
                    <input {...register("phone")} id="phone" type="text"
                        placeholder={t("register.phonePlaceholder")}
                        className="w-full border rounded p-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                    {errors.phone && <p className="text-red-500 text-sm mt-1">
                        {t(errors.phone.message!)}
                    </p>}
                </div>

                {/* Password */}
                <div className="mb-6">
                    <label htmlFor="password" className="block mb-1 font-semibold">
                        {t("register.password")}
                    </label>
                    <input {...register("password")} id="password" type="password"
                        placeholder={t("register.passwordPlaceholder")}
                        className="w-full border rounded p-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                    {errors.password && <p className="text-red-500 text-sm mt-1">
                        {t(errors.password.message!)}
                    </p>}
                </div>

                <button type="submit" disabled={isSubmitting}
                    className="cursor-pointer primary w-full py-2 rounded transition mb-3"
                >
                    {isSubmitting ? t("register.submitting") : t("register.submit")}
                </button>

                <div className="flex flex-col items-start gap-2">
                    <Link to="/login" className="text-blue-600 underline">
                        {t("register.goLogin")}
                    </Link>
                    <Link to="/" className="text-blue-600 underline">
                        {t("register.goHome")}
                    </Link>
                </div>
            </form>
        </main>
    )
}