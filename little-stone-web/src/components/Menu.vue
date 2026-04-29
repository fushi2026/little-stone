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
            <span>{{ menu.permName }}</span>
          </template>
          <template v-for="child in menu.children" :key="child.id">
            <el-menu-item 
              v-if="child.permType === 1" 
              :index="child.path.startsWith('/api') ? child.path.replace('/api', '') : child.path"
            >
              <el-icon v-if="child.icon"><component :is="child.icon" /></el-icon>
              <span>{{ child.permName }}</span>
            </el-menu-item>
          </template>
        </el-sub-menu>
        <!-- 没有子菜单的情况 -->
        <el-menu-item 
          v-else-if="menu.permType === 1" 
          :index="menu.path.startsWith('/api') ? menu.path.replace('/api', '') : menu.path"
        >
          <el-icon v-if="menu.icon"><component :is="menu.icon" /></el-icon>
          <el-icon v-else><Menu /></el-icon>
          <template #title>{{ menu.permName }}</template>
        </el-menu-item>
      </template>
    </el-menu>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const userStore = useUserStore()
const isCollapse = ref(false)

// 动态菜单项
const menuList = computed(() => {
  return userStore.menuTree || []
})

// 当前激活的菜单
const activeMenu = computed(() => {
  return route.path
})

onMounted(() => {
  console.log(userStore.menuTree)
  console.log(userStore.userInfo)
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