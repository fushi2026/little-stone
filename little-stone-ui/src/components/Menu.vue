<template>
  <div class="relative">
    <div 
      class="logo-title-container h-[60px] flex items-center justify-center transition-all duration-300"
      @click="$router.push('/')"
    >
      <img src="@/assets/ico.svg" class="h-8 w-8" />
      <span v-if="!isCollapse" class="logo-title flex items-center ml-2">小石头在线</span>
    </div>
    <el-menu
      default-active="2"
      class="el-menu-vertical-demo dark:text-gray-300 border-r-0 mx-auto bg-transparent transition-all duration-300"
      :collapse="isCollapse"
      @open="handleOpen"
      @close="handleClose"
    >
      <el-menu-item index="1" class="text-gray-600 dark:text-gray-300 hover:bg-green-300 dark:hover:bg-green-900">
        <el-icon><icon-menu /></el-icon>
        <template #title>销售管理</template>
      </el-menu-item>
      <el-menu-item index="2" class="text-gray-600 dark:text-gray-300 hover:bg-green-300 dark:hover:bg-green-900">
        <el-icon><ShoppingCart /></el-icon>
        <template #title>采购管理</template>
      </el-menu-item>
      <el-menu-item index="3" class="text-gray-600 dark:text-gray-300 hover:bg-green-300 dark:hover:bg-green-900">
        <el-icon><Box /></el-icon>
        <template #title>库存管理</template>
      </el-menu-item>
      <el-menu-item index="4" class="text-gray-600 dark:text-gray-300 hover:bg-green-300 dark:hover:bg-green-900">
        <el-icon><Monitor /></el-icon>
        <template #title>项目管理</template>
      </el-menu-item>
      <el-menu-item index="5" class="text-gray-600 dark:text-gray-300 hover:bg-green-300 dark:hover:bg-green-900">
        <el-icon><Files /></el-icon>
        <template #title>合同管理</template>
      </el-menu-item>
    </el-menu>
  </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import {
  Menu as IconMenu,
} from '@element-plus/icons-vue'

let isCollapse = ref(false)

const handleOpen = (key: string, keyPath: string[]) => {
  console.log(key, keyPath)
}
const handleClose = (key: string, keyPath: string[]) => {
  console.log(key, keyPath)
}
</script>

<style type="scss" scoped>
.logo-title-container {
  cursor: pointer;
  .logo-title {
    background: -webkit-linear-gradient(315deg, #42d392 25%, #647eff);
    background-clip: text;
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    letter-spacing: 3px;
    font-family: "PingFang SC", sans-serif;
    font-size: 20px;
    font-weight: 600;
    text-rendering: optimizeLegibility;
  }
}

.el-menu-vertical-demo:not(.el-menu--collapse) {
  width: 200px;
  min-height: 400px;
}
.is-active {
  color: #42d392 !important;
}
</style>
