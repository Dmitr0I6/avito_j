import { Link } from 'react-router-dom';

export default function Home() {
    return (
        <div>
            <h1>Главная страница</h1>
            <Link to="/auth">Войти</Link> <br />
            <Link to="/createadvertisement">Создать объявление</Link>
        </div>
    );
}