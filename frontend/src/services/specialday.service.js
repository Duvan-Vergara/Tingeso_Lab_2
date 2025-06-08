import httpClient from "../http-common";

const API_URL = '/api/specialtariffs/specialdays/';

const getAllSpecialDays = () => {
    return httpClient.get(API_URL);
};

const createSpecialDay = (specialDay) => {
    return httpClient.post(API_URL, specialDay);
};

const getSpecialDayById = (id) => {
    return httpClient.get(`${API_URL}${id}`);
};

const deleteSpecialDayById = (id) => {
    return httpClient.delete(`${API_URL}${id}`);
};

export default {
    getAllSpecialDays,
    createSpecialDay,
    getSpecialDayById,
    deleteSpecialDayById,
};