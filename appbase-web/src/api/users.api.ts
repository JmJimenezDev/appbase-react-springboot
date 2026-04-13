import type { UserDTO } from "../features/app/users/types/user.types";
import { api } from "../utils/axios";

const baseUrl = "/users";

export const getUsers = async (params?: { search?: string; role?: string; page?: number; size?: number; sort?: string; emailVerified?: string; enabled?: string }) => {
  const { data } = await api.get(baseUrl, { params });
  return data;
};

export const getUserById = async (id: number) => {
  const { data } = await api.get(`${baseUrl}/${id}`);
  return data;
};

export const createUser = async (user: UserDTO) => {
  const { data } = await api.post(baseUrl, user);
  return data;
};

export const updateUser = async (id: number, user: UserDTO) => {
  const { data } = await api.put(`${baseUrl}/${id}`, user);
  return data;
};

export const deleteUser = async (id: number) => {
  await api.delete(`${baseUrl}/${id}`);
};

export const exportUsersToPDF = async (fields: string[]) => {
  const params = {
    fields: fields.join(","),
  };

  const response = await api.get(`${baseUrl}/export/pdf`, {
    responseType: "blob",
    params,
  });

  const blob = new Blob([response.data], { type: "application/pdf" });
  const url = window.URL.createObjectURL(blob);
  const link = document.createElement("a");
  link.href = url;

  link.setAttribute("download", `users_${new Date().toISOString().replace(/[:.]/g, "-")}.pdf`);
  document.body.appendChild(link);
  link.click();
  link.remove();
};

export const exportUsersToCSV = async (fields: string[]) => {
  const params = {
    fields: fields.join(","),
  };

  const response = await api.get(`${baseUrl}/export/csv`, {
    responseType: "blob",
    params,
  });

  const blob = new Blob([response.data], { type: "text/csv" });
  const url = window.URL.createObjectURL(blob);
  const link = document.createElement("a");
  link.href = url;

  link.setAttribute("download", `users_${new Date().toISOString().replace(/[:.]/g, "-")}.csv`);
  document.body.appendChild(link);
  link.click();
  link.remove();
};
