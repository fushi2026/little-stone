<template>
  <component :is="iconComponent" v-if="iconComponent" />
  <span v-else class="icon-placeholder">?</span>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

const props = defineProps<{
  icon: string
}>()

const iconComponent = computed(() => {
  if (!props.icon) return null
  
  // 尝试多种命名格式
  const iconName = props.icon
  const possibleNames = [
    iconName,                              // 原名称
    iconName.replace(/-/g, ''),            // 移除连字符
    iconName.replace(/-/g, ' ').replace(/\s+/g, (_, i) => 
      i === 0 ? _.toLowerCase() : _.toUpperCase()
    ).replace(/\s/g, ''),                  // kebab-case -> PascalCase
    iconName.charAt(0).toUpperCase() + iconName.slice(1) // 首字母大写
  ]
  
  for (const name of possibleNames) {
    if (ElementPlusIconsVue[name as keyof typeof ElementPlusIconsVue]) {
      return ElementPlusIconsVue[name as keyof typeof ElementPlusIconsVue]
    }
  }
  
  console.warn(`Icon "${props.icon}" not found in Element Plus icons`)
  return null
})
</script>

<style scoped>
.icon-placeholder {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
  background: #f5f5f5;
  border-radius: 4px;
  font-size: 12px;
  color: #999;
}
</style>
