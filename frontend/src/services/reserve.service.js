import httpClient from "../http-common";

const API_URL = '/api/reserves/';

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

const sendPaymentReceipt = (id) => {
    return httpClient.get(`${API_URL}${id}/payment-receipt`);
};

const sendPaymentReceiptV2 = (id) => {
    return httpClient.get(`${API_URL}${id}/payment-receipt-v2`);
};

const calculateFinalPrice = (reserve) => {
    return httpClient.post(`${API_URL}calculate-price`, reserve);
};

export default {
    getAllReserves,
    getReserveById,
    saveReserve,
    deleteReserveById,
    sendPaymentReceipt,
    sendPaymentReceiptV2,
    calculateFinalPrice,
};