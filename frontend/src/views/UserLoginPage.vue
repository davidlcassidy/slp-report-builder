<template>
  <div class="form-container">
    <h1>Login</h1>
    <form @submit.prevent="login">
      <div class="input-group">
        <input type="text" v-model="username" placeholder="Username" />
      </div>
      <div class="input-group">
        <input type="password" v-model="password" placeholder="Password" />
      </div>
      <button type="submit" class="btn-primary">Login</button>
    </form>
    <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
  </div>
</template>

<script>
import axios from '../axios-config';

export default {
  data() {
    return {
      username: '',
      password: '',
      errorMessage: ''
    };
  },
  methods: {
    async login() {
      try {
        const response = await axios.post('/api/v1/user/login', {
          username: this.username,
          password: this.password
        });
        const user = response.data;
        this.$store.commit('login', user);
        this.$router.push('/reports');
      } catch (error) {
        if (error.response && error.response.status === 400) {
          this.errorMessage = 'Invalid username or password';
        } else {
          console.error('Login error:', error);
          this.errorMessage = 'Failed to login. Please try again later.';
        }
      }
    }
  }
};
</script>

<style scoped>
.form-container {
  max-width: 400px;
  margin: 0 auto;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  background-color: #f9f9f9;
}

h1 {
  text-align: center;
  margin-bottom: 2rem;
  color: #333;
}

.input-group {
  margin-bottom: 1.5rem;
}

input[type="text"],
input[type="password"] {
  width: 100%;
  padding: 0.75rem;
  font-size: 1rem;
  border: 1px solid #ccc;
  border-radius: 4px;
}

button.btn-primary {
  width: 100%;
  padding: 0.75rem;
  font-size: 1rem;
  border: none;
  border-radius: 4px;
  background-color: #007bff;
  color: #fff;
  cursor: pointer;
}

button.btn-primary:hover {
  background-color: #0056b3;
}

.error-message {
  color: red;
  margin-top: 1rem;
  text-align: center;
}
</style>
