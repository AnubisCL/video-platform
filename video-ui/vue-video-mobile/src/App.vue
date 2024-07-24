<script setup lang="ts">
export default {
  setup() {
    const theme = ref('light');
    setTimeout(() => {
      theme.value = 'dark';
    }, 1000);

    return { theme };
  },
};

</script>

<template>
  <van-config-provider theme="dark">
    <RouterView/>
  </van-config-provider>
</template>

<style scoped>
</style>
