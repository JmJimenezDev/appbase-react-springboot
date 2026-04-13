import clsx from 'clsx'
import { preferencesLayoutAtom } from '../atoms/preferencesLayoutAtom'
import { useAtom } from 'jotai'
import { IoCloseOutline, IoPeopleOutline } from 'react-icons/io5'
import { LuArrowLeftFromLine, LuArrowRightFromLine } from 'react-icons/lu'
import { NavLink } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { useAuth } from '../features/auth/hooks/useAuth'

type SideMenuProps = React.HTMLAttributes<HTMLElement> & {
    isOpen: boolean
    setIsOpen: (open: boolean) => void
}

export const SideMenu = ({ className, isOpen, setIsOpen, ...props }: SideMenuProps) => {
    const { t } = useTranslation();
    const { user } = useAuth();
    const [preferences, setPreferences] = useAtom(preferencesLayoutAtom)
    const isCollapsed = preferences.size === 'COLLAPSED'

    const toggleCollapse = () => {
        setPreferences(prev => ({
            ...prev,
            size: prev.size === 'COLLAPSED' ? 'EXPANDED' : 'COLLAPSED'
        }))
    }

    const sections = [
        { name: t('menu.sections.admin-user'), url: '/admin-user', icon: <IoPeopleOutline aria-hidden="true" />, roles: ['ROLE_ADMIN', 'ROLE_USER'] },
        { name: t('menu.sections.only-user'), url: '/only-user', icon: <IoPeopleOutline aria-hidden="true" />, roles: ['ROLE_USER'] },
        { name: t('menu.sections.only-admin'), url: '/only-admin', icon: <IoPeopleOutline aria-hidden="true" />, roles: ['ROLE_ADMIN'] },
        { name: t('menu.sections.users'), url: '/users', icon: <IoPeopleOutline aria-hidden="true" />, roles: ['ROLE_ADMIN'] },
    ]

    const filteredSections = sections.filter(section =>
        section.roles.some((role: string) => user?.roles.includes(role))
    );

    return <aside {...props} role="navigation" aria-label={t('menu.navigation')}
        className={clsx(className, isCollapsed ? 'w-28' : 'w-64', isOpen ? 'translate-x-0' : '-translate-x-full')}>
        <div className="p-2 h-full">
            <div className="h-full rounded-2xl overflow-y-auto flex flex-col bg-white dark:bg-neutral-800 border border-l-0 2xl:border-0 border-neutral-300 dark:border-neutral-700">
                <div className="flex flex-col">
                    <div className="h-16 flex items-center justify-between gap-2">
                        <button type="button" aria-label={t('menu.closeNavigation')} onClick={() => setIsOpen(false)}
                            className="cursor-pointer items-center rounded-full text-2xl 2xl:hidden ml-5 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-neutral-500"
                        >
                            <IoCloseOutline className="size-6" aria-hidden="true" />
                        </button>

                        <button aria-expanded={!isCollapsed} onClick={toggleCollapse}
                            aria-label={isCollapsed ? t('menu.expand') : t('menu.collapse')}
                            className={clsx(
                                'flex-1 cursor-pointer hover:bg-neutral-400/30 px-1 2xl:px-5 py-1 mr-5 flex items-center gap-3 rounded-md 2xl:rounded-none 2xl:rounded-r-md transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-neutral-500',
                                isCollapsed ? '2xl:mr-3' : '2xl:mr-7'
                            )}
                        >
                            {isCollapsed ?
                                <LuArrowRightFromLine className="size-6" aria-hidden="true" />
                                :
                                <LuArrowLeftFromLine className="size-6" aria-hidden="true" />
                            }

                            {!isCollapsed && <span>{t('menu.collapse')}</span>}
                        </button>
                    </div>

                    <hr className="text-neutral-100 dark:text-neutral-900 border-4" />

                    <nav className="py-8 flex flex-col gap-3">
                        {filteredSections.map(section => (
                            <NavLink to={section.url} key={section.url} aria-label={section.name}
                                className={({ isActive }) =>
                                    clsx('flex items-center gap-3 px-5 py-2 rounded-r-md transition-all duration-200 text-sm text-nowrap',
                                        isCollapsed ? 'mr-3' : 'mr-7',
                                        isActive ? 'font-bold bg-neutral-800 dark:bg-neutral-200 text-white dark:text-neutral-950' : 'hover:bg-neutral-400/30'
                                    )
                                }
                            >
                                <span className="text-xl">{section.icon}</span>

                                {!isCollapsed && section.name}
                            </NavLink>
                        ))}
                    </nav>
                </div>
            </div>
        </div>
    </aside>
}