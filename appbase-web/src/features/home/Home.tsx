import { Link } from 'react-router-dom'
import { useTranslation } from 'react-i18next'

export const Home = () => {
  const { t } = useTranslation()

  return (
    <main className="bg-neutral-300 text-neutral-800 flex justify-center items-center flex-col min-h-screen px-4">
      <h1 className="text-4xl font-bold mb-4">
        {t('home.title')}
      </h1>

      <p className="text-center">
        {t('home.welcome')}
      </p>

      <p className="text-center">
        {t('home.description')}
      </p>

      <p className="text-center">
        {t('home.extra')}
      </p>

      <Link
        to="/login"
        aria-label={t('home.goToLogin')}
        className="mt-4 bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
      >
        {t('home.goToLogin')}
      </Link>
    </main>
  )
}