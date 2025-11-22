<template>
  <div class="ingredient-container">
    <el-card>
      <template #header>
        <div class="header">
          <span>原料管理</span>
          <el-button type="primary" @click="handleAdd">新增原料</el-button>
        </div>
      </template>

      <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="ingredientId" label="ID" width="80" />
        <el-table-column prop="name" label="原料名称" />
        <el-table-column prop="unit" label="单位" width="100" />
        <el-table-column prop="currentStock" label="库存" width="120">
          <template #default="{ row }">
            <el-tag v-if="row.currentStock <= row.minStock" type="danger">
              {{ row.currentStock }}
            </el-tag>
            <span v-else>{{ row.currentStock }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="minStock" label="预警阈值" width="120" />
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <el-button link type="success" @click="handleStockIn(row)">入库</el-button>
            <el-button link type="warning" @click="handleStockOut(row)">出库</el-button>
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="原料名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="单位" prop="unit">
          <el-input v-model="form.unit" placeholder="例：公斤、升" />
        </el-form-item>
        <el-form-item label="库存" prop="currentStock">
          <el-input-number v-model="form.currentStock" :min="0" />
        </el-form-item>
        <el-form-item label="预警阈值" prop="minStock">
          <el-input-number v-model="form.minStock" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="stockDialogVisible" :title="stockDialogTitle" width="500px">
      <el-form :model="stockForm" ref="stockFormRef" label-width="100px">
        <el-form-item label="数量">
          <el-input-number v-model="stockForm.quantity" :min="0.01" :precision="2" />
        </el-form-item>
        <el-form-item label="操作人">
          <el-input v-model="stockForm.operator" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="stockForm.remark" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="stockDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleStockSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../utils/request'

const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const form = ref({})
const stockDialogVisible = ref(false)
const stockDialogTitle = ref('')
const stockFormRef = ref(null)
const stockForm = ref({})
const stockType = ref('') // 'in' or 'out'

const rules = {
  name: [{ required: true, message: '请输入原料名称', trigger: 'blur' }],
  unit: [{ required: true, message: '请输入单位', trigger: 'blur' }]
}

const loadData = async () => {
  try {
    const res = await request.get('/ingredient/list')
    tableData.value = res.data || []
  } catch (error) {
    console.error(error)
  }
}

const handleAdd = () => {
  dialogTitle.value = '新增原料'
  form.value = { currentStock: 0, minStock: 10 }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑原料'
  form.value = { ...row }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  
  try {
    if (form.value.ingredientId) {
      await request.put('/ingredient', form.value)
      ElMessage.success('更新成功')
    } else {
      await request.post('/ingredient', form.value)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    console.error(error)
  }
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定要删除该原料吗?', '提示', {
    type: 'warning'
  })
  
  try {
    await request.delete(`/ingredient/${row.ingredientId}`)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    console.error(error)
  }
}

const handleStockIn = (row) => {
  stockDialogTitle.value = '入库'
  stockType.value = 'in'
  stockForm.value = { ingredientId: row.ingredientId, quantity: 0, operator: '', remark: '' }
  stockDialogVisible.value = true
}

const handleStockOut = (row) => {
  stockDialogTitle.value = '出库'
  stockType.value = 'out'
  stockForm.value = { ingredientId: row.ingredientId, quantity: 0, operator: '', remark: '' }
  stockDialogVisible.value = true
}

const handleStockSubmit = async () => {
  try {
    const url = stockType.value === 'in' 
      ? `/ingredient/${stockForm.value.ingredientId}/in`
      : `/ingredient/${stockForm.value.ingredientId}/out`
    
    await request.post(url, null, {
      params: {
        quantity: stockForm.value.quantity,
        operator: stockForm.value.operator,
        remark: stockForm.value.remark
      }
    })
    ElMessage.success(`${stockType.value === 'in' ? '入库' : '出库'}成功`)
    stockDialogVisible.value = false
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
.ingredient-container {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
