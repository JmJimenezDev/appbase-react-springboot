import { useTranslation } from 'react-i18next'
import { MainSection } from '../../../layout/MainSection'

export const OnlyAdmin = () => {
  const { t } = useTranslation();

  return <MainSection>
    {t("only-admin.view")}
  </MainSection>
}
