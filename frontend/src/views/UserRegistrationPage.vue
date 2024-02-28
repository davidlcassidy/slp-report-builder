<template>
  <div class="form-container">
    <h1>Register</h1>
    <form @submit.prevent="register" class="form">
      <div class="form-group">
        <label for="username">Username</label>
        <input type="text" id="username" v-model="username" placeholder="Enter your username" required />
      </div>
      <div class="form-group">
        <label for="password">Password</label>
        <input type="password" id="password" v-model="password" placeholder="Enter your password" required />
      </div>
      <div class="form-group">
        <label for="firstName">First Name</label>
        <input type="text" id="firstName" v-model="firstName" placeholder="Enter your first name" required />
      </div>
      <div class="form-group">
        <label for="lastName">Last Name</label>
        <input type="text" id="lastName" v-model="lastName" placeholder="Enter your last name" required />
      </div>
      <button type="submit" class="btn-register">Register</button>
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
      firstName: '',
      lastName: '',
      errorMessage: ''
    };
  },
  methods: {
    async register() {
      try {
        const response = await axios.post('/api/v1/user/registration', {
          username: this.username,
          password: this.password,
          firstName: this.firstName,
          lastName: this.lastName
        });
        if (response.status === 201) {
          this.$router.push('/login');
        } else {
          this.errorMessage = 'Registration failed. Please try again later.';
        }
      } catch (error) {
        if (error.response && error.response.status === 400) {
          this.errorMessage = 'User already exists';
        } else {
          this.errorMessage = 'Failed to register. Please try again later.';
          console.error('Registration error:', error);
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
  border: 1px solid #ccc;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

h1 {
  text-align: center;
  margin-bottom: 2rem;
}

.form-group {
  margin-bottom: 1.5rem;
}

label {
  display: block;
  font-weight: bold;
}

input[type="text"],
input[type="password"] {
  width: 100%;
  padding: 0.5rem;
  font-size: 1rem;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.btn-register {
  width: 100%;
  padding: 0.75rem;
  font-size: 1rem;
  background-color: #007bff;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.btn-register:hover {
  background-color: #0056b3;
}

.error-message {
  color: red;
  margin-top: 1rem;
}
</style>
