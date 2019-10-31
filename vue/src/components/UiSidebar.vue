<template>
  <div class="ui-sidebar">
    <a href="https://github.com">Github</a>
    <a href="https://github.com">Github</a>
    <a href="https://github.com">Github</a>
    <router-link to="/guild">Guilds</router-link>
    <router-link v-for="guild of guilds" :key="guild.id" :to="`/guild/${guild.id}`">{{ guild.name }}</router-link>
  </div>
</template>

<script>
export default {
  name: "ui-sidebar",
  data() {
    return {
      guilds: [
        { name: "guild1", id: 4345623945 },
        { name: "guild2", id: 2345253455 },
        { name: "guild3", id: 2342528955 }
      ]
    };
  }
};
</script>

<style langs="less" scoped>
.ui-sidebar {
  background-color: #232323;
  float: left;
  top: 0;
  left: 0;
  height: 100%;
  width: 20em;
  display: flex;
  flex-direction: column;
}
</style>
