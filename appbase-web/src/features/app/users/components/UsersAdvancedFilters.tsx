import { useEffect, useState } from "react";

interface AdvancedFiltersValues {
    name?: string;
    surnames?: string;
    email?: string;
    phone?: string;
    startDate?: Date;
    endDate?: Date;
}

interface Props {
    initialValues: AdvancedFiltersValues;
    onApply: (filters: AdvancedFiltersValues) => void;
    onClear: () => void;
}

export const UsersAdvancedFilters = ({ initialValues = {}, onApply, onClear }: Props) => {
    const [filters, setFilters] = useState<AdvancedFiltersValues>(initialValues || {});

    useEffect(() => {
        setFilters(initialValues);
    }, [initialValues]);

    const handleChange = (key: keyof AdvancedFiltersValues, value: string) => {
        setFilters(prev => ({
            ...prev,
            [key]: key === "startDate" || key === "endDate" ? (value ? new Date(value) : undefined) : value || undefined
        }));
    };

    const formatDate = (date?: Date) => date ? date.toISOString().split("T")[0] : "";

    const handleSubmit = (e: React.SyntheticEvent<HTMLFormElement>) => {
        e.preventDefault();
        onApply(filters);
    };

    return <form onSubmit={handleSubmit} className="mb-5 w-full p-4 border border-neutral-300 dark:border-neutral-600 rounded-xl bg-neutral-100 dark:bg-neutral-800 shadow-sm">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            <div className="flex flex-col gap-1">
                <label className="text-xs">Nombre</label>
                <input type="text" value={filters.name || ""} onChange={(e) => handleChange("name", e.target.value)} className="input" placeholder="Nombre" />
            </div>

            <div className="flex flex-col gap-1">
                <label className="text-xs">Apellidos</label>
                <input type="text" value={filters.surnames || ""} onChange={(e) => handleChange("surnames", e.target.value)} className="input" placeholder="Apellidos" />
            </div>

            <div className="flex flex-col gap-1">
                <label className="text-xs">Email</label>
                <input type="text" value={filters.email || ""} onChange={(e) => handleChange("email", e.target.value)} className="input" placeholder="Email" />
            </div>

            <div className="flex flex-col gap-1">
                <label className="text-xs">Teléfono</label>
                <input type="text" value={filters.phone || ""} onChange={(e) => handleChange("phone", e.target.value)} className="input" placeholder="Teléfono" />
            </div>

            <div className="flex flex-col gap-1">
                <label className="text-xs">Fecha de alta - Desde</label>
                <input
                    type="date"
                    value={formatDate(filters.startDate)}
                    onChange={(e) => handleChange("startDate", e.target.value)}
                    className="input"
                />
            </div>

            <div className="flex flex-col gap-1">
                <label className="text-xs">Fecha de alta - Hasta</label>
                <input type="date" value={formatDate(filters.endDate)} onChange={(e) => handleChange("endDate", e.target.value)} className="input" />
            </div>
        </div>

        <div className="flex flex-wrap justify-end gap-2 mt-4">
            <button type="button" className="px-4 py-2 secondary"
                onClick={() => {
                    setFilters({});
                    onClear();
                }}
            >
                Limpiar
            </button>

            <button type="submit" className="primary">
                Aplicar filtros
            </button>
        </div>
    </form>
};