<template>
  <div class="order-container">
    <el-card>
      <template #header>
        <div class="header">
          <span>订单管理</span>
          <div class="stats">
            <el-statistic title="今日订单" :value="todayCount" />
            <el-statistic title="今日金额" :value="todayAmount" prefix="¥" :precision="2" />
          </div>
        </div>
      </template>

      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="订单号">
          <el-input v-model="queryForm.orderId" placeholder="请输入订单号" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="订单状态">
          <el-select v-model="queryForm.status" placeholder="请选择" clearable style="width: 150px">
            <el-option label="全部" :value="null" />
            <el-option label="待支付" :value="0" />
            <el-option label="已支付" :value="1" />
            <el-option label="制作中" :value="2" />
            <el-option label="已完成" :value="3" />
            <el-option label="已取消" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="订单类型">
          <el-select v-model="queryForm.orderType" placeholder="请选择" clearable style="width: 120px">
            <el-option label="全部" :value="null" />
            <el-option label="自取" value="take" />
            <el-option label="外卖" value="delivery" />
          </el-select>
        </el-form-item>
        <el-form-item label="创建时间">
          <el-date-picker
            v-model="queryForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            style="width: 240px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="loadData">查询</el-button>
          <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
          <el-button :icon="Download" @click="exportData">导出</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" style="width: 100%" stripe>
        <el-table-column prop="orderId" label="订单号" width="120" fixed />
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="orderType" label="订单类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.orderType === 'take'" type="success" size="small">自取</el-tag>
            <el-tag v-else type="warning" size="small">外卖</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="订单金额" width="120" align="right">
          <template #default="{ row }">
            <span style="color: #f56c6c; font-weight: bold;">¥{{ row.amount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column prop="payTime" label="支付时间" width="180">
          <template #default="{ row }">
            {{ row.payTime || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right" align="center">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 1"
              link
              type="primary"
              size="small"
              @click="updateStatus(row.orderId, 2)"
            >
              开始制作
            </el-button>
            <el-button
              v-if="row.status === 2"
              link
              type="success"
              size="small"
              @click="updateStatus(row.orderId, 3)"
            >
              完成订单
            </el-button>
            <el-button
              v-if="row.status === 1 || row.status === 2"
              link
              type="danger"
              size="small"
              @click="cancelOrder(row.orderId)"
            >
              取消订单
            </el-button>
            <el-button link type="info" size="small" @click="viewDetail(row)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next"
        @current-change="loadData"
        @size-change="loadData"
        style="margin-top: 20px; justify-content: flex-end;"
      />
    </el-card>

    <el-dialog v-model="detailVisible" title="订单详情" width="700px">
      <el-descriptions :column="2" border size="large">
        <el-descriptions-item label="订单号" label-class-name="label-bold">
          <el-text type="primary" size="large">{{ detail.orderId }}</el-text>
        </el-descriptions-item>
        <el-descriptions-item label="用户ID">
          {{ detail.userId }}
        </el-descriptions-item>
        <el-descriptions-item label="订单类型">
          <el-tag v-if="detail.orderType === 'take'" type="success">自取</el-tag>
          <el-tag v-else type="warning">外卖</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <el-tag :type="getStatusType(detail.status)">
            {{ getStatusText(detail.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="订单金额">
          <el-text type="danger" size="large" style="font-weight: bold;">¥{{ detail.amount }}</el-text>
        </el-descriptions-item>
        <el-descriptions-item label="租户ID">
          {{ detail.tenantId }}
        </el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="2">
          {{ detail.createTime }}
        </el-descriptions-item>
        <el-descriptions-item label="支付时间" :span="2">
          {{ detail.payTime || '未支付' }}
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Download } from '@element-plus/icons-vue'
import request from '../utils/request'

const queryForm = ref({ 
  orderId: '',
  status: null, 
  orderType: null,
  dateRange: null 
})
const tableData = ref([])
const pagination = ref({ current: 1, size: 10, total: 0 })
const detailVisible = ref(false)
const detail = ref({})
const todayCount = ref(0)
const todayAmount = ref(0)

const getStatusType = (status) => {
  const map = { 0: 'info', 1: 'success', 2: 'warning', 3: 'success', 5: 'danger' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { 0: '待支付', 1: '已支付', 2: '制作中', 3: '已完成', 5: '已取消' }
  return map[status] || '未知'
}

const loadData = async () => {
  try {
    const params = {
      current: pagination.value.current,
      size: pagination.value.size,
      status: queryForm.value.status,
      orderType: queryForm.value.orderType,
      orderId: queryForm.value.orderId
    }
    
    if (queryForm.value.dateRange && queryForm.value.dateRange.length === 2) {
      params.startDate = formatDate(queryForm.value.dateRange[0])
      params.endDate = formatDate(queryForm.value.dateRange[1])
    }
    
    const res = await request.get('/order/all', { params })
    tableData.value = res.data.records || []
    pagination.value.total = res.data.total || 0
  } catch (error) {
    console.error(error)
  }
}

const formatDate = (date) => {
  const d = new Date(date)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}

const resetQuery = () => {
  queryForm.value = { 
    orderId: '',
    status: null,
    orderType: null,
    dateRange: null
  }
  pagination.value.current = 1
  loadData()
}

const updateStatus = async (orderId, status) => {
  try {
    await request.put(`/order/${orderId}/status`, null, { params: { status } })
    ElMessage.success('状态更新成功')
    loadData()
    loadTodayStats()
  } catch (error) {
    console.error(error)
  }
}

const cancelOrder = async (orderId) => {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '提示', {
      type: 'warning'
    })
    await request.put(`/order/${orderId}/status`, null, { params: { status: 5 } })
    ElMessage.success('订单已取消')
    loadData()
    loadTodayStats()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const exportData = () => {
  ElMessage.info('导出功能开发中...')
}

const loadTodayStats = async () => {
  try {
    const today = new Date()
    const startDate = formatDate(today)
    const endDate = formatDate(today)
    
    const res = await request.get('/order/all', {
      params: {
        current: 1,
        size: 1000,
        startDate,
        endDate
      }
    })
    
    const orders = res.data.records || []
    todayCount.value = orders.length
    todayAmount.value = orders.reduce((sum, order) => sum + parseFloat(order.amount || 0), 0)
  } catch (error) {
    console.error(error)
  }
}

const viewDetail = (row) => {
  detail.value = row
  detailVisible.value = true
}

onMounted(() => {
  loadData()
  loadTodayStats()
})
</script>

<style scoped>
.order-container {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stats {
  display: flex;
  gap: 40px;
}

.query-form {
  margin-bottom: 16px;
}

:deep(.label-bold) {
  font-weight: bold;
}
</style>
