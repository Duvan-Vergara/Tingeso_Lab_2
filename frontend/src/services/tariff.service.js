import httpClient from "../http-common";

const API_URL = '/api/v1/tariffs/';

const getAllTariffs = () => {
    return httpClient.get(API_URL);
};

const saveTariff = (tariff) => {
    return httpClient.post(API_URL, tariff);
};

const getTariffById = (id) => {
    return httpClient.get(`${API_URL}${id}`);
};

const deleteTariffById = (id) => {
    return httpClient.delete(`${API_URL}${id}`);
};

export default {
    getAllTariffs,
    saveTariff,
    getTariffById,
    deleteTariffById,
};