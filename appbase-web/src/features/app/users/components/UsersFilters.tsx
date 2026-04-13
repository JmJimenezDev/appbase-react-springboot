import { useState } from "react";
import { UsersAdvancedFilters } from "./UsersAdvancedFilters";
import clsx from "clsx";

interface AdvancedFiltersValues {
    name?: string;
    surnames?: string;
    email?: string;
    phone?: string;
    startDate?: Date;
    endDate?: Date;
}

interface UsersFiltersProps {
    role?: string;
    emailVerified?: string;
    enabled?: string;

    onRoleChange: (role?: string) => void;
    onEmailVerifiedChange: (value?: string) => void;
    onEnabledChange: (value?: string) => void;

    advancedFilters: AdvancedFiltersValues;
    onApplyAdvanced: (filters: AdvancedFiltersValues) => void;
    onClearAdvanced: () => void;
}

export const UsersFilters = ({
    role,
    emailVerified,
    enabled,
    onRoleChange,
    onEmailVerifiedChange,
    onEnabledChange,
    advancedFilters,
    onApplyAdvanced,
    onClearAdvanced
}: UsersFiltersProps) => {

    const [advancedOpen, setAdvancedOpen] = useState(false);

    const baseBtn = "text-xs cursor-pointer rounded-full border py-1 px-4 border-neutral-300 dark:border-neutral-600 hover:bg-neutral-100 hover:dark:bg-neutral-600 transition-all";
    const activeBtn = "bg-neutral-200 dark:bg-neutral-500";

    const getClass = (current?: string, value?: string) =>
        `${baseBtn} ${current === value ? activeBtn : ""}`;

    return <>
        <div className='flex flex-col gap-3'>
            <div className="flex flex-col-reverse lg:flex-row lg:items-center justify-between">
                <button
                    onClick={() => setAdvancedOpen(prev => !prev)}
                    className="mt-3 text-xs text-start w-max text-blue-500 underline hover:text-blue-600 transition"
                >
                    {advancedOpen ? "Ocultar búsqueda avanzada" : "Búsqueda avanzada"}
                </button>

                <div className='flex flex-col lg:flex-row lg:items-center gap-3 lg:gap-10'>
                    <div className='flex items-center gap-2'>
                        <span className='text-xs'>Rol:</span>
                        <button onClick={() => onRoleChange(role === "ROLE_USER" ? undefined : "ROLE_USER")} className={getClass(role, "ROLE_USER")}>
                            USER
                        </button>
                        <button onClick={() => onRoleChange(role === "ROLE_ADMIN" ? undefined : "ROLE_ADMIN")} className={getClass(role, "ROLE_ADMIN")}>
                            ADMIN
                        </button>
                    </div>

                    <div className='flex items-center gap-2'>
                        <span className='text-xs'>Email:</span>
                        <button onClick={() => onEmailVerifiedChange(emailVerified === "true" ? undefined : "true")} className={getClass(emailVerified, "true")}>
                            Verificado
                        </button>
                        <button onClick={() => onEmailVerifiedChange(emailVerified === "false" ? undefined : "false")} className={getClass(emailVerified, "false")}>
                            No verificado
                        </button>
                    </div>

                    <div className='flex items-center gap-2'>
                        <span className='text-xs'>Estado:</span>
                        <button onClick={() => onEnabledChange(enabled === "true" ? undefined : "true")} className={getClass(enabled, "true")}>
                            Habilitado
                        </button>
                        <button onClick={() => onEnabledChange(enabled === "false" ? undefined : "false")} className={getClass(enabled, "false")}>
                            No habilitado
                        </button>
                    </div>
                </div>
            </div>
            <div className={clsx("transition-all duration-300 overflow-hidden", advancedOpen ? "opacity-100 max-h-150" : "opacity-0 max-h-0")}>
                {advancedOpen && <UsersAdvancedFilters
                    initialValues={advancedFilters}
                    onApply={onApplyAdvanced}
                    onClear={onClearAdvanced}
                />}
            </div>
        </div>
    </>
};