<template>
  <div class="topbar">
    <div class="brand">SLP Report Builder</div>
    <div class="user-menu" v-if="isAuthenticated">
      <div class="username" @click="toggleDropdown()">{{ username }}</div>
      <div class="dropdown" v-show="showDropdown" @click.stop>
        <button @click="goToSettings">Settings</button>
        <button @click="logout">Logout</button>
      </div>
    </div>
    <div class="auth-links" v-else>
      <router-link to="/login" class="auth-link">Login</router-link>
      <router-link to="/registration" class="auth-link">Register</router-link>
    </div>
  </div>
</template>

<script>
export default {
  computed: {
    isAuthenticated() {
      return this.$store.getters.isAuthenticated;
    },
    username() {
      const user = this.$store.getters.getUser;
      if (user) {
        return `${user.firstName} ${user.lastName}`;
      } else {
        return '';
      }
    }
  },
  data() {
    return {
      showDropdown: false
    };
  },
  methods: {
    toggleDropdown() {
      this.showDropdown = !this.showDropdown;
    },
    goToSettings() {
      // Implement navigation to settings page
    },
    logout() {
      this.$store.dispatch('logout');
    }
  }
};
</script>

<style scoped>
.topbar {
  background-color: #007bff;
  color: #fff;
  padding: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.brand {
  font-size: 1.5rem;
}

.user-menu {
  position: relative;
}

.username {
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 4px;
  background-color: #0056b3;
}

.username:hover {
  background-color: #00438a;
}

.dropdown {
  position: absolute;
  top: calc(100% + 5px);
  right: 0;
  background-color: #fff;
  border: 1px solid #ccc;
  padding: 5px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.dropdown button {
  display: block;
  width: 100%;
  text-align: left;
  padding: 5px 10px;
  margin: 5px 0;
  background-color: transparent;
  border: none;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.dropdown button:hover {
  background-color: #f0f0f0;
}

.auth-links {
  display: flex;
}

.auth-link {
  color: #fff;
  text-decoration: none;
  padding: 8px 12px;
  margin-left: 10px;
  border-radius: 4px;
  background-color: #0056b3;
}

.auth-link:hover {
  background-color: #00438a;
}
</style>
