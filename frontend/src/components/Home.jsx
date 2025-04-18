import { Link } from 'react-router-dom';
import { useEffect, useState } from 'react';
import axios from 'axios';
import '../assets/Home.css';

export default function Home() {
    const [ads, setAds] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchAds = async () => {
            try {
                const response = await axios.get('http://localhost:9000/api/advertisement/page');
                setAds(response.data);
            } catch (err) {
                setError(err.message);
            } finally {
                setLoading(false);
            }
        };

        fetchAds();
    }, []);

    return (
        <div className="home-container">
            <header className="home-header">
                <h1 className="home-title">Доска объявлений</h1>
                <div className="home-actions">
                    <Link to="/auth" className="home-button primary">Войти</Link>
                    <Link to="/createadvertisement" className="home-button secondary">Создать объявление</Link>
                </div>
            </header>

            {loading ? (
                <div className="loading-spinner"></div>
            ) : error ? (
                <div className="error-message">{error}</div>
            ) : (
                <div className="ads-grid">
                    {ads.map(ad => (
                        <div key={ad.id} className="ad-card">
                            {ad.images && ad.images.length > 0 && (
                                <img
                                    src={ad.images[0].url}
                                    alt={ad.title}
                                    className="ad-image"
                                    onError={(e) => {
                                        e.target.onerror = null;
                                        e.target.src = '/placeholder-image.jpg';
                                    }}
                                />
                            )}
                            <div className="ad-content">
                                <div className="ad-category">{ad.category?.categoryName}</div>
                                <h3 className="ad-title">{ad.title}</h3>
                                <p className="ad-price">{ad.price.toLocaleString()} ₽</p>
                                <p className="ad-description">
                                    {ad.description.length > 100
                                        ? `${ad.description.substring(0, 100)}...`
                                        : ad.description}
                                </p>
                                <div className="ad-footer">
                                    <span className="ad-author">{ad.user.name} {ad.user.surname}</span>
                                    <Link to={`/advertisement/${ad.id}`} className="ad-link">Подробнее</Link>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}