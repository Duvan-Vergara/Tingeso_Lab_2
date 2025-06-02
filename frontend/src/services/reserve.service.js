import httpClient from "../http-common";

const API_URL = '/api/v1/reserves/';

const getAllReserves = () => {
    return httpClient.get(API_URL);
};

const getReserveById = (id) => {
    return httpClient.get(`${API_URL}${id}`);
};

const saveReserve = (reserve) => {
    return httpClient.post(API_URL, reserve);
};

const deleteReserveById = (id) => {
    return httpClient.delete(`${API_URL}${id}`);
};

const listReservesByRutAndMonth = (rut, month) => {
    return httpClient.get(`${API_URL}${rut}/${month}`);
};

const listReservesByDay = (day) => {
    return httpClient.get(`${API_URL}day/${day}`);
};

const listReservesByMonth = (month) => {
    return httpClient.get(`${API_URL}month/${month}`);
};

const listReservesByWeek = (year, month, week) => {
    return httpClient.get(`${API_URL}week/${year}/${month}/${week}`);
};

const sendPaymentReceipt = (id) => {
    return httpClient.get(`${API_URL}${id}/payment-receipt`);
};

const sendPaymentReceiptV2 = (id) => {
    return httpClient.get(`${API_URL}${id}/payment-receipt-v2`);
};

const generateTariffReport = (startDate, endDate) => {
    return httpClient.get(`${API_URL}report/tariff`, {
        params: { startDate, endDate },
        responseType: 'blob', // Para manejar archivos descargables
    });
};

const generateGroupSizeReport = (startDate, endDate) => {
    return httpClient.get(`${API_URL}report/group-size`, {
        params: { startDate, endDate },
        responseType: 'blob', // Para manejar archivos descargables
    });
};

const calculateFinalPrice = (reserve) => {
    return httpClient.post("/api/v1/reserves/calculate-price", reserve);
};


export default {
    getAllReserves,
    getReserveById,
    saveReserve,
    deleteReserveById,
    listReservesByRutAndMonth,
    listReservesByDay,
    listReservesByMonth,
    listReservesByWeek,
    sendPaymentReceipt,
    sendPaymentReceiptV2,
    generateTariffReport,
    generateGroupSizeReport,
    calculateFinalPrice,
};