<template>
  <div class="common-layout h-screen">
    <el-container class="h-full">
      
      <el-header height="60px" class="flex items-center border-b border-gray-300 dark:border-gray-600 transition-colors duration-500">
          <Header></Header>
        </el-header>
      <el-container>
        <el-aside class="w-auto border-r border-gray-300 dark:border-gray-600 transition-colors duration-500">
          <Menu></Menu>
        </el-aside>
        <el-main>
          <Breadcrumb></Breadcrumb>
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import Menu from '@/components/Menu.vue'
import Header from '@/components/Header.vue'
import Breadcrumb from '@/components/Breadcrumb.vue'



</script>

<style scoped type="scss">



</style>