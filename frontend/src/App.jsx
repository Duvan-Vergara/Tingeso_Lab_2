import './App.css'
import { BrowserRouter as Router, Route, Routes, Navigate } from 'react-router-dom'
import Navbar from "./components/Navbar"
import Home from './components/Home';


import TariffList from './components/tariff/TariffList';
import AddEditTariff from './components/tariff/AddEditTariff';

import TariffSpecialList from './components/tariff/TariffSpecialList';
import AddEditTariffSpecial from './components/tariff/AddEditTariffSpecial';

import SpecialDayList from './components/tariff/SpecialDayList';
import AddEditSpecialDay from './components/tariff/AddEditSpecialDay';

import UserList from './components/reserve/UsersList';
import AddEditUser from './components/reserve/AddEditUser';

import ReserveList from './components/reserve/ReserveList';
import AddEditReserve from './components/reserve/AddEditReserve';
import ReportGenerator from './components/reserve/ReportGenerator';

import DesctNumberList from './components/reserve/DesctNumberList';
import AddEditDesctNumber from './components/reserve/AddEditDesctNumber';

import DesctFrecList from './components/reserve/DesctFrecList';
import AddEditDesctFrec from './components/reserve/AddEditDesctFrec';

import Rack from './components/reserve/Rack';

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
          
          <Route path="/desctnumber/list" element={<DesctNumberList />} />
          <Route path="/desctnumber/add" element={<AddEditDesctNumber />} />
          <Route path="/desctnumber/edit" element={<AddEditDesctNumber />} />

          <Route path="/desctfrec/list" element={<DesctFrecList />} />
          <Route path="/desctfrec/add" element={<AddEditDesctFrec />} />
          <Route path="/desctfrec/edit" element={<AddEditDesctFrec />} />

          <Route path="/specialdays/list" element={<SpecialDayList />} />
          <Route path="/specialdays/add" element={<AddEditSpecialDay />} />
          <Route path="/specialdays/edit/:id" element={<AddEditSpecialDay />} />
          
          <Route path="/tariff/list" element={<TariffList />} />
          <Route path="/tariff/add" element={<AddEditTariff />} />
          <Route path="/tariff/edit/:id" element={<AddEditTariff />} />
          <Route path="/tariff/special/list" element={<TariffSpecialList />} />
          <Route path="/tariff/special/edit" element={<AddEditTariffSpecial />} />
          
          <Route path="/reports/generate" element={<ReportGenerator />} />

          <Route path="/rack/semana" element={<Rack />} />

        </Routes>
      </div>
    </Router>
  );
}

export default App