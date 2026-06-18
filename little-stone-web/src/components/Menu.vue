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
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'

import { router } from '@/router'

const route = useRoute()
const userStore = useUserStore()
const isCollapse = ref(false)

// 动态菜单项 - 根据选中的模块过滤
const menuList = computed(() => {
  const allMenus = userStore.menuList || []
  const currentModuleCode = userStore.selectedModuleCode
  return allMenus.filter(menu => menu.moduleCode === currentModuleCode)
})

// 当前激活的菜单
const activeMenu = computed(() => {
  return route.path
})

onMounted(() => {

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

/* ========== 浅色模式 ========== */
.el-menu-vertical-demo {
  --menu-text-color: #4b5563;
  --menu-hover-bg: #f3f4f6;
  --menu-hover-text: #1f2937;
  --menu-active-text: #42d392;
  --menu-bg: transparent;
  --submenu-bg: transparent;
}

/* ========== 暗黑模式 ========== */
.dark .el-menu-vertical-demo {
  --menu-text-color: #d1d5db;
  --menu-hover-bg: rgba(255, 255, 255, 0.06);
  --menu-hover-text: #f3f4f6;
  --menu-active-text: #42d392;
  --menu-bg: transparent;
  --submenu-bg: rgba(0, 0, 0, 0.15);
}

/* 菜单项基础样式 */
:deep(.el-menu-item) {
  color: var(--menu-text-color) !important;
  background: var(--menu-bg) !important;
  transition: all 0.2s ease;
}

:deep(.el-menu-item:hover) {
  color: var(--menu-hover-text) !important;
  background: var(--menu-hover-bg) !important;
}

:deep(.el-menu-item.is-active) {
  color: var(--menu-active-text) !important;
  background: rgba(66, 211, 146, 0.1) !important;
  font-weight: 600;
}

/* 子菜单标题 */
:deep(.el-sub-menu__title) {
  color: var(--menu-text-color) !important;
  background: var(--menu-bg) !important;
  transition: all 0.2s ease;
}

:deep(.el-sub-menu__title:hover) {
  color: var(--menu-hover-text) !important;
  background: var(--menu-hover-bg) !important;
}

/* 子菜单展开后的背景 */
:deep(.el-menu--inline) {
  background: var(--submenu-bg) !important;
}

/* 子菜单中的菜单项 */
:deep(.el-menu--inline .el-menu-item) {
  padding-left: 56px !important;
}

/* 暗黑模式下子菜单展开图标颜色 */
.dark :deep(.el-sub-menu__icon-arrow) {
  color: #9ca3af;
}

/* 暗黑模式下的边框 */
.dark :deep(.el-menu) {
  border-right: none;
}
</style>