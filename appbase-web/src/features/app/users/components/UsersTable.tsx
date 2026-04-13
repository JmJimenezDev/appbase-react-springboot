import { IoArrowDownOutline, IoArrowUpOutline, IoPencilOutline, IoSwapVerticalOutline, IoTrashOutline } from 'react-icons/io5';
import type { UserDTO } from '../types/user.types';
import { Loader } from '../../../../components/Loader';
import { formatDateTime } from '../../../../utils/helpers';

interface UsersTableProps {
    data: UserDTO[];
    isLoading: boolean;
    sort: string;
    onSortChange: (field: string) => void;
}

export const UsersTable = ({ data, isLoading, sort, onSortChange }: UsersTableProps) => {
    const getSortIndicator = (field: string) => {
        const [currentField, direction] = sort.split(",");
        if (currentField !== field) return <IoSwapVerticalOutline className='size-4' />;
        return direction === "asc" ? <IoArrowUpOutline className='size-4' /> : <IoArrowDownOutline className='size-4' />;
    };

    return <Loader isLoading={isLoading}>
        <div className="w-full overflow-x-auto">
            <table className="min-w-200 w-full table-auto text-sm whitespace-nowrap">
                <thead className="bg-neutral-50 dark:bg-neutral-900">
                    <tr>
                        {["name", "surnames", "email", "createdAt"].map((field) => (
                            <th key={field} onClick={() => onSortChange(field)}
                                className="cursor-pointer px-4 py-2 border border-neutral-300 dark:border-neutral-600 max-w-37.5 text-ellipsis overflow-hidden whitespace-nowrap"
                            >
                                <div className="flex justify-center items-center gap-2 select-none">
                                    <span className="truncate">
                                        {field === "name"
                                            ? "Nombre"
                                            : field === "surnames"
                                                ? "Apellidos"
                                                : field === "email"
                                                    ? "Email"
                                                    : "Fecha de alta"}
                                    </span>
                                    {getSortIndicator(field)}
                                </div>
                            </th>
                        ))}
                        <th className="px-4 py-1 border border-neutral-300 dark:border-neutral-600 max-w-30 truncate">Teléfono</th>
                        <th className="px-4 py-1 border border-neutral-300 dark:border-neutral-600 max-w-30 truncate">Rol</th>
                        <th className="px-4 py-1 border border-neutral-300 dark:border-neutral-600 max-w-35 truncate">Verificación email</th>
                        <th className="px-4 py-1 border border-neutral-300 dark:border-neutral-600 max-w-30 truncate">Estado</th>
                        <th className="px-4 py-1 border border-neutral-300 dark:border-neutral-600 max-w-30 truncate">Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    {data.map((user) => (
                        <tr key={user.id} className="hover:bg-gray-50 hover:dark:bg-neutral-900/40">
                            <td className="px-4 py-2 border border-neutral-300 dark:border-neutral-600 max-w-37.5 truncate">{user.name}</td>
                            <td className="px-4 py-2 border border-neutral-300 dark:border-neutral-600 max-w-37.5 truncate">{user.surnames}</td>
                            <td className="px-4 py-2 border border-neutral-300 dark:border-neutral-600 max-w-50 truncate">{user.email}</td>
                            <td className="px-4 py-2 border border-neutral-300 dark:border-neutral-600 max-w-35 truncate">{formatDateTime(user.createdAt?.toString())}</td>
                            <td className="px-4 py-2 border border-neutral-300 dark:border-neutral-600 max-w-30 truncate">{user.phone}</td>
                            <td className="px-4 py-2 border border-neutral-300 dark:border-neutral-600 max-w-37.5 truncate">{user.roles.join(", ")}</td>
                            <td className="px-4 py-2 border border-neutral-300 dark:border-neutral-600 max-w-35 truncate">
                                <span className={`inline-block size-2 rounded-full mr-1 ${user.emailVerified ? 'bg-green-500' : 'bg-red-500'}`} />
                                {user.emailVerified ? "Verificado" : "No verificado"}
                            </td>
                            <td className="px-4 py-2 border border-neutral-300 dark:border-neutral-600 max-w-30 truncate">
                                <span className={`inline-block size-2 rounded-full mr-1 ${user.enabled ? 'bg-green-500' : 'bg-red-500'}`} />
                                {user.enabled ? "Habilitado" : "No habilitado"}
                            </td>
                            <td className="px-1 py-2 border border-neutral-300 dark:border-neutral-600 max-w-30">
                                <div className='flex justify-center items-center gap-1'>
                                    <button className='cursor-pointer flex items-center gap-1 py-1 px-1 rounded border border-neutral-300 dark:border-neutral-600'><IoPencilOutline /></button>
                                    <button className='cursor-pointer flex items-center gap-1 py-1 px-1 rounded border border-neutral-300 dark:border-neutral-600'><IoTrashOutline /></button>
                                </div>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    </Loader>
};