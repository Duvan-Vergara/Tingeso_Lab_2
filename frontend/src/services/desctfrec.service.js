import httpClient from "../http-common";

const API_URL = '/api/desctfrecu/';

const getAllDesctFrecu = () => httpClient.get(API_URL);

const createDesctFrecu = (desct) => httpClient.post(`${API_URL}crear`, desct);

const getDescuentoByVeces = (veces) =>
  httpClient.post(`${API_URL}obtener-descuento`, { veces });

export default {
  getAllDesctFrecu,
  createDesctFrecu,
  getDescuentoByVeces,
};