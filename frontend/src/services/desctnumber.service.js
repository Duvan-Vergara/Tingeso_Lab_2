import httpClient from "../http-common";

const API_URL = '/api/desctnumber/';

const getAllDesctNumbers = () => httpClient.get(API_URL);

const createDesctNumber = (desct) => httpClient.post(`${API_URL}crear`, desct);

const getDescuentoByPersonas = (personas) =>
  httpClient.post(`${API_URL}obtener-descuento`, { personas });

export default {
  getAllDesctNumbers,
  createDesctNumber,
  getDescuentoByPersonas,
};