<template>
  <div>

  </div>
</template>

<script lang="ts" setup>
  import { getUserInfo } from '@/api/user'
  import { onMounted } from 'vue'

  onMounted(() => {
    getUserInfo().then(res => {
      console.log(res)
    })
  })
</script>

<style scoped>

</style>
