import { Loader } from "../../../../components/Loader";
import { formatDateTime } from "../../../../utils/helpers";
import type { UserDTO } from "../types/user.types";

interface UsersCardsProps {
    data: UserDTO[];
    isLoading: boolean;
}

export const UsersCards = ({ data, isLoading }: UsersCardsProps) => {
    return <Loader isLoading={isLoading}>
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
            {data.map(user => (
                <div key={user.id} className="border rounded-lg p-4 shadow-sm hover:shadow-md transition-all bg-white dark:bg-neutral-900">
                    <h2 className="font-bold">{user.name} {user.surnames}</h2>
                    <p className="text-sm">{user.email}</p>
                    <p className="text-sm">{user.phone}</p>
                    <p className="text-sm">Rol: {user.roles.join(', ')}</p>
                    <p className="text-sm">
                        {user.emailVerified ? <span className='text-green-500'>Verificado</span> : <span className='text-red-500'>No verificado</span>}
                    </p>
                    <p className="text-sm">
                        {user.enabled ? <span className='text-green-500'>Habilitado</span> : <span className='text-red-500'>No habilitado</span>}
                    </p>
                    <p className="text-xs text-gray-400">Creado: {formatDateTime(user.createdAt?.toString())}</p>
                </div>
            ))}
        </div>
    </Loader>
};