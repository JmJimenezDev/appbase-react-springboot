import { IoCheckmark } from "react-icons/io5"
import { MainSection } from "../../../layout/MainSection"
import { useAuth } from "../../auth/hooks/useAuth"
import { useState } from "react"
import { ProfileData } from "./ProfileData"
import { Preferences } from "./Preferences"
import { Pill, PillLink } from "../../../components/Pills"
import { useTranslation } from "react-i18next"
import clsx from "clsx"

type ViewType = "data" | "preferences" | "notifications"

export const Profile = () => {
    const { t } = useTranslation()
    const { user } = useAuth()
    const [selectedView, setSelectedView] = useState<ViewType>("data")

    const nameInitials = user?.name
        ?.split(" ")
        .map(word => word[0])
        .join("")
        .slice(0, 2)
        .toUpperCase()

    const renderContent = () => {
        switch (selectedView) {
            case "data":
                return <ProfileData />
            case "preferences":
                return <Preferences />
            case "notifications":
                return <div>{t("profile.notificationsPlaceholder")}</div>
            default:
                return null
        }
    }

    const menuItems: { key: ViewType; label: string; badge?: number }[] = [
        { key: "data", label: t("profile.my-data.title") },
        { key: "preferences", label: t("profile.preferences.title") },
        { key: "notifications", label: t("profile.notifications"), badge: 4 },
    ]

    return <MainSection>
        <div className="bg-white dark:bg-neutral-900 rounded-2xl shadow-md overflow-hidden flex flex-col lg:flex-row">
            <div className="pb-3 border-b lg:border-b-0 lg:border-r border-neutral-200 dark:border-neutral-800">
                <div className="relative h-30 bg-linear-to-r from-neutral-700 to-neutral-800">
                    <div className="absolute -bottom-8 left-3">
                        <div className="size-24 rounded-full bg-neutral-800 flex items-center justify-center text-white text-3xl font-bold border-4 border-white dark:border-neutral-900 shadow-lg">
                            {nameInitials}
                        </div>
                    </div>
                </div>

                <div className="flex justify-end items-center p-2 text-xs">
                    {user?.emailVerified ? <Pill type="green">
                        <IoCheckmark className="text-lg" aria-hidden="true" /> {t("profile.verified")}
                    </Pill>
                        :
                        <PillLink to="" type="yellow">
                            {t("profile.verify")}
                        </PillLink>}
                </div>

                <nav className="relative mt-10 min-w-60" aria-label={t("profile.menu")}>
                    <ul className="space-y-3">
                        {menuItems.map(item => (
                            <li key={item.key} className="w-full pr-8">
                                <button type="button" onClick={() => setSelectedView(item.key)}
                                    className={clsx('cursor-pointer w-full flex justify-between items-center gap-3 px-5 py-2 rounded-r-md transition-all duration-200 text-sm text-nowrap',
                                        selectedView === item.key ? 'font-bold bg-neutral-800 dark:bg-neutral-200 text-white dark:text-neutral-950' : 'hover:bg-neutral-400/30'
                                    )}
                                >
                                    <span>{item.label}</span>
                                    {item.badge && (
                                        <span className="bg-red-500 size-6 rounded-full flex items-center justify-center text-white text-xs ml-2">
                                            {item.badge}
                                        </span>
                                    )}
                                </button>
                            </li>
                        ))}
                    </ul>
                </nav>
            </div>

            <div className="w-100 h-120">{renderContent()}</div>
        </div>
    </MainSection>
}