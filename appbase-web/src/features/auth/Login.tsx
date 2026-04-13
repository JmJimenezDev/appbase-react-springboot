import { zodResolver } from "@hookform/resolvers/zod"
import { useForm } from "react-hook-form"
import { Link, useNavigate } from "react-router-dom"
import { z } from "zod"
import { showErrorToast, showSuccessToast } from "../../utils/toastUtils"
import type { LoginRequestDTO } from "./types/auth.types"
import { useEffect } from "react"
import { useAuth } from "./hooks/useAuth"
import { useTranslation } from "react-i18next"

const loginSchema = z.object({
    email: z.string({ message: "login.emailInvalid" }),
    password: z.string().min(3, { message: "login.passwordMin" }),
})

type LoginFormValues = z.infer<typeof loginSchema>

export default function Login() {
    const { t } = useTranslation()
    const navigate = useNavigate()
    const { login, isAuthenticated } = useAuth()

    useEffect(() => {
        if (isAuthenticated) navigate("/profile")
    }, [isAuthenticated, navigate])

    const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm<LoginFormValues>({
        resolver: zodResolver(loginSchema),
    })

    function formatRetryTime(seconds: number) {
        if (seconds < 60) return `${seconds} ${seconds === 1 ? 'second' : 'seconds'}`;
        const minutes = Math.floor(seconds / 60);
        const remainingSeconds = seconds % 60;
        return remainingSeconds > 0
            ? `${minutes} ${minutes === 1 ? 'minute' : 'minutes'} ${remainingSeconds} ${remainingSeconds === 1 ? 'second' : 'seconds'}`
            : `${minutes} ${minutes === 1 ? 'minute' : 'minutes'}`;
    }

    const onSubmit = async (data: LoginFormValues) => {
        try {
            const loginData: LoginRequestDTO = {
                email: data.email,
                password: data.password,
            }

            await login(loginData)
            showSuccessToast(t("login.success"))
        } catch (error: any) {
            console.error("Login error", error)

            if (error.response?.status === 400)
                showErrorToast(t("login.invalidCredentials"))
            else if (error.response?.status === 500)
                showErrorToast(t("login.serverError"))
            else if (error.response?.status === 429) {
                const retryAfter = error.response?.data?.retryAfterSeconds;
                if (retryAfter != null) {
                    showErrorToast(t("login.tooManyAttempts", { time: formatRetryTime(retryAfter) }));
                } else {
                    showErrorToast(t("login.tooManyAttempts", { time: "?" }));
                }
            } else {
                showErrorToast(t("login.unknownError"))
            }
        }
    }

    return <main className="min-h-screen flex items-center justify-center bg-gray-100 px-4">
        <form onSubmit={handleSubmit(onSubmit)} aria-label={t("login.formLabel")}
            className="bg-white p-8 rounded-lg shadow-md w-full max-w-md"
        >
            <h1 className="text-2xl font-bold mb-6 text-center">
                {t("login.title")}
            </h1>

            <div className="mb-4">
                <label htmlFor="email" className="block mb-1 font-semibold">
                    {t("login.email")}
                </label>
                <input {...register("email")} id="email" type="text" placeholder={t("login.emailPlaceholder")}
                    className="w-full border rounded p-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                    aria-invalid={!!errors.email} {...(errors.email ? { "aria-describedby": "email-error" } : {})}
                />
                {errors.email && <p id="email-error" className="text-red-500 text-sm mt-1">
                    {t(errors.email.message!)}
                </p>}
            </div>

            <div className="mb-6">
                <label htmlFor="password" className="block mb-1 font-semibold">
                    {t("login.password")}
                </label>
                <input {...register("password")} id="password" type="password" placeholder={t("login.passwordPlaceholder")}
                    className="w-full border rounded p-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                    aria-invalid={!!errors.password} {...(errors.password ? { "aria-describedby": "password-error" } : {})}
                />
                {errors.password && <p id="password-error" className="text-red-500 text-sm mt-1">
                    {t(errors.password.message!)}
                </p>}
            </div>

            <button type="submit" disabled={isSubmitting}
                className="cursor-pointer primary w-full py-2 rounded transition mb-3"
            >
                {isSubmitting ? t("login.submitting") : t("login.submit")}
            </button>
            <Link to="/register" className="text-sm transition active:scale-95 block text-center w-full mb-3 rounded py-2 bg-neutral-400 text-neutral-900 dark:bg-neutral-700 dark:text-neutral-100 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-neutral-500">
                {t("login.register")}
            </Link>

            <div className="flex flex-col items-start gap-2">
                <Link to="/" className="text-blue-600 underline focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500">
                    {t("login.goHome")}
                </Link>
                <Link to="/reset-password" className="text-blue-600 underline focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500">
                    {t("login.forgotPassword")}
                </Link>
            </div>
        </form>
    </main>
}