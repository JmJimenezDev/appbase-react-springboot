import { useAuth } from '../../auth/hooks/useAuth'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { useTranslation } from 'react-i18next'
import { useUserMutations } from '../users/hooks/useUserMutations'
import { useAtom } from 'jotai'
import { loaderAtom } from '../../../atoms/loaderAtom'
import type { LoggedUserDTO } from '../../auth/types/auth.types'

const profileSchema = z.object({
  name: z.string().min(2, { message: 'profile.my-data.nameMin' }),
  surnames: z.string().min(2, { message: 'profile.my-data.surnamesMin' }),
  phone: z.string().min(5, { message: 'profile.my-data.phoneMin' }),
})

type ProfileFormValues = z.infer<typeof profileSchema>

export const ProfileData = () => {
  const { t } = useTranslation()
  const { user, setAuth } = useAuth()
  const { update } = useUserMutations();
  const [, setLoader] = useAtom(loaderAtom);

  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm<ProfileFormValues>({
    resolver: zodResolver(profileSchema),
    defaultValues: {
      name: user?.name ?? '',
      surnames: user?.surnames ?? '',
      phone: user?.phone ?? '',
    },
  })

  const onSubmit = async (data: ProfileFormValues) => {
    if (!user) return;

    setLoader(true);
    try {
      const updatedUser = await update.mutateAsync({
        id: user.id,
        data: {
          ...user,
          ...data,
          enabled: true
        },
      });

      const loggedUser: LoggedUserDTO = {
        ...updatedUser,
        emailVerified: user.emailVerified,
      };

      setAuth({
        isAuthenticated: true,
        user: loggedUser,
      });
    } catch (e) {
      console.error(e);
    } finally {
      setLoader(false);
    }
  }

  return <div className="px-10 py-4 h-full w-full">
    <h1 className="text-2xl font-bold mb-5">{t('profile.my-data.title')}</h1>

    <form onSubmit={handleSubmit(onSubmit)} className="flex flex-col gap-5 justify-evenly">
      <div className="flex flex-col gap-1">
        <label htmlFor="name">{t('profile.my-data.name')}</label>
        <input id="name" {...register('name')} className="border rounded p-2"
          aria-invalid={!!errors.name} {...(errors.name ? { "aria-describedby": "name-error" } : {})}
        />
        {errors.name && <p id="name-error" className="text-red-500 text-sm">
          {t(errors.name.message!)}
        </p>}
      </div>

      <div className="flex flex-col gap-1">
        <label htmlFor="surnames">{t('profile.my-data.surnames')}</label>
        <input id="surnames" {...register('surnames')} className="border rounded p-2"
          aria-invalid={!!errors.surnames} {...(errors.surnames ? { "aria-describedby": "surnames-error" } : {})}
        />
        {errors.surnames && <p id="surnames-error" className="text-red-500 text-sm">
          {t(errors.surnames.message!)}
        </p>}
      </div>

      <div className="flex flex-col gap-1">
        <label htmlFor="phone">{t('profile.my-data.phone')}</label>
        <input id="phone" {...register('phone')} className="border rounded p-2"
          aria-invalid={!!errors.phone} {...(errors.phone ? { "aria-describedby": "phone-error" } : {})}
        />
        {errors.phone && <p id="phone-error" className="text-red-500 text-sm">
          {t(errors.phone.message!)}
        </p>}
      </div>

      <div className="flex flex-col gap-1">
        <label htmlFor="email">{t('profile.my-data.email')}</label>
        <input id="email" value={user?.email ?? ''} disabled readOnly
          className="border rounded p-2 bg-gray-100 cursor-not-allowed"
        />
      </div>

      <div className="flex items-center justify-center gap-3">
        <button type="submit" disabled={isSubmitting} className="primary flex-1">
          {t('commons.save')}
        </button>
        <button type="button" className="secondary w-max" onClick={() => console.log('Cambiar contraseña')}>
          {t('profile.my-data.changePassword')}
        </button>
      </div>
    </form>
  </div>
}