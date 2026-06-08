<template>
  <div class="relative"> 
    <el-menu
      :default-active="activeMenu"
      class="el-menu-vertical-demo dark:text-gray-300 border-r-0 mx-auto bg-transparent transition-all duration-300"
      :collapse="isCollapse"
      router
      @open="handleOpen"
      @close="handleClose"
    >
      <template v-for="menu in menuList" :key="menu.id">
        <!-- 有子菜单的情况 -->
        <el-sub-menu v-if="menu.children && menu.children.length > 0" :index="menu.path">
          <template #title>
            <el-icon v-if="menu.icon"><component :is="menu.icon" /></el-icon>
            <el-icon v-else><Menu /></el-icon>
            <span>{{ menu.menuName }}</span>
          </template>
          <template v-for="child in menu.children" :key="child.id">
            <el-menu-item 
              v-if="child.component" 
              :index="`${menu.path}${child.path}`"
            >
              <el-icon v-if="child.icon"><component :is="child.icon" /></el-icon>
              <span>{{ child.menuName }}</span>
            </el-menu-item>
          </template>
        </el-sub-menu>
        <!-- 没有子菜单的情况 -->
        <el-menu-item 
          v-else-if="menu.component" 
          :index="`${menu.path}`"
        >
          <el-icon v-if="menu.icon"><component :is="menu.icon" /></el-icon>
          <el-icon v-else><Menu /></el-icon>
          <template #title>{{ menu.menuName }}</template>
        </el-menu-item>
      </template>
    </el-menu>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'

import { router } from '@/router'

const route = useRoute()
const userStore = useUserStore()
const isCollapse = ref(false)

// 动态菜单项
const menuList = computed(() => {
  return userStore.menuList || []
})

// 当前激活的菜单
const activeMenu = computed(() => {
  return route.path
})

onMounted(() => {
  console.log('menuList', userStore.menuList)
  
  console.log('router', router.getRoutes())
})

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