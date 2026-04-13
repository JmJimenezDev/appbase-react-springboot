import { useTranslation } from 'react-i18next';
import { MainSection } from '../../../layout/MainSection';

export const ViewAdminUser = () => {
    const { t } = useTranslation();

    return <MainSection>
        {t("view-admin-user.view")}
    </MainSection>
}
