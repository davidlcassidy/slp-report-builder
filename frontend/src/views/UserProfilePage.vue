<template>
  <div class="update-profile">
    <h1>Update User Profile</h1>
    <form @submit.prevent="updateProfile">
      <div class="form-group">
        <label for="username">Username</label>
        <input type="text" v-model="username" id="username">
      </div>
      <div class="form-group">
        <label for="password">Password</label>
        <input type="password" v-model="password" id="password">
      </div>
      <div class="form-group">
        <label for="firstName">First Name</label>
        <input type="text" v-model="firstName" id="firstName">
      </div>
      <div class="form-group">
        <label for="lastName">Last Name</label>
        <input type="text" v-model="lastName" id="lastName">
      </div>
      <div class="form-group">
        <label for="position">Position</label>
        <input type="text" v-model="position" id="position">
      </div>
      <button type="submit">Update Profile</button>
      <button type="button" @click="deleteUser">Delete User</button>
    </form>
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
      position: ''
    };
  },
  methods: {
    async updateProfile() {
      try {
        const response = await axios.post('/api/v1/user/update', {
          username: this.username,
          password: this.password,
          firstName: this.firstName,
          lastName: this.lastName,
          position: this.position
        });
        const user = response.data;
        this.$store.commit('login', user);
        this.$router.push('/dashboard');
      } catch (error) {
        console.error('Error updating profile:', error.response.data);
      }
    },
    async deleteUser() {
      try {
        await axios.delete('/api/v1/user');
        this.$store.commit('logout');
        this.$router.push('/');
      } catch (error) {
        console.error('Error deleting user:', error.response.data);
      }
    }
  }
};
</script>


<style scoped>
.update-profile {
  max-width: 400px;
  margin: auto;
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  margin-bottom: 5px;
}

input {
  width: 100%;
  padding: 8px;
  font-size: 16px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

button {
  padding: 10px 20px;
  font-size: 16px;
  border: none;
  border-radius: 4px;
  background-color: #007bff;
  color: #fff;
  cursor: pointer;
}

button:hover {
  background-color: #0056b3;
}
</style>
