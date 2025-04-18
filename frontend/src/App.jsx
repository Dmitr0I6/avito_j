import CreateAdvertisementForm from "./components/CreateAdvertisementForm";
import AuthComponent from "./components/AuthComponent.jsx";
import Home from "./components/Home.jsx";
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';

function App() {
    return (
        <Router>
            <Routes>
                <Route path="/auth" element={<AuthComponent />} />
                <Route path="/" element={<Home />} />
                <Route path="/createadvertisement" element={<CreateAdvertisementForm />}/>
            </Routes>
        </Router>
    );
}

export default App;