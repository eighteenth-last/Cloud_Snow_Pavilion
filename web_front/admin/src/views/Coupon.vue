<template>
  <div class="coupon-container">
    <el-card>
      <template #header>
        <div class="header">
          <span>优惠券管理</span>
          <el-button type="primary" @click="handleAdd">新增优惠券</el-button>
        </div>
      </template>

      <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="couponId" label="ID" width="80" />
        <el-table-column prop="title" label="优惠券名称" />
        <el-table-column label="类型" width="100">
          <template #default="{ row }">
            <el-tag>满减券</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="面值" width="100">
          <template #default="{ row }">
            ¥{{ row.amount }}
          </template>
        </el-table-column>
        <el-table-column prop="threshold" label="最低消费" width="120">
          <template #default="{ row }">
            ¥{{ row.threshold }}
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="剩余/总数" width="120">
          <template #default="{ row }">
            {{ row.stock }} / {{ row.totalCount }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="优惠券名称" prop="title">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="抵扣金额" prop="amount">
          <el-input-number v-model="form.amount" :min="0" :precision="2" />
          <span style="margin-left: 10px;">元</span>
        </el-form-item>
        <el-form-item label="使用门槛" prop="threshold">
          <el-input-number v-model="form.threshold" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="总数量" prop="totalCount">
          <el-input-number v-model="form.totalCount" :min="1" />
        </el-form-item>
        <el-form-item label="有效期" prop="startTime">
          <el-date-picker
            v-model="form.startTime"
            type="datetime"
            placeholder="开始时间"
          />
          <span style="margin: 0 10px;">~</span>
          <el-date-picker
            v-model="form.endTime"
            type="datetime"
            placeholder="结束时间"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../utils/request'

const tableData = ref([])
const pagination = ref({ current: 1, size: 10, total: 0 })
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const form = ref({})

const rules = {
  title: [{ required: true, message: '请输入优惠券名称', trigger: 'blur' }],
  amount: [{ required: true, message: '请输入抵扣金额', trigger: 'blur' }]
}

const loadData = async () => {
  try {
    const res = await request.get('/coupon/page', {
      params: {
        current: pagination.value.current,
        size: pagination.value.size
      }
    })
    tableData.value = res.data.records || []
    pagination.value.total = res.data.total || 0
  } catch (error) {
    console.error(error)
  }
}

const handleAdd = () => {
  dialogTitle.value = '新增优惠券'
  form.value = {
    threshold: 0,
    amount: 0,
    totalCount: 100,
    stock: 100
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑优惠券'
  form.value = { ...row }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  
  try {
    const data = {
      ...form.value,
      stock: form.value.stock || form.value.totalCount
    }
    
    if (data.couponId) {
      await request.put('/coupon', data)
      ElMessage.success('更新成功')
    } else {
      await request.post('/coupon', data)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    console.error(error)
  }
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定要删除该优惠券吗?', '提示', {
    type: 'warning'
  })
  
  try {
    await request.delete(`/coupon/${row.couponId}`)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.coupon-container {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
