<template>
  <div class="dashboard-container">
    <!-- 欢迎区域 -->
    <div class="welcome-section">
      <div class="welcome-content">
        <div class="welcome-text">
          <h1 class="welcome-title">
            {{ greeting }}，{{ userStore.userInfo?.nickname || userStore.userInfo?.username || '用户' }}
          </h1>
          <p class="welcome-subtitle">欢迎回到小石头在线，祝您工作愉快！</p>
        </div>
        <div class="welcome-decoration">
          <div class="decoration-circle"></div>
          <div class="decoration-circle small"></div>
        </div>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div
        v-for="stat in statsData"
        :key="stat.label"
        class="stat-card"
        :style="{ '--card-color': stat.color }"
      >
        <div class="stat-card-header">
          <div class="stat-icon-wrapper" :style="{ background: stat.bgColor }">
            <el-icon :size="28" :color="stat.color">
              <component :is="stat.icon" />
            </el-icon>
          </div>
          <div class="stat-trend" v-if="stat.trend">
            <span :class="stat.trend > 0 ? 'trend-up' : 'trend-down'">
              <el-icon>
                <component :is="stat.trend > 0 ? Top : Bottom" />
              </el-icon>
              {{ Math.abs(stat.trend) }}%
            </span>
            <span class="trend-text">较上月</span>
          </div>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ stat.value }}</span>
          <span class="stat-label">{{ stat.label }}</span>
        </div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="charts-grid">
      <!-- 项目趋势图 -->
      <div class="chart-card large">
        <div class="chart-header">
          <h3 class="chart-title">项目趋势</h3>
          <el-radio-group v-model="trendPeriod" size="small">
            <el-radio-button label="week">本周</el-radio-button>
            <el-radio-button label="month">本月</el-radio-button>
            <el-radio-button label="year">本年</el-radio-button>
          </el-radio-group>
        </div>
        <v-chart 
          :option="trendChartOption" 
          class="chart" 
          :autoresize="true"
          ref="trendChartRef"
        />
      </div>

      <!-- 项目分布 -->
      <div class="chart-card">
        <div class="chart-header">
          <h3 class="chart-title">项目分布</h3>
        </div>
        <v-chart 
          :option="distributionChartOption" 
          class="chart pie-chart"
          :autoresize="true"
          ref="pieChartRef"
        />
      </div>
    </div>

    <!-- 底部区域 -->
    <div class="bottom-grid">
      <!-- 快捷入口 -->
      <div class="quick-actions-card">
        <h3 class="card-title">
          <el-icon><Promotion /></el-icon>
          <span>快捷入口</span>
        </h3>
        <div class="quick-actions-grid">
          <div
            v-for="action in quickActions"
            :key="action.label"
            class="quick-action-item"
            @click="handleQuickAction(action)"
          >
            <div class="action-icon" :style="{ background: action.bgColor }">
              <el-icon :size="24" :color="action.color">
                <component :is="action.icon" />
              </el-icon>
            </div>
            <span class="action-label">{{ action.label }}</span>
          </div>
        </div>
      </div>

      <!-- 最近动态 -->
      <div class="recent-activity-card">
        <h3 class="card-title">
          <el-icon><Clock /></el-icon>
          <span>最近动态</span>
        </h3>
        <div class="activity-list">
          <div
            v-for="(activity, index) in recentActivities"
            :key="index"
            class="activity-item"
          >
            <div class="activity-icon" :style="{ background: activity.bgColor }">
              <el-icon :size="16" :color="activity.color">
                <component :is="activity.icon" />
              </el-icon>
            </div>
            <div class="activity-content">
              <p class="activity-title">{{ activity.title }}</p>
              <span class="activity-time">{{ activity.time }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 待办事项 -->
      <div class="todo-card">
        <h3 class="card-title">
          <el-icon><List /></el-icon>
          <span>待办事项</span>
          <el-tag type="danger" size="small">{{ todoList.filter(t => !t.done).length }}</el-tag>
        </h3>
        <div class="todo-list">
          <div
            v-for="(todo, index) in todoList"
            :key="index"
            class="todo-item"
            :class="{ 'todo-done': todo.done }"
          >
            <el-checkbox v-model="todo.done" />
            <span class="todo-text">{{ todo.text }}</span>
            <el-tag v-if="todo.priority === 'high'" type="danger" size="small">紧急</el-tag>
            <el-tag v-else-if="todo.priority === 'medium'" type="warning" size="small">重要</el-tag>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, PieChart, BarChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
} from 'echarts/components'
import {
  Document,
  User,
  DataAnalysis,
  TrendCharts,
  Plus,
  Search,
  Setting,
  List,
  Clock,
  Promotion,
  Top,
  Bottom,
  Edit,
  ChatDotRound,
  Warning,
  Check,
} from '@element-plus/icons-vue'

use([
  CanvasRenderer,
  LineChart,
  PieChart,
  BarChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
])

const router = useRouter()
const userStore = useUserStore()
const trendChartRef = ref()
const pieChartRef = ref()
const isDark = ref(document.documentElement.classList.contains('dark'))

// 问候语
const greeting = computed(() => {
  const hour = new Date().getHours()
  if (hour < 6) return '夜深了'
  if (hour < 9) return '早上好'
  if (hour < 12) return '上午好'
  if (hour < 14) return '中午好'
  if (hour < 18) return '下午好'
  return '晚上好'
})

// 统计周期
const trendPeriod = ref('month')

// 统计数据
const statsData = ref([
  {
    label: '项目总数',
    value: 128,
    icon: Document,
    color: '#3b82f6',
    bgColor: 'rgba(59, 130, 246, 0.1)',
    trend: 12,
  },
  {
    label: '活跃用户',
    value: 56,
    icon: User,
    color: '#10b981',
    bgColor: 'rgba(16, 185, 129, 0.1)',
    trend: 8,
  },
  {
    label: '本月新增',
    value: 23,
    icon: TrendCharts,
    color: '#f59e0b',
    bgColor: 'rgba(245, 158, 11, 0.1)',
    trend: -3,
  },
  {
    label: '完成率',
    value: '86.5%',
    icon: DataAnalysis,
    color: '#8b5cf6',
    bgColor: 'rgba(139, 92, 246, 0.1)',
    trend: 5,
  },
])

// 项目趋势图配置
const trendChartOption = computed(() => {
  const weekData = {
    xAxis: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
    series1: [12, 19, 15, 25, 18, 8, 5],
    series2: [8, 12, 10, 18, 12, 5, 3],
  }
  const monthData = {
    xAxis: ['第1周', '第2周', '第3周', '第4周'],
    series1: [45, 52, 48, 62],
    series2: [28, 35, 32, 42],
  }
  const yearData = {
    xAxis: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'],
    series1: [120, 132, 101, 134, 90, 230, 210, 182, 191, 234, 290, 330],
    series2: [80, 92, 71, 104, 70, 160, 140, 122, 131, 154, 190, 210],
  }

  const data = trendPeriod.value === 'week' ? weekData : 
               trendPeriod.value === 'month' ? monthData : yearData

  const bgColor = isDark.value ? '#1f2937' : '#fff'
  const textColor = isDark.value ? '#e5e7eb' : '#374151'
  const subTextColor = isDark.value ? '#9ca3af' : '#6b7280'
  const borderColor = isDark.value ? '#374151' : '#e5e7eb'
  const splitLineColor = isDark.value ? '#374151' : '#f3f4f6'

  return {
    backgroundColor: bgColor,
    tooltip: {
      trigger: 'axis',
      backgroundColor: bgColor,
      borderColor: borderColor,
      textStyle: { color: textColor },
    },
    legend: {
      data: ['新增项目', '完成项目'],
      top: 0,
      textStyle: { color: subTextColor },
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '60px',
      containLabel: true,
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: data.xAxis,
      axisLine: { lineStyle: { color: borderColor } },
      axisLabel: { color: subTextColor },
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      splitLine: { lineStyle: { color: splitLineColor } },
      axisLabel: { color: subTextColor },
    },
    series: [
      {
        name: '新增项目',
        type: 'line',
        stack: 'Total',
        data: data.series1,
        smooth: true,
        itemStyle: { color: '#42d392' },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(66, 211, 146, 0.3)' },
              { offset: 1, color: 'rgba(66, 211, 146, 0.05)' },
            ],
          },
        },
      },
      {
        name: '完成项目',
        type: 'line',
        stack: 'Total',
        data: data.series2,
        smooth: true,
        itemStyle: { color: '#647eff' },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(100, 126, 255, 0.3)' },
              { offset: 1, color: 'rgba(100, 126, 255, 0.05)' },
            ],
          },
        },
      },
    ],
  }
})

// 项目分布饼图
const distributionChartOption = computed(() => {
  const bgColor = isDark.value ? '#1f2937' : '#fff'
  const textColor = isDark.value ? '#e5e7eb' : '#374151'
  const subTextColor = isDark.value ? '#9ca3af' : '#6b7280'
  const borderColor = isDark.value ? '#374151' : '#e5e7eb'

  return {
    backgroundColor: bgColor,
    tooltip: {
      trigger: 'item',
      backgroundColor: bgColor,
      borderColor: borderColor,
      textStyle: { color: textColor },
    },
    legend: {
      orient: 'vertical',
      right: '5%',
      top: 'center',
      textStyle: { color: subTextColor },
    },
    series: [
      {
        name: '项目状态',
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['35%', '50%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 8,
          borderColor: bgColor,
          borderWidth: 2,
        },
        label: {
          show: false,
          position: 'center',
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 16,
            fontWeight: 'bold',
            color: textColor,
          },
        },
        labelLine: {
          show: false,
        },
        data: [
          { value: 48, name: '进行中', itemStyle: { color: '#42d392' } },
          { value: 25, name: '待立项', itemStyle: { color: '#647eff' } },
          { value: 18, name: '已完成', itemStyle: { color: '#9ca3af' } },
          { value: 12, name: '已暂停', itemStyle: { color: '#f59e0b' } },
          { value: 8, name: '已取消', itemStyle: { color: '#ef4444' } },
        ],
      },
    ],
  }
})

// 快捷入口
const quickActions = [
  { label: '新建项目', icon: Plus, color: '#3b82f6', bgColor: 'rgba(59, 130, 246, 0.1)', path: '/project' },
  { label: '项目列表', icon: List, color: '#10b981', bgColor: 'rgba(16, 185, 129, 0.1)', path: '/project' },
  { label: '搜索项目', icon: Search, color: '#f59e0b', bgColor: 'rgba(245, 158, 11, 0.1)', path: '/project' },
  { label: '系统设置', icon: Setting, color: '#8b5cf6', bgColor: 'rgba(139, 92, 246, 0.1)', path: '/settings' },
]

// 最近动态
const recentActivities = ref([
  { title: '管理员更新了系统配置', time: '10 分钟前', color: '#3b82f6', bgColor: 'rgba(59, 130, 246, 0.1)', icon: Setting },
  { title: '项目 "小石头一期" 已完成立项', time: '1 小时前', color: '#10b981', bgColor: 'rgba(16, 185, 129, 0.1)', icon: Check },
  { title: '新用户 "张三" 注册了账号', time: '3 小时前', color: '#f59e0b', bgColor: 'rgba(245, 158, 11, 0.1)', icon: User },
  { title: '项目 "数据中心建设" 需要评审', time: '昨天', color: '#8b5cf6', bgColor: 'rgba(139, 92, 246, 0.1)', icon: Warning },
  { title: '收到新的评论消息', time: '昨天', color: '#ec4899', bgColor: 'rgba(236, 72, 153, 0.1)', icon: ChatDotRound },
])

// 待办事项
const todoList = ref([
  { text: '评审 "小石头一期" 项目方案', done: false, priority: 'high' },
  { text: '更新项目进度报告', done: false, priority: 'medium' },
  { text: '参加项目周会', done: true, priority: 'medium' },
  { text: '整理项目文档', done: false, priority: 'low' },
  { text: '回复客户邮件', done: true, priority: 'low' },
])

const handleQuickAction = (action: typeof quickActions[0]) => {
  router.push(action.path)
}

const resizeCharts = () => {
  nextTick(() => {
    trendChartRef.value?.resize()
    pieChartRef.value?.resize()
  })
}

let darkModeObserver: MutationObserver | null = null

watch(isDark, () => {
  resizeCharts()
})

onMounted(() => {
  resizeCharts()
  window.addEventListener('resize', resizeCharts)
  
  // 监听深色模式变化
  darkModeObserver = new MutationObserver(() => {
    isDark.value = document.documentElement.classList.contains('dark')
    resizeCharts()
  })
  darkModeObserver.observe(document.documentElement, {
    attributes: true,
    attributeFilter: ['class']
  })
})

onUnmounted(() => {
  window.removeEventListener('resize', resizeCharts)
  if (darkModeObserver) {
    darkModeObserver.disconnect()
  }
})
</script>

<style scoped>
.dashboard-container {
  padding: 0;
  max-width: 1600px;
  margin: 0 auto;
}

/* ========== 欢迎区域 ========== */
.welcome-section {
  background: linear-gradient(135deg, #42d392 0%, #647eff 100%);
  border-radius: 16px;
  padding: 32px 36px;
  margin-bottom: 24px;
  color: #fff;
  box-shadow: 0 4px 20px rgba(66, 211, 146, 0.25);
  position: relative;
  overflow: hidden;
}

.welcome-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
  z-index: 1;
}

.welcome-title {
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 8px 0;
  letter-spacing: 1px;
}

.welcome-subtitle {
  font-size: 14px;
  opacity: 0.9;
  margin: 0;
}

.welcome-decoration {
  position: relative;
}

.decoration-circle {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.15);
  position: absolute;
  right: 0;
  top: -20px;
}

.decoration-circle.small {
  width: 40px;
  height: 40px;
  right: 60px;
  top: 20px;
  background: rgba(255, 255, 255, 0.2);
}

/* ========== 统计卡片 ========== */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border: 1px solid #f0f0f0;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 4px;
  background: var(--card-color);
  opacity: 0.8;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.1);
}

.stat-card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.stat-icon-wrapper {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-info {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #1f2937;
  line-height: 1;
}

.stat-label {
  font-size: 14px;
  color: #9ca3af;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  padding: 4px 8px;
  background: rgba(16, 185, 129, 0.1);
  border-radius: 6px;
}

.stat-trend:has(.trend-down) {
  background: rgba(239, 68, 68, 0.1);
}

.trend-up {
  color: #10b981;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 2px;
}

.trend-down {
  color: #ef4444;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 2px;
}

.trend-text {
  color: #9ca3af;
  font-size: 12px;
}

/* ========== 图表区域 ========== */
.charts-grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 20px;
  margin-bottom: 24px;
}

.chart-card {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border: 1px solid #f0f0f0;
  display: flex;
  flex-direction: column;
}

.chart-card.large {
  min-height: 380px;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  flex-shrink: 0;
}

.chart-title {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
}

.chart {
  width: 100%;
  height: 100%;
  min-height: 280px;
  flex: 1;
}

.pie-chart {
  min-height: 260px;
}

/* ========== 底部网格 ========== */
.bottom-grid {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 20px;
}

.quick-actions-card,
.recent-activity-card,
.todo-card {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border: 1px solid #f0f0f0;
}

.card-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 20px 0;
  padding-bottom: 14px;
  border-bottom: 1px solid #f3f4f6;
}

.card-title .el-tag {
  margin-left: auto;
}

/* ========== 快捷入口 ========== */
.quick-actions-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.quick-action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 20px 12px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
  background: #f9fafb;
}

.quick-action-item:hover {
  background: #f3f4f6;
  transform: translateY(-2px);
}

.action-icon {
  width: 52px;
  height: 52px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.action-label {
  font-size: 14px;
  color: #6b7280;
  font-weight: 500;
}

/* ========== 最近动态 ========== */
.activity-list {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.activity-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 14px;
  border-bottom: 1px solid #f9fafb;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.activity-item:hover {
  background: #f9fafb;
}

.activity-item:last-child {
  border-bottom: none;
}

.activity-icon {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.activity-content {
  flex: 1;
  min-width: 0;
}

.activity-title {
  font-size: 14px;
  color: #374151;
  margin: 0 0 4px 0;
  line-height: 1.4;
}

.activity-time {
  font-size: 12px;
  color: #9ca3af;
}

/* ========== 待办事项 ========== */
.todo-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.todo-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.todo-item:hover {
  background: #f9fafb;
}

.todo-done {
  opacity: 0.6;
}

.todo-done .todo-text {
  text-decoration: line-through;
}

.todo-text {
  flex: 1;
  font-size: 14px;
  color: #374151;
}

/* ========== 深色模式 ========== */
.dark .stat-card,
.dark .chart-card,
.dark .quick-actions-card,
.dark .recent-activity-card,
.dark .todo-card {
  background: #1f2937;
  border-color: #374151;
}

.dark .stat-value {
  color: #f9fafb;
}

.dark .stat-label,
.dark .trend-text {
  color: #9ca3af;
}

.dark .chart-title {
  color: #f9fafb;
}

.dark .card-title {
  color: #f9fafb;
  border-bottom-color: #374151;
}

.dark .quick-action-item {
  background: #374151;
}

.dark .quick-action-item:hover {
  background: #4b5563;
}

.dark .action-label {
  color: #d1d5db;
}

.dark .activity-title,
.dark .todo-text {
  color: #e5e7eb;
}

.dark .activity-item {
  border-bottom-color: #374151;
}

.dark .activity-item:hover {
  background: #374151;
}

.dark .todo-item:hover {
  background: #374151;
}

/* ========== 响应式 ========== */
@media (max-width: 1400px) {
  .bottom-grid {
    grid-template-columns: 1fr 1fr;
  }
  
  .todo-card {
    grid-column: span 2;
  }
}

@media (max-width: 1200px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .charts-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .bottom-grid {
    grid-template-columns: 1fr;
  }
  
  .todo-card {
    grid-column: span 1;
  }
  
  .welcome-content {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>

<!-- 额外的深色模式适配（非 scoped） -->
<style>
.dark .activity-list {
  background: transparent !important;
}

.dark .activity-item {
  background: transparent !important;
}

.dark .activity-item:hover {
  background: #374151 !important;
}

.dark .todo-list {
  background: transparent !important;
}

.dark .todo-item:hover {
  background: #374151 !important;
}

/* 确保 Element Plus 组件也适配深色模式 */
.dark .el-checkbox__label {
  color: #e5e7eb !important;
}

.dark .el-tag--danger {
  background-color: rgba(239, 68, 68, 0.2) !important;
  border-color: rgba(239, 68, 68, 0.3) !important;
  color: #fca5a5 !important;
}

.dark .el-tag--warning {
  background-color: rgba(245, 158, 11, 0.2) !important;
  border-color: rgba(245, 158, 11, 0.3) !important;
  color: #fcd34d !important;
}
</style>