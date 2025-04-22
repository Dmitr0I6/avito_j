import CreateAdvertisementForm from "./components/CreateAdvertisementForm";
import AuthComponent from "./components/AuthComponent.jsx";
import AdvertisementView from "./components/AdvertisementView.jsx";
import Home from "./components/Home.jsx";
import { useAuth } from './hooks/useAuth';
import { setupAxiosInterceptors } from './utils/axiosInterceptor';

import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import {useEffect} from "react";


function App() {
    const { logout } = useAuth();

    useEffect(() => {
        setupAxiosInterceptors(logout);
    }, [logout]);
    return (
        <Router>
            <Routes>
                <Route path="/auth" element={<AuthComponent />} />
                <Route path="/" element={<Home />} />
                <Route path="/createadvertisement" element={<CreateAdvertisementForm />}/>
                <Route path="/advertisement/:id" element={<AdvertisementView />} />
            </Routes>
        </Router>
    );
}

export default App;