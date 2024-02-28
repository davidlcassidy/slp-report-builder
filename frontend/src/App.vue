<template>
  <div id="app">
    <Topbar />
    <div class="content-wrapper">
      <router-view />
    </div>
  </div>
</template>

<script>
import Topbar from './components/GlobalTopbar.vue';

export default {
  components: {
    Topbar
  }
};
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
}

.content-wrapper {
  padding-top: 50px;
}
</style>
