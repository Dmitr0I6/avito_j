


import React, { useState } from 'react';

import { useNavigate } from 'react-router-dom';
import axios from 'axios';

const AuthComponent = () => {
    const [isLogin, setIsLogin] = useState(true);
    const [formData, setFormData] = useState({
        name: '',
        surname: '',
        username: '',
        password: '',
        email: '',
        phoneNumber: '',
    });
    const [error, setError] = useState('');
    const navigate = useNavigate();

    // Функция для сохранения данных аутентификации
    const saveAuthData = (authData) => {
        // Сохраняем в localStorage
        localStorage.setItem('accessToken', authData.accessToken);
        localStorage.setItem('refreshToken', authData.refreshToken);
        localStorage.setItem('userId', authData.userId);
        localStorage.setItem('userData', JSON.stringify(authData.user));

        // Устанавливаем токен в axios для всех последующих запросов
        axios.defaults.headers.common['Authorization'] = `Bearer ${authData.accessToken}`;
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value,
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        if (!isLogin && (!formData.name.trim() || !formData.surname.trim())) {
            setError('Имя и фамилия обязательны для заполнения');
            return;
        }

        try {
            const endpoint = isLogin
                ? 'http://localhost:9000/api/user/login'
                : 'http://localhost:9000/api/user/register';

            const payload = isLogin
                ? {
                    username: formData.username.trim(),
                    password: formData.password.trim()
                }
                : {
                    name: formData.name.trim(),
                    surname: formData.surname.trim(),
                    username: formData.username.trim(),
                    password: formData.password.trim(),
                    email: formData.email.trim(),
                    phoneNumber: formData.phoneNumber.trim()
                };

            const response = await axios.post(endpoint, payload);

            if (response.data) {
                // Сохраняем данные аутентификации
                saveAuthData({
                    accessToken: response.data.accessToken,
                    refreshToken: response.data.refreshToken,
                    userId: response.data.userId,
                    user: {
                        username: formData.username,
                        name: formData.name,
                        surname: formData.surname,
                        email: formData.email
                    }
                });

                navigate('/');
            }
        } catch (err) {
            setError(err.response?.data?.message || 'Произошла ошибка при авторизации');
            console.error('Ошибка авторизации:', err);
        }
    };

    return (
        <div style={{
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            minHeight: '100vh',
            backgroundColor: '#f5f7fa',
            padding: '20px',
            width: '100vw'
        }}>
            <div style={{
                width: '100%',
                maxWidth: '450px',
                padding: '40px',
                backgroundColor: 'white',
                borderRadius: '12px',
                boxShadow: '0 5px 15px rgba(0, 0, 0, 0.08)',
                margin: '0 auto'
            }}>
                <div style={{
                    textAlign: 'center',
                    marginBottom: '30px'
                }}>
                    <h2 style={{
                        fontSize: '28px',
                        fontWeight: '600',
                        color: '#2d3748',
                        marginBottom: '10px'
                    }}>
                        {isLogin ? 'Добро пожаловать!' : 'Создайте аккаунт'}
                    </h2>
                    <p style={{
                        color: '#718096',
                        fontSize: '16px'
                    }}>
                        {isLogin ? 'Войдите в свой аккаунт' : 'Заполните форму для регистрации'}
                    </p>
                </div>

                {error && (
                    <div style={{
                        padding: '15px',
                        backgroundColor: '#fee2e2',
                        color: '#dc2626',
                        borderRadius: '8px',
                        marginBottom: '25px',
                        textAlign: 'center',
                        fontSize: '15px'
                    }}>
                        {error}
                    </div>
                )}

                <form onSubmit={handleSubmit}>
                    <div style={{
                        display: 'grid',
                        gridTemplateColumns: '1fr 1fr',
                        gap: '20px',
                        marginBottom: '20px'
                    }}>
                        {!isLogin && (
                            <>
                                <div style={{ gridColumn: 'span 1' }}>
                                    <label style={{
                                        display: 'block',
                                        marginBottom: '8px',
                                        fontWeight: '500',
                                        color: '#4a5568',
                                        fontSize: '15px'
                                    }}>
                                        Имя
                                    </label>
                                    <input
                                        type="text"
                                        name="name"
                                        value={formData.name}
                                        onChange={handleChange}
                                        required={!isLogin}
                                        style={{
                                            width: '100%',
                                            padding: '14px',
                                            border: '1px solid #e2e8f0',
                                            borderRadius: '8px',
                                            fontSize: '15px',
                                            boxSizing: 'border-box',
                                            transition: 'border-color 0.2s',
                                            outline: 'none'
                                        }}
                                        onFocus={(e) => e.target.style.borderColor = '#3182ce'}
                                        onBlur={(e) => e.target.style.borderColor = '#e2e8f0'}
                                    />
                                </div>

                                <div style={{ gridColumn: 'span 1' }}>
                                    <label style={{
                                        display: 'block',
                                        marginBottom: '8px',
                                        fontWeight: '500',
                                        color: '#4a5568',
                                        fontSize: '15px'
                                    }}>
                                        Фамилия
                                    </label>
                                    <input
                                        type="text"
                                        name="surname"
                                        value={formData.surname}
                                        onChange={handleChange}
                                        required={!isLogin}
                                        style={{
                                            width: '100%',
                                            padding: '14px',
                                            border: '1px solid #e2e8f0',
                                            borderRadius: '8px',
                                            fontSize: '15px',
                                            boxSizing: 'border-box',
                                            transition: 'border-color 0.2s',
                                            outline: 'none'
                                        }}
                                        onFocus={(e) => e.target.style.borderColor = '#3182ce'}
                                        onBlur={(e) => e.target.style.borderColor = '#e2e8f0'}
                                    />
                                </div>
                            </>
                        )}

                        <div style={{ gridColumn: 'span 2' }}>
                            <label style={{
                                display: 'block',
                                marginBottom: '8px',
                                fontWeight: '500',
                                color: '#4a5568',
                                fontSize: '15px'
                            }}>
                                Имя пользователя
                            </label>
                            <input
                                type="text"
                                name="username"
                                value={formData.username}
                                onChange={handleChange}
                                required
                                style={{
                                    width: '100%',
                                    padding: '14px',
                                    border: '1px solid #e2e8f0',
                                    borderRadius: '8px',
                                    fontSize: '15px',
                                    boxSizing: 'border-box',
                                    transition: 'border-color 0.2s',
                                    outline: 'none'
                                }}
                                onFocus={(e) => e.target.style.borderColor = '#3182ce'}
                                onBlur={(e) => e.target.style.borderColor = '#e2e8f0'}
                            />
                        </div>

                        <div style={{ gridColumn: 'span 2' }}>
                            <label style={{
                                display: 'block',
                                marginBottom: '8px',
                                fontWeight: '500',
                                color: '#4a5568',
                                fontSize: '15px'
                            }}>
                                Пароль
                            </label>
                            <input
                                type="password"
                                name="password"
                                value={formData.password}
                                onChange={handleChange}
                                required
                                style={{
                                    width: '100%',
                                    padding: '14px',
                                    border: '1px solid #e2e8f0',
                                    borderRadius: '8px',
                                    fontSize: '15px',
                                    boxSizing: 'border-box',
                                    transition: 'border-color 0.2s',
                                    outline: 'none'
                                }}
                                onFocus={(e) => e.target.style.borderColor = '#3182ce'}
                                onBlur={(e) => e.target.style.borderColor = '#e2e8f0'}
                            />
                        </div>

                        {!isLogin && (
                            <>
                                <div style={{ gridColumn: 'span 2' }}>
                                    <label style={{
                                        display: 'block',
                                        marginBottom: '8px',
                                        fontWeight: '500',
                                        color: '#4a5568',
                                        fontSize: '15px'
                                    }}>
                                        Email
                                    </label>
                                    <input
                                        type="email"
                                        name="email"
                                        value={formData.email}
                                        onChange={handleChange}
                                        required
                                        style={{
                                            width: '100%',
                                            padding: '14px',
                                            border: '1px solid #e2e8f0',
                                            borderRadius: '8px',
                                            fontSize: '15px',
                                            boxSizing: 'border-box',
                                            transition: 'border-color 0.2s',
                                            outline: 'none'
                                        }}
                                        onFocus={(e) => e.target.style.borderColor = '#3182ce'}
                                        onBlur={(e) => e.target.style.borderColor = '#e2e8f0'}
                                    />
                                </div>

                                <div style={{ gridColumn: 'span 2' }}>
                                    <label style={{
                                        display: 'block',
                                        marginBottom: '8px',
                                        fontWeight: '500',
                                        color: '#4a5568',
                                        fontSize: '15px'
                                    }}>
                                        Телефон
                                    </label>
                                    <input
                                        type="tel"
                                        name="phoneNumber"
                                        value={formData.phoneNumber}
                                        onChange={handleChange}
                                        required
                                        style={{
                                            width: '100%',
                                            padding: '14px',
                                            border: '1px solid #e2e8f0',
                                            borderRadius: '8px',
                                            fontSize: '15px',
                                            boxSizing: 'border-box',
                                            transition: 'border-color 0.2s',
                                            outline: 'none'
                                        }}
                                        onFocus={(e) => e.target.style.borderColor = '#3182ce'}
                                        onBlur={(e) => e.target.style.borderColor = '#e2e8f0'}
                                    />
                                </div>
                            </>
                        )}
                    </div>

                    <button
                        type="submit"
                        style={{
                            width: '100%',
                            padding: '16px',
                            backgroundColor: '#4299e1',
                            color: 'white',
                            border: 'none',
                            borderRadius: '8px',
                            fontSize: '16px',
                            fontWeight: '600',
                            cursor: 'pointer',
                            marginTop: '15px',
                            transition: 'background-color 0.2s, transform 0.1s'
                        }}
                        onMouseOver={(e) => e.target.style.backgroundColor = '#3182ce'}
                        onMouseOut={(e) => e.target.style.backgroundColor = '#4299e1'}
                        onMouseDown={(e) => e.target.style.transform = 'scale(0.98)'}
                        onMouseUp={(e) => e.target.style.transform = 'scale(1)'}
                    >
                        {isLogin ? 'Войти' : 'Зарегистрироваться'}
                    </button>
                </form>

                <div style={{
                    textAlign: 'center',
                    marginTop: '25px',
                    color: '#718096',
                    fontSize: '15px'
                }}>
                    {isLogin ? 'Ещё нет аккаунта? ' : 'Уже зарегистрированы? '}
                    <button
                        onClick={() => {
                            setIsLogin(!isLogin);
                            setError('');
                        }}
                        style={{
                            background: 'none',
                            border: 'none',
                            color: '#4299e1',
                            cursor: 'pointer',
                            padding: '0',
                            fontSize: 'inherit',
                            fontWeight: '600',
                            textDecoration: 'underline'
                        }}
                    >
                        {isLogin ? 'Создать аккаунт' : 'Войти'}
                    </button>
                </div>
            </div>
        </div>
    );
};

export default AuthComponent;