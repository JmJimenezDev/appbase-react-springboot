import { useMutation, useQueryClient } from "@tanstack/react-query";
import type { UserDTO } from "../types/user.types";
import { createUser, deleteUser, updateUser } from "../../../../api/users.api";
import { showErrorToast, showSuccessToast } from "../../../../utils/toastUtils";

interface UpdateUserParams {
  id: number;
  data: UserDTO;
}

export const useUserMutations = () => {
  const queryClient = useQueryClient();

  const create = useMutation<UserDTO, Error, UserDTO>({
    mutationFn: createUser,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["users"] });
      showSuccessToast("Usuario creado correctamente");
    },
    onError: (error) => {
      showErrorToast(error?.message || "Error creando usuario");
    },
  });

  const update = useMutation<UserDTO, Error, UpdateUserParams>({
    mutationFn: ({ id, data }) => updateUser(id, data),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ["users"] });
      queryClient.invalidateQueries({ queryKey: ["user", variables.id] });
      showSuccessToast("Usuario actualizado correctamente");
    },
    onError: (error) => {
      showErrorToast(error?.message || "Error actualizando usuario");
    },
  });

  const remove = useMutation<void, Error, number>({
    mutationFn: (id) => deleteUser(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["users"] });
      showSuccessToast("Usuario eliminado correctamente");
    },
    onError: (error) => {
      showErrorToast(error?.message || "Error eliminando usuario");
    },
  });

  return { create, update, remove };
};
