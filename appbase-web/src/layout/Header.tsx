import clsx from 'clsx'
import { IoLogOutOutline, IoPersonOutline } from 'react-icons/io5'
import { Link } from 'react-router-dom'
import { useAuth } from '../features/auth/hooks/useAuth'
import type { ReactNode } from 'react'
import { useTranslation } from 'react-i18next'

type HeaderProps = React.HTMLAttributes<HTMLElement> & {
    children?: ReactNode
}

export const Header = ({ className, children, ...props }: HeaderProps) => {
    const { t } = useTranslation();
    const { user, logout } = useAuth();

    return <header {...props} className={clsx('flex items-center justify-between px-5 bg-white dark:bg-neutral-800 rounded-2xl z-50', className)}>
        <div className="flex items-center gap-5">
            {children}
            <span className="font-medium text-sm sm:text-lg">{t('header.title')}</span>
        </div>

        <div className="flex items-center gap-5">
            <Link to="/profile" aria-label={t('header.profile')}
                className="size-10 p-0 sm:size-fit sm:py-2 sm:px-5 rounded-full flex gap-2 justify-center items-center bg-neutral-800 dark:bg-neutral-200 text-white dark:text-neutral-950 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-neutral-500"
            >
                <IoPersonOutline className="text-2xl" aria-hidden="true" />
                <span className="hidden sm:inline">
                    {user?.name ?? t('header.userFallback')}
                </span>
            </Link>

            <button onClick={logout} type="button" aria-label={t('header.logout')}
                className="p-2 rounded-full cursor-pointer hover:bg-red-50 hover:dark:bg-red-950 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500"
            >
                <IoLogOutOutline className="text-2xl text-red-500" aria-hidden="true" />
            </button>
        </div>
    </header>
}