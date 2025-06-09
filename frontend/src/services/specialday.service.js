import httpClient from "../http-common";

const API_URL = '/api/specialtariffs/specialdays/';

const getAllSpecialDays = () => {
    console.log("Intnetando Get:", httpClient.defaults.baseURL + API_URL);
    return httpClient.get(API_URL);
};

const createSpecialDay = (specialDay) => {
    console.log("Intnetando Post:", httpClient.defaults.baseURL + API_URL);
    return httpClient.post(API_URL, specialDay);
};

const getSpecialDayById = (id) => {
    console.log("Intnetando Get by ID:", httpClient.defaults.baseURL + API_URL + id);
    return httpClient.get(`${API_URL}${id}`);
};

const deleteSpecialDayById = (id) => {
    console.log("Intnetando Delete by ID:", httpClient.defaults.baseURL + API_URL + id);
    return httpClient.delete(`${API_URL}${id}`);
};

export default {
    getAllSpecialDays,
    createSpecialDay,
    getSpecialDayById,
    deleteSpecialDayById,
};