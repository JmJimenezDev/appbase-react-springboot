import { useQuery } from "@tanstack/react-query";
import { getUserById, getUsers } from "../../../../api/users.api";
import type { Page } from "../../../../types/paginator.types";
import type { UserDTO } from "../types/user.types";

export const useUsers = (params?: { search?: string; role?: string; page?: number; size?: number; sort?: string; emailVerified?: string; enabled?: string }) => {
  return useQuery<Page<UserDTO>, Error>({
    queryKey: ["users", params],
    queryFn: () => getUsers(params),
  });
};

export const useUser = (id: number) => {
  return useQuery<UserDTO, Error>({
    queryKey: ["user", id],
    queryFn: () => getUserById(id),
    enabled: !!id,
  });
};
