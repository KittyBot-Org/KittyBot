<template>
  <div class="view-guild">
    <h1>Guild {{ guildId }}</h1>
  </div>
</template>

<script>
export default {
  name: "view-guild",
  data() {
    return {
      guildId: this.$route.params.id
    };
  }
};
</script>

<style scoped>
</style>
