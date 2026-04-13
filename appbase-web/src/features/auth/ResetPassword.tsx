import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
import { Link } from 'react-router-dom';
import { z } from 'zod';
import { showErrorToast, showSuccessToast } from '../../utils/toastUtils';
import { api } from '../../utils/axios';

const loginSchema = z.object({
    email: z.email({ message: "Email inválido" })
});

type LoginFormValues = z.infer<typeof loginSchema>;

export const ResetPassword = () => {

    const {
        register,
        handleSubmit,
        formState: { errors, isSubmitting },
    } = useForm<LoginFormValues>({
        resolver: zodResolver(loginSchema),
    });

    const onSubmit = async (data: LoginFormValues) => {
        try {
            await api.get("/email/reset-password?emailTo=" + data.email);

            showSuccessToast("Email de restablecimiento enviado correctamente");
        } catch (error) {
            console.error("Error al enviar el email de restablecimiento", error);
            showErrorToast("Error al enviar el email de restablecimiento. Por favor, inténtalo de nuevo más tarde.");
        }
    };

    return <div className="min-h-screen flex items-center justify-center bg-gray-100">
        <form
            onSubmit={handleSubmit(onSubmit)}
            className="bg-white p-8 rounded-lg shadow-md w-full max-w-md"
        >
            <h2 className="text-2xl font-bold mb-6 text-center">Cambiar contraseña</h2>

            <div className="mb-4">
                <label className="block mb-1 font-semibold">Email</label>
                <input
                    {...register("email")}
                    type="email"
                    className="w-full border rounded p-2"
                    placeholder="correo@ejemplo.com"
                />
                {errors.email && (
                    <p className="text-red-500 text-sm mt-1">{errors.email.message}</p>
                )}
            </div>

            <button
                type="submit"
                disabled={isSubmitting}
                className="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700 transition mb-3"
            >
                {isSubmitting ? "Envaindo email" : "Solicitar cambio de contraseña"}
            </button>

            <div className="flex flex-col items-start gap-2">
                <Link to="/login" className="text-blue-600 underline">Go to Login</Link>
            </div>
        </form>
    </div>
}
