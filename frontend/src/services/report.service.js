import httpClient from "../http-common";

const API_URL = '/api/reportes';

const generateTariffReport = (startDate, endDate) => {
    return httpClient.get(`${API_URL}/report/tariff`, {
        params: { startDate, endDate },
        responseType: 'blob',
    });
};

const generateGroupSizeReport = (startDate, endDate) => {
    return httpClient.get(`${API_URL}/report/group-size`, {
        params: { startDate, endDate },
        responseType: 'blob',
    });
};

export default {
    generateTariffReport,
    generateGroupSizeReport,
};