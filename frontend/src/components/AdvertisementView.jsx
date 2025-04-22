import { useEffect, useState } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import axios from 'axios';
import '../assets/AdvertisementView.css';

export default function AdvertisementView() {
    const { id } = useParams();
    const navigate = useNavigate();
    const [ad, setAd] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [isAuthorized, setIsAuthorized] = useState(false);

    useEffect(() => {
        const checkAuthAndFetchAd = async () => {
            const token = localStorage.getItem('accessToken');

            if (!token) {
                navigate('/login');
                return;
            }

            setIsAuthorized(true);

            try {
                const response = await axios.get(`http://localhost:9000/api/advertisement/get/${id}`, {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });
                setAd(response.data);
            } catch (err) {
                if (err.response?.status === 401) {
                    setError('Сессия истекла. Пожалуйста, войдите снова.');
                    localStorage.removeItem('accessToken');
                    navigate('/login');
                } else {
                    setError(err.response?.data?.message || 'Не удалось загрузить объявление');
                }
            } finally {
                setLoading(false);
            }
        };

        checkAuthAndFetchAd();
    }, [id, navigate]);

    if (!isAuthorized) {
        return null; // или лоадер, так как будет редирект
    }

    if (loading) {
        return (
            <div className="ad-loading">
                <div className="loading-spinner"></div>
                <p>Загрузка объявления...</p>
            </div>
        );
    }

    if (error) {
        return (
            <div className="ad-error">
                <p className="error-message">{error}</p>
                <Link to="/" className="back-link">Вернуться на главную</Link>
            </div>
        );
    }

    if (!ad) {
        return (
            <div className="ad-not-found">
                <p>Объявление не найдено</p>
                <Link to="/" className="back-link">Вернуться на главную</Link>
            </div>
        );
    }

    return (
        <div className="ad-container">
            <div className="ad-header">
                <h1 className="ad-title">{ad.title}</h1>
                <Link to="/" className="back-link">← На главную</Link>
            </div>

            <div className="ad-content">
                <div className="ad-images">
                    {ad.images && ad.images.length > 0 ? (
                        ad.images.map((image) => (
                            <img
                                key={image.id}
                                src={image.url}
                                alt={ad.title}
                                className="ad-main-image"
                                onError={(e) => {
                                    e.target.onerror = null;
                                    e.target.src = '/placeholder-image.jpg';
                                }}
                            />
                        ))
                    ) : (
                        <div className="no-image-placeholder">Нет изображений</div>
                    )}
                </div>

                <div className="ad-details">
                    <div className="ad-price-section">
                        <span className="ad-price">{ad.price.toLocaleString()} ₽</span>
                        <span className="ad-category">{ad.category?.categoryName}</span>
                    </div>

                    <div className="ad-description">
                        <h3>Описание</h3>
                        <p>{ad.description}</p>
                    </div>

                    <div className="ad-author-info">
                        <h3>Автор объявления</h3>
                        <div className="author-details">
                            <p>{ad.user.name} {ad.user.surname}</p>
                            <p>{ad.user.email}</p>
                            {ad.user.phoneNumber && <p>{ad.user.phoneNumber}</p>}
                        </div>
                    </div>

                    <div className="ad-ratings">
                        <h3>Отзывы о продавце</h3>
                        {ad.user.ratings && ad.user.ratings.length > 0 ? (
                            <div className="ratings-list">
                                {ad.user.ratings.map((rating) => (
                                    <div key={rating.id} className="rating-item">
                                        <div className="rating-header">
                                            <span className="rating-author">{rating.fromUserName}</span>
                                            <span className="rating-stars">{'★'.repeat(rating.rating)}{'☆'.repeat(5 - rating.rating)}</span>
                                        </div>
                                        <p className="rating-text">{rating.text}</p>
                                        <span className="rating-date">
                                            {new Date(rating.createdAt).toLocaleDateString()}
                                        </span>
                                    </div>
                                ))}
                            </div>
                        ) : (
                            <p>Пока нет отзывов</p>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
}