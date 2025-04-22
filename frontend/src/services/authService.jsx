
import axios from 'axios';

export const refreshToken = async () => {
    try {
        const refreshToken = localStorage.getItem('refreshToken');
        if (!refreshToken) {
            throw new Error('No refresh token available');
        }

        const response = await axios.post('http://localhost:9000/api/user/refresh', {
            refreshToken
        });

        if (response.data.accessToken) {
            localStorage.setItem('accessToken', response.data.accessToken);
            return response.data.accessToken;
        }
    } catch (err) {
        console.error('Ошибка обновления токена:', err);
        throw err;
    }
};

export const saveAuthData = (authData) => {
    localStorage.setItem('accessToken', authData.accessToken);
    localStorage.setItem('refreshToken', authData.refreshToken);
    localStorage.setItem('userId', authData.userId);
};

export const clearAuthData = () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('userId');
};