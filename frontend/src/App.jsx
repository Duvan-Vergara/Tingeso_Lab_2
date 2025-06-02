import './App.css'
import { BrowserRouter as Router, Route, Routes, Navigate } from 'react-router-dom'
import Navbar from "./components/Navbar"
import Home from './components/Home';
import UserList from './components/UsersList';
import AddEditUser from './components/AddEditUser';
import ReserveList from './components/ReserveList';
import TariffList from './components/TariffList';
import AddEditTariff from './components/AddEditTariff';
import AddEditSpecialDay from './components/AddEditSpecialDay';
import AddEditReserve from './components/AddEditReserve';
import SpecialDayList from './components/SpecialDayList';
import ReportGenerator from './components/ReportGenerator';
import NotFound from './components/NotFound';

function App() {
  return (
    <Router>
      <div className="container">
        <Navbar></Navbar>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/home" element={<Home />} />
          <Route path="/user/list" element={<UserList />} />
          <Route path="/user/add" element={<AddEditUser />} />
          <Route path="/user/edit/:id" element={<AddEditUser />} />
          <Route path="/reserve/list" element={<ReserveList />} />
          <Route path="/reserve/add" element={<AddEditReserve />} />
          <Route path="/reserve/edit/:id" element={<AddEditReserve />} />
          <Route path="/specialdays/list" element={<SpecialDayList />} />
          <Route path="/specialdays/add" element={<AddEditSpecialDay />} />
          <Route path="/specialdays/edit/:id" element={<AddEditSpecialDay />} />
          <Route path="/tariff/list" element={<TariffList />} />
          <Route path="/tariff/add" element={<AddEditTariff />} />
          <Route path="/tariff/edit/:id" element={<AddEditTariff />} />
          <Route path="/reports/generate" element={<ReportGenerator />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App