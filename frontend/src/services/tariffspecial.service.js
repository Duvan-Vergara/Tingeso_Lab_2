import httpClient from "../http-common";

const API_URL = '/api/specialtariffs/';

const getTariffSpecial = () => {
    return httpClient.get(API_URL);
};

const createTariffSpecial = (tariff) => {
    return httpClient.post(`${API_URL}crear`, tariff);
};

const updateTariffSpecial = (tariff) => {
    return httpClient.post(`${API_URL}actualizar`, tariff);
};

export default {
    getTariffSpecial,
    createTariffSpecial,
    updateTariffSpecial,
};