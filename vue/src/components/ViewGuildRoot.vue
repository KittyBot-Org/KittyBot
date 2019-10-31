<template>
  <div class="view-guild-root">
    <ui-sidebar />
    <router-view></router-view>
  </div>
</template>

<script>
import UiSidebar from "./UiSidebar";

export default {
  name: "view-guild-root",
  components: {
    UiSidebar
  }
};
</script>

<style lang="less" scoped>
.view-guild-root {
}
</style>
