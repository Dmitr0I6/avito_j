import axios from 'axios';
import { refreshToken, clearAuthData } from '../services/authService';

const PUBLIC_ENDPOINTS = [
    '/api/advertisement/page',
    '/api/category/all',
    '/api/user/login',
    '/api/user/register',
    '/api/user/refresh',
    '/api/advertisement/get/**'
];

export const setupAxiosInterceptors = (logoutCallback) => {
    // Удаляем глобальный заголовок Authorization
    delete axios.defaults.headers.common['Authorization'];

    // Интерцептор запросов
    axios.interceptors.request.use(config => {
        const isPublicEndpoint = PUBLIC_ENDPOINTS.some(endpoint =>
            config.url.includes(endpoint)
        );

        if (!isPublicEndpoint) {
            const accessToken = localStorage.getItem('accessToken');
            if (accessToken) {
                config.headers.Authorization = `Bearer ${accessToken}`;
            }
        }
        return config;
    });

    // Интерцептор ответов
    axios.interceptors.response.use(
        response => response,
        async error => {
            const originalRequest = error.config;

            // // Проверяем, что это ошибка 401 и запрос не публичный
            // const isPublicEndpoint = PUBLIC_ENDPOINTS.some(endpoint =>
            //     originalRequest.url.includes(endpoint)
            // );

            if (error.response?.status === 401  && !originalRequest._retry) {
                originalRequest._retry = true;

                try {
                    const newAccessToken = await refreshToken();
                    if (newAccessToken) {
                        // Обновляем токен в оригинальном запросе
                        originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
                        // Повторяем оригинальный запрос с новым токеном
                        return axios(originalRequest);
                    }
                } catch (refreshError) {
                    console.error('Refresh token failed:', refreshError);
                    clearAuthData();
                    if (logoutCallback) {
                        logoutCallback();
                    }
                    return Promise.reject(new Error('Session expired. Please login again.'));
                }
            }

            // Если это 401 ошибка и мы уже пытались обновить токен
            if (error.response?.status === 401 && !isPublicEndpoint) {
                clearAuthData();
                if (logoutCallback) {
                    logoutCallback();
                }
                return Promise.reject(new Error('Authorization required. Please login.'));
            }

            return Promise.reject(error);
        }
    );
};