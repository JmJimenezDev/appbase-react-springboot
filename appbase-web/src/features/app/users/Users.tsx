import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { BsFiletypeCsv, BsFiletypePdf } from "react-icons/bs";
import { IoAddOutline, IoGridOutline, IoListOutline, IoSearchOutline } from 'react-icons/io5';
import { Paginator } from '../../../components/Paginator';
import { useTableController } from '../../../hooks/useTableController';
import { MainSection } from '../../../layout/MainSection';
import { UsersCards } from './components/UsersCards';
import { UsersFilters } from './components/UsersFilters';
import { UsersModalExport } from './components/UsersModalExport';
import { UsersTable } from './components/UsersTable';
import { useUsers } from './hooks/useUsers';

interface UsersFiltersType {
    [key: string]: unknown;
    role?: string;
    emailVerified?: string;
    enabled?: string;
    name?: string;
    surnames?: string;
    email?: string;
    phone?: string;
    startDate?: Date;
    endDate?: Date;
}

export const Users = () => {
    const { t } = useTranslation();

    const [viewMode, setViewMode] = useState<'table' | 'cards'>('table');

    const [openModalExportPDF, setOpenModalExportPDF] = useState(false);
    const [openModalExportCSV, setOpenModalExportCSV] = useState(false);

    const { data, isLoading, size, setPage, setSize,
        setSearch, setSort, sort, filters, setFilters, resetFilters,
    } = useTableController<UsersFiltersType>({
        queryFn: useUsers,
        defaultSort: "createdAt,desc",
        defaultFilters: {},
    });

    const handleSort = (field: string) => {
        setSort(field);
    };

    return <MainSection>
        <div className='w-full min-h-full p-10'>
            <h1 className='text-4xl font-bold mb-10 text-start w-full'>Usuarios</h1>

            <div className='flex items-center justify-between flex-wrap gap-2 mb-3'>
                <div className='flex items-center flex-wrap gap-2'>
                    <button onClick={() => {
                        setViewMode('table');
                        setSize(10);
                    }}
                        className='cursor-pointer hover:bg-neutral-100 hover:dark:bg-neutral-600 transition-all flex items-center gap-2 rounded-md px-3 py-2 border border-neutral-300 dark:border-neutral-600'
                    >
                        <IoGridOutline /> Tabla
                    </button>

                    <button
                        onClick={() => {
                            setViewMode('cards');
                            setSize(6);
                        }}
                        className='cursor-pointer hover:bg-neutral-100 hover:dark:bg-neutral-600 transition-all flex items-center gap-2 rounded-md px-3 py-2 border border-neutral-300 dark:border-neutral-600'
                    >
                        <IoListOutline /> Lista
                    </button>
                </div>

                <div className='flex items-center flex-wrap gap-2'>
                    <div className='flex items-center gap-2 rounded-md pl-2 pr-1 py-1 border border-neutral-300 dark:border-neutral-600'>
                        <IoSearchOutline className='size-5' />
                        <input type='search' placeholder='Buscar...' onChange={(e) => setSearch(e.target.value)}
                            className='outline-none border-none bg-transparent max-w-64 rounded placeholder-gray-400'
                        />
                    </div>
                    <button
                        onClick={() => setOpenModalExportPDF(true)}
                        className='cursor-pointer hover:bg-neutral-100 hover:dark:bg-neutral-600 transition-all flex items-center gap-2 rounded-md px-3 py-2 border border-neutral-300 dark:border-neutral-600'
                    >
                        <BsFiletypePdf className='size-5' />
                        {t("others.export-pdf")}
                    </button>

                    <button
                        onClick={() => setOpenModalExportCSV(true)}
                        className='cursor-pointer hover:bg-neutral-100 hover:dark:bg-neutral-600 transition-all flex items-center gap-2 rounded-md px-3 py-2 border border-neutral-300 dark:border-neutral-600'
                    >
                        <BsFiletypeCsv className='size-5' />
                        {t("others.export-csv")}
                    </button>

                    <button className='primary flex items-center gap-2 rounded-md px-3 py-2 border border-neutral-300 dark:border-neutral-600'>
                        <IoAddOutline className='size-5' />
                        Añadir usuario
                    </button>
                </div>
            </div>

            <hr className='text-neutral-300 dark:text-neutral-600 my-3' />

            <UsersFilters
                role={filters.role}
                emailVerified={filters.emailVerified}
                enabled={filters.enabled}

                onRoleChange={(value) => setFilters({ role: value })}
                onEmailVerifiedChange={(value) => setFilters({ emailVerified: value })}
                onEnabledChange={(value) => setFilters({ enabled: value })}

                advancedFilters={filters}
                onApplyAdvanced={(f) => setFilters(f)}
                onClearAdvanced={resetFilters}
            />

            {viewMode === 'table' ? <UsersTable
                data={data?.content || []}
                isLoading={isLoading}
                sort={sort}
                onSortChange={handleSort}
            /> :
                <UsersCards
                    data={data?.content || []}
                    isLoading={isLoading}
                />
            }

            <Paginator
                currentPage={data?.number ?? 0}
                totalPages={data?.totalPages ?? 0}
                totalElements={data?.totalElements ?? 0}
                pageSize={size}
                onPageChange={setPage}
                onPageSizeChange={setSize}
                sizes={viewMode === "cards" ? [6, 12, 30] : [10, 20, 50]}
            />
        </div>

        <UsersModalExport
            type="PDF"
            isOpen={openModalExportPDF}
            onClose={() => setOpenModalExportPDF(false)}
        />

        <UsersModalExport
            type="CSV"
            isOpen={openModalExportCSV}
            onClose={() => setOpenModalExportCSV(false)}
        />
    </MainSection>
};