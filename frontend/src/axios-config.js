import axios from 'axios';
import store from './store';

const instance = axios.create({
  baseURL: 'http://localhost:8080'
});

instance.interceptors.request.use(
  function(config) {
    config.headers.AuthenticationToken = store.getters.getUser?.authenticationToken;
    return config;
  },
  function(error) {
    return Promise.reject(error);
  }
);

instance.interceptors.response.use(
    function(response) {
      return response;
    },
    function(error) {
      if (error.response && error.response.status === 401) {
        store.dispatch('logout');
      }
      return Promise.reject(error);
    }
  );

export default instance;
