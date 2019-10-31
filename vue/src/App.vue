<template>
  <div id="app">
    <router-view></router-view>
  </div>
</template>

<script>
export default {
  name: "app"
};
</script>

<style lang="less">
#app,
html,
body {
  font-family: "Avenir", Helvetica, Arial, sans-serif;
  color: #ffffff;
  min-width: 100%;
  min-height: 100%;
  background-color: #383838;
}
</style>
