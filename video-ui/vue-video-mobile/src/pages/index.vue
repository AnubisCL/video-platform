<script setup lang="ts">

import Login from "./login.vue";

defineOptions({
  name: 'index'
})
const data = reactive({
  userSignIn: {
    username: '',
    password: '',
    email: '',
    signType: 'signIn'
  },
  userLoginIn: {
    username: '',
    password: '',
    email: '',
    signType: 'loginIn'
  }
})

const passwordHash = computed(() => {
  return data.userForm.password
})

const store = useBaseStore()


onMounted(() => {
  console.log('onMounted')
})

onCreated(() => {
  console.log('onCreated')
})

onDestroyed(() => {
  console.log('onDestroyed')
})

function signIn() {}
function loginIn() {}

</script>

<template>
  <div>
    <button @click="signIn">signIn</button>
    <button @click="loginIn">signIn</button>
  </div>

</template>

<style scoped>

</style>
