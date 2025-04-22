
import { useState, useEffect } from 'react';
import axios from 'axios';

export const useAuth = () => {
    const [user, setUser] = useState(null);
    const [isAuthenticated, setIsAuthenticated] = useState(false);

    const initializeAuth = () => {
        const accessToken = localStorage.getItem('accessToken');
        const userData = localStorage.getItem('userData');

        if (accessToken && userData) {
            setUser(JSON.parse(userData));
            setIsAuthenticated(true);
        }
    };

    useEffect(() => {
        initializeAuth();
    }, []);

    const login = (authData) => {
        localStorage.setItem('accessToken', authData.accessToken);
        localStorage.setItem('refreshToken', authData.refreshToken);
        localStorage.setItem('userId', authData.userId);
        localStorage.setItem('userData', JSON.stringify(authData.user));
        setUser(authData.user);
        setIsAuthenticated(true);
    };

    const logout = () => {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        localStorage.removeItem('userId');
        localStorage.removeItem('userData');
        setUser(null);
        setIsAuthenticated(false);
    };

    const refreshToken = async () => {
        try {
            const refreshToken = localStorage.getItem('refreshToken');
            if (!refreshToken) {
                logout();
                return null;
            }

            const response = await axios.post('http://localhost:9000/api/auth/refresh', {
                refreshToken
            });

            if (response.data.accessToken) {
                localStorage.setItem('accessToken', response.data.accessToken);
                return response.data.accessToken;
            }
        } catch (err) {
            console.error('Ошибка обновления токена:', err);
            logout();
            return null;
        }
    };

    return {
        user,
        isAuthenticated,
        login,
        logout,
        refreshToken
    };
};