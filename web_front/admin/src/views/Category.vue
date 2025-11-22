<template>
  <div class="category-container">
    <el-card>
      <template #header>
        <div class="header">
          <span>分类管理</span>
          <el-button type="primary" @click="handleAdd">新增分类</el-button>
        </div>
      </template>

      <el-table :data="tableData" row-key="id" :tree-props="{ children: 'children' }" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="分类名称" />
        <el-table-column prop="sort" label="排序" width="100" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="父分类" prop="parentId">
          <el-select v-model="form.parentId" placeholder="请选择父分类" style="width: 100%;" clearable>
            <el-option label="顶级分类" :value="0" />
            <el-option
              v-for="cat in flatCategories"
              :key="cat.id"
              :label="cat.name"
              :value="cat.id"
              :disabled="form.id === cat.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" />
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
const flatCategories = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const form = ref({})

const rules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

// 构建树结构
const buildTree = (categories) => {
  const tree = []
  const map = new Map()
  
  categories.forEach(cat => {
    map.set(cat.id, { ...cat, children: [] })
  })
  
  categories.forEach(cat => {
    const node = map.get(cat.id)
    if (cat.parentId === 0 || !cat.parentId) {
      tree.push(node)
    } else {
      const parent = map.get(cat.parentId)
      if (parent) {
        parent.children.push(node)
      }
    }
  })
  
  return tree
}

const loadData = async () => {
  try {
    const res = await request.get('/category/list')
    const categories = res.data || []
    flatCategories.value = categories
    tableData.value = buildTree(categories)
  } catch (error) {
    console.error(error)
    // 新租户没有数据是正常的，不显示错误
    flatCategories.value = []
    tableData.value = []
  }
}

const handleAdd = () => {
  dialogTitle.value = '新增分类'
  form.value = { parentId: 0, sort: 0 }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑分类'
  form.value = { ...row }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  
  try {
    if (form.value.id) {
      await request.put('/category', form.value)
      ElMessage.success('更新成功')
    } else {
      await request.post('/category', form.value)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    console.error(error)
  }
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定要删除该分类吗?', '提示', {
    type: 'warning'
  })
  
  try {
    await request.delete(`/category/${row.id}`)
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
.category-container {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
