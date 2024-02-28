import { createStore } from 'vuex';
import router from '../router';

export default createStore({
  state: {
    user: null
  },
  mutations: {
    login(state, user) {
      state.user = user
    },
    logout(state) {
      state.user = null
    }
  },
  actions: {
    login({ commit }, user) {
      commit('login', user)
    },
    logout({ commit }) {
      commit('logout')
      router.push('/')
    }
  },
  getters: {
    isAuthenticated: state => state.user !== null,
    getUser: state => state.user
  }
});
