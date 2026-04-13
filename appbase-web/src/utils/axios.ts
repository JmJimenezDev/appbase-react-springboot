import axios, { AxiosError, type AxiosRequestConfig } from "axios";
import { getCookie } from "./helpers";
import { redirect } from "react-router-dom";

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  withCredentials: true,
});

let isRefreshing = false;
let failedQueue: ((tokenRefreshed: boolean) => void)[] = [];

const processQueue = (tokenRefreshed = false) => {
  failedQueue.forEach((prom) => prom(tokenRefreshed));
  failedQueue = [];
};

api.interceptors.request.use((config) => {
  const csrfToken = getCookie("csrf_token");
  if (csrfToken) config.headers["X-CSRF-Token"] = csrfToken;

  return config;
});

api.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const originalRequest = error.config as AxiosRequestConfig & { _retry?: boolean };

    if (error.response?.status === 401 && !originalRequest._retry) {
      if (isRefreshing) {
        return new Promise((resolve) =>
          failedQueue.push((tokenRefreshed: boolean) => {
            if (tokenRefreshed) resolve(api(originalRequest));
            else resolve(Promise.reject(error));
          }),
        );
      }

      originalRequest._retry = true;
      isRefreshing = true;

      try {
        await api.post("/auth/refresh");
        processQueue(true);
        return api(originalRequest);
      } catch (err) {
        processQueue(false);
        redirect("/login");
        return Promise.reject(err);
      } finally {
        isRefreshing = false;
      }
    }

    return Promise.reject(error);
  },
);
