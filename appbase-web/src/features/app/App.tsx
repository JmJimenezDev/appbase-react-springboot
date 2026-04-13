import { Outlet } from 'react-router-dom'
import { Header } from '../../layout/Header'
import { SideMenu } from '../../layout/SideMenu'
import { preferencesLayoutAtom } from '../../atoms/preferencesLayoutAtom'
import { useAtom } from 'jotai'
import clsx from 'clsx'
import { useEffect, useRef, useState } from 'react'
import { IoMenuOutline } from 'react-icons/io5'
import { t } from 'i18next'

export const App = () => {
    const [isMenuOpen, setIsMenuOpen] = useState<boolean>(false)
    const menuRef = useRef<HTMLDivElement | null>(null)

    const [preferences] = useAtom(preferencesLayoutAtom)
    const isCollapsed = preferences.size === 'COLLAPSED'

    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
            const target = event.target as Node

            if (isMenuOpen && menuRef.current && !menuRef.current.contains(target))
                setIsMenuOpen(false)
        }

        const handleEscape = (event: KeyboardEvent) => {
            if (event.key === 'Escape')
                setIsMenuOpen(false)
        }

        document.addEventListener('mousedown', handleClickOutside)
        document.addEventListener('keydown', handleEscape)

        return () => {
            document.removeEventListener('mousedown', handleClickOutside)
            document.removeEventListener('keydown', handleEscape)
        }
    }, [isMenuOpen])

    return <div className="flex gap-2 min-h-screen text-neutral-950 bg-neutral-100 dark:text-neutral-50 dark:bg-neutral-900 py-2 p-2 2xl:pl-0">
        <div ref={menuRef}>
            <SideMenu id="side-menu" isOpen={isMenuOpen} setIsOpen={setIsMenuOpen}
                className="h-screen fixed top-0 left-0 z-60 2xl:translate-x-0 transition-all 2xl:shadow" />
        </div>

        <div className={clsx('flex flex-col w-full', isCollapsed ? '2xl:pl-28' : '2xl:pl-64')}>
            <Header className="h-16 sticky top-0 shadow">
                <button type="button" onClick={() => setIsMenuOpen(prev => !prev)} aria-expanded={isMenuOpen} aria-controls="side-menu"
                    aria-label={isMenuOpen ? t('menu.close') : t('menu.open')}
                    className="cursor-pointer inline-flex items-center text-sm rounded-full 2xl:hidden transition focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-neutral-500"
                >
                    <span className="sr-only">
                        {isMenuOpen ? t('menu.close') : t('menu.open')}
                    </span>
                    <IoMenuOutline className="size-6" aria-hidden="true" />
                </button>
            </Header>

            <div className="bg-white dark:bg-neutral-800 rounded-2xl mt-2 flex-1 shadow">
                <Outlet />
            </div>
        </div>
    </div>
}