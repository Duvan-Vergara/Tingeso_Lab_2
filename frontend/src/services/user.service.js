import httpClient from "../http-common";

const API_URL = '/api/reserves/users/';

const getAllUsers = () => {
    return httpClient.get(API_URL);
};

const saveUser = (data) => {
    return httpClient.post(API_URL, data);
};

const getUserById = (id) => {
    return httpClient.get(`${API_URL}${id}`);
};

const removeUser = (id) => {
    return httpClient.delete(`${API_URL}${id}`);
};

export default { getAllUsers, saveUser, getUserById, removeUser };