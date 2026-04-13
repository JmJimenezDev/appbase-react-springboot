import { useEffect, useLayoutEffect, useState } from "react"
import { useTranslation } from "react-i18next"

type ThemeOption = "light" | "dark" | "system"
type LanguageOption = "es_ES" | "en_GB"

export const Preferences = () => {
  const { t, i18n } = useTranslation()

  const getInitialTheme = (): ThemeOption => {
    const stored = localStorage.getItem("selectedTheme") as ThemeOption | null
    if (!stored || stored === "system") return "system"
    return stored
  }

  const [theme, setTheme] = useState<ThemeOption>(getInitialTheme)

  const applyTheme = (appliedTheme: ThemeOption) => {
    const finalTheme =
      appliedTheme === "system"
        ? window.matchMedia("(prefers-color-scheme: dark)").matches
          ? "dark"
          : "light"
        : appliedTheme

    if (finalTheme === "dark") document.documentElement.classList.add("dark")
    else document.documentElement.classList.remove("dark")
  }

  useLayoutEffect(() => {
    applyTheme(theme)
  }, [theme])

  useEffect(() => {
    const selectedLanguage = localStorage.getItem("selectedLanguage") as LanguageOption
    if (selectedLanguage) i18n.changeLanguage(selectedLanguage)
  }, [i18n])

  const handleThemeSelection = (newTheme: ThemeOption) => {
    localStorage.setItem("selectedTheme", newTheme)
    setTheme(newTheme)
  }

  const handleLanguageSelection = (language: LanguageOption) => {
    localStorage.setItem("selectedLanguage", language)
    i18n.changeLanguage(language)
  }

  return (
    <div className="px-10 py-4 h-full w-full">
      <h1 className="text-2xl font-bold mb-5">{t("profile.preferences.title")}</h1>
      <div className="flex flex-col gap-5 justify-evenly">
        <div className="flex flex-col gap-1">
          <label htmlFor="language">{t("profile.preferences.language")}</label>
          <select
            id="language"
            value={i18n.language as LanguageOption}
            onChange={(e) => handleLanguageSelection(e.target.value as LanguageOption)}
            className="border rounded p-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="es_ES">{t("profile.preferences.spanish")}</option>
            <option value="en_GB">{t("profile.preferences.english")}</option>
          </select>
        </div>

        <div className="flex flex-col gap-1">
          <label htmlFor="theme">{t("profile.preferences.theme")}</label>
          <select
            id="theme"
            value={theme}
            onChange={(e) => handleThemeSelection(e.target.value as ThemeOption)}
            className="border rounded p-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="light">{t("profile.preferences.light")}</option>
            <option value="dark">{t("profile.preferences.dark")}</option>
            <option value="system">{t("profile.preferences.system")}</option>
          </select>
        </div>

        <div className="flex items-center gap-2">
          <input
            id="emailNotifications"
            type="checkbox"
            className="w-4 h-4 cursor-pointer focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
          <label htmlFor="emailNotifications">{t("profile.preferences.emailNotifications")}</label>
        </div>
      </div>
    </div>
  )
}