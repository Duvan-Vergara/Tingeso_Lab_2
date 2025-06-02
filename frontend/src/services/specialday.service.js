import httpClient from "../http-common";

const getAllSpecialDays = () => {
    return httpClient.get('/api/v1/specialdays/');
};

const createSpecialDay = (specialDay) => {
    return httpClient.post('/api/v1/specialdays/', specialDay);
};

const getSpecialDayById = (id) => {
    return httpClient.get(`/api/v1/specialdays/${id}`);
};

const deleteSpecialDayById = (id) => {
    return httpClient.delete(`/api/v1/specialdays/${id}`);
};

export default {
    getAllSpecialDays,
    createSpecialDay,
    getSpecialDayById,
    deleteSpecialDayById,
};