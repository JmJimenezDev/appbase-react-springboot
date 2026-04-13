import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { exportUsersToCSV, exportUsersToPDF } from '../../../../api/users.api';
import { Modal } from '../../../../components/Modal';
import { showErrorToast } from '../../../../utils/toastUtils';

export const UsersModalExport = ({ isOpen, onClose, type = "PDF" }: { isOpen: boolean; onClose: () => void; type: "PDF" | "CSV" }) => {
    const { t } = useTranslation();
    const [selectedFields, setSelectedFields] = useState<string[]>([]);

    const exportableFields = [
        { label: "Nombre", value: "name" },
        { label: "Apellidos", value: "surnames" },
        { label: "Email", value: "email" },
        { label: "Fecha de alta", value: "createdAt" },
        { label: "Teléfono", value: "phone" },
        { label: "Rol", value: "role" },
        { label: "Estado", value: "enabled" },
        { label: "Verificado", value: "emailVerified" },
    ];

    const toggleField = (field: string) => {
        setSelectedFields(prev =>
            prev.includes(field) ? prev.filter(f => f !== field) : [...prev, field]
        );
    };

    const handleExport = () => {
        if (selectedFields.length === 0) {
            showErrorToast("Debes seleccionar al menos un campo para exportar");
            return;
        }
        try {
            if (type === "PDF")
                exportUsersToPDF(selectedFields)
            else
                exportUsersToCSV(selectedFields);

            onClose();
        } catch (e) {
            console.error(e);
            showErrorToast("Error exportando los datos");
        }
    };

    return <Modal isOpen={isOpen} onClose={onClose} closeOnOutsideClick={false}>
        <Modal.Header>{type === "PDF" ? t("others.export-pdf") : t("others.export-csv")}</Modal.Header>
        <Modal.Body>
            <p className='text-sm mb-3'>Selecciona los campos que quieres exportar:</p>
            <div className="flex flex-col gap-2 mt-2">
                {exportableFields.map(f => (
                    <label key={f.value} className="flex items-center gap-2 w-max">
                        <input type="checkbox" checked={selectedFields.includes(f.value)} onChange={() => toggleField(f.value)} />
                        <strong>{f.label}</strong>
                    </label>
                ))}
            </div>
        </Modal.Body>
        <Modal.Footer>
            <button className="secondary px-4 py-2" onClick={onClose}>
                {t("commons.cancel")}
            </button>
            <button className="primary px-4 py-2" onClick={handleExport} disabled={selectedFields.length === 0}>
                {t("commons.export")}
            </button>
        </Modal.Footer>
    </Modal>
};