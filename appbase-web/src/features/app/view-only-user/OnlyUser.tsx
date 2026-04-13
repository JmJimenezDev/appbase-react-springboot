import { useTranslation } from 'react-i18next'
import { MainSection } from '../../../layout/MainSection'

export const OnlyUser = () => {
    const { t } = useTranslation();

    return <MainSection>
        {t("only-user.view")}
    </MainSection>
}
