<template>
  <div class="analysis-container">
    <!-- 时间筛选 -->
    <el-card class="filter-card">
      <div class="filter-header">
        <el-radio-group v-model="quickSelect" @change="handleQuickSelect" size="default">
          <el-radio-button label="today">今天</el-radio-button>
          <el-radio-button label="week">本周</el-radio-button>
          <el-radio-button label="month">本月</el-radio-button>
          <el-radio-button label="year">本年</el-radio-button>
          <el-radio-button label="custom">自定义</el-radio-button>
        </el-radio-group>
        <el-date-picker
          v-if="quickSelect === 'custom'"
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          @change="loadData"
          style="margin-left: 20px"
        />
      </div>
    </el-card>

    <!-- 收入统计 -->
    <el-card class="stats-card">
      <template #header>
        <div class="header">
          <span>收入统计</span>
        </div>
      </template>

      <el-row :gutter="20">
        <el-col :span="8">
          <div class="stat-item">
            <div class="stat-icon" style="background: #ecf5ff; color: #409eff">
              <el-icon :size="24"><Document /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-label">订单总数</div>
              <div class="stat-value">{{ revenue.orderCount }}</div>
              <div class="stat-trend" v-if="revenue.orderCountGrowth !== undefined">
                <el-icon v-if="revenue.orderCountGrowth >= 0" color="#67c23a"><CaretTop /></el-icon>
                <el-icon v-else color="#f56c6c"><CaretBottom /></el-icon>
                <span :style="{ color: revenue.orderCountGrowth >= 0 ? '#67c23a' : '#f56c6c' }">
                  {{ Math.abs(revenue.orderCountGrowth) }}%
                </span>
              </div>
            </div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-item">
            <div class="stat-icon" style="background: #fef0f0; color: #f56c6c">
              <el-icon :size="24"><Wallet /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-label">总收入</div>
              <div class="stat-value">¥{{ revenue.totalRevenue }}</div>
              <div class="stat-trend" v-if="revenue.revenueGrowth !== undefined">
                <el-icon v-if="revenue.revenueGrowth >= 0" color="#67c23a"><CaretTop /></el-icon>
                <el-icon v-else color="#f56c6c"><CaretBottom /></el-icon>
                <span :style="{ color: revenue.revenueGrowth >= 0 ? '#67c23a' : '#f56c6c' }">
                  {{ Math.abs(revenue.revenueGrowth) }}%
                </span>
              </div>
            </div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-item">
            <div class="stat-icon" style="background: #f0f9ff; color: #409eff">
              <el-icon :size="24"><TrendCharts /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-label">平均订单金额</div>
              <div class="stat-value">¥{{ revenue.avgOrderAmount }}</div>
              <div class="stat-trend" v-if="revenue.avgGrowth !== undefined">
                <el-icon v-if="revenue.avgGrowth >= 0" color="#67c23a"><CaretTop /></el-icon>
                <el-icon v-else color="#f56c6c"><CaretBottom /></el-icon>
                <span :style="{ color: revenue.avgGrowth >= 0 ? '#67c23a' : '#f56c6c' }">
                  {{ Math.abs(revenue.avgGrowth) }}%
                </span>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 收入趋势图 -->
    <el-card class="chart-card">
      <template #header>
        <div class="header">
          <span>收入趋势</span>
        </div>
      </template>
      <div ref="revenueChartRef" style="width: 100%; height: 350px;"></div>
    </el-card>

    <!-- 商品销量排行 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="header">
              <span>商品销量排行</span>
            </div>
          </template>
          <div ref="salesChartRef" style="width: 100%; height: 400px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="header">
              <span>销量明细</span>
            </div>
          </template>
          <el-table :data="salesData" style="width: 100%" max-height="400">
            <el-table-column type="index" label="排名" width="60" />
            <el-table-column prop="skuId" label="SKU ID" width="80" />
            <el-table-column prop="salesCount" label="销量" width="80">
              <template #default="{ row }">
                <el-tag type="success" size="small">{{ row.salesCount }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="salesAmount" label="销售额">
              <template #default="{ row }">
                <el-tag type="warning" size="small">¥{{ row.salesAmount }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { Document, Wallet, TrendCharts, CaretTop, CaretBottom } from '@element-plus/icons-vue'
import request from '../utils/request'
import * as echarts from 'echarts'

const quickSelect = ref('month')
const dateRange = ref([])
const revenue = ref({
  orderCount: 0,
  totalRevenue: 0,
  avgOrderAmount: 0,
  orderCountGrowth: 0,
  revenueGrowth: 0,
  avgGrowth: 0
})
const salesData = ref([])
const revenueTrendData = ref([])

const revenueChartRef = ref(null)
const salesChartRef = ref(null)
let revenueChart = null
let salesChart = null

const formatDate = (date) => {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

const handleQuickSelect = (val) => {
  const now = new Date()
  let start, end

  switch (val) {
    case 'today':
      start = end = now
      break
    case 'week':
      const day = now.getDay() || 7
      start = new Date(now.getFullYear(), now.getMonth(), now.getDate() - day + 1)
      end = now
      break
    case 'month':
      start = new Date(now.getFullYear(), now.getMonth(), 1)
      end = new Date(now.getFullYear(), now.getMonth() + 1, 0)
      break
    case 'year':
      start = new Date(now.getFullYear(), 0, 1)
      end = new Date(now.getFullYear(), 11, 31)
      break
    case 'custom':
      return
  }

  dateRange.value = [start, end]
  loadData()
}

const loadRevenue = async () => {
  try {
    const params = {}
    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = formatDate(dateRange.value[0])
      params.endDate = formatDate(dateRange.value[1])
    }
    
    const res = await request.get('/analysis/revenue', { params })
    revenue.value = res.data
  } catch (error) {
    console.error(error)
    revenue.value = {
      orderCount: 0,
      totalRevenue: 0,
      avgOrderAmount: 0
    }
  }
}

const loadSales = async () => {
  try {
    const params = {}
    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = formatDate(dateRange.value[0])
      params.endDate = formatDate(dateRange.value[1])
    }
    
    const res = await request.get('/analysis/productSales', { params })
    salesData.value = (res.data || []).slice(0, 10)
    renderSalesChart()
  } catch (error) {
    console.error(error)
    salesData.value = []
  }
}

const loadRevenueTrend = async () => {
  try {
    const params = {}
    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = formatDate(dateRange.value[0])
      params.endDate = formatDate(dateRange.value[1])
    }
    
    // 获取收入趋势数据
    const res = await request.get('/analysis/revenueTrend', { params })
    revenueTrendData.value = res.data || []
    renderRevenueChart()
  } catch (error) {
    console.error(error)
    revenueTrendData.value = []
  }
}

const renderRevenueChart = () => {
  if (!revenueChartRef.value) return

  if (!revenueChart) {
    revenueChart = echarts.init(revenueChartRef.value)
  }

  const option = {
    title: {
      text: '收入趋势',
      left: 'center',
      textStyle: {
        fontSize: 14,
        fontWeight: 'normal'
      }
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross'
      }
    },
    legend: {
      data: ['收入', '订单数'],
      top: 30
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: revenueTrendData.value.map(item => item.date) || []
    },
    yAxis: [
      {
        type: 'value',
        name: '收入(元)',
        position: 'left'
      },
      {
        type: 'value',
        name: '订单数',
        position: 'right'
      }
    ],
    series: [
      {
        name: '收入',
        type: 'line',
        smooth: true,
        data: revenueTrendData.value.map(item => item.revenue) || [],
        itemStyle: {
          color: '#409eff'
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
            { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
          ])
        }
      },
      {
        name: '订单数',
        type: 'line',
        smooth: true,
        yAxisIndex: 1,
        data: revenueTrendData.value.map(item => item.orderCount) || [],
        itemStyle: {
          color: '#67c23a'
        }
      }
    ]
  }

  revenueChart.setOption(option)
}

const renderSalesChart = () => {
  if (!salesChartRef.value) return

  if (!salesChart) {
    salesChart = echarts.init(salesChartRef.value)
  }

  const option = {
    title: {
      text: 'TOP 10 商品销量',
      left: 'center',
      textStyle: {
        fontSize: 14,
        fontWeight: 'normal'
      }
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'value'
    },
    yAxis: {
      type: 'category',
      data: salesData.value.map(item => `SKU ${item.skuId}`)
    },
    series: [
      {
        name: '销量',
        type: 'bar',
        data: salesData.value.map(item => item.salesCount),
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
            { offset: 0, color: '#83bff6' },
            { offset: 0.5, color: '#188df0' },
            { offset: 1, color: '#188df0' }
          ])
        },
        label: {
          show: true,
          position: 'right'
        }
      }
    ]
  }

  salesChart.setOption(option)
}

const loadData = () => {
  loadRevenue()
  loadSales()
  loadRevenueTrend()
}

onMounted(() => {
  // 默认本月
  handleQuickSelect('month')
  
  nextTick(() => {
    renderRevenueChart()
    renderSalesChart()
  })
  
  // 监听窗口大小变化
  window.addEventListener('resize', () => {
    revenueChart?.resize()
    salesChart?.resize()
  })
})
</script>

<style scoped>
.analysis-container {
  padding: 20px;
}

.filter-card {
  margin-bottom: 20px;
}

.filter-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
  font-weight: 500;
}

.stats-card {
  margin-bottom: 20px;
}

.stat-item {
  display: flex;
  align-items: center;
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  border: 1px solid #ebeef5;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
}

.stat-content {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 4px;
}

.stat-trend {
  display: flex;
  align-items: center;
  font-size: 12px;
  gap: 4px;
}

.chart-card {
  margin-bottom: 20px;
}
</style>
