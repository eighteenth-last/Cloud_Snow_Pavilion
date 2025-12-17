<template>
  <div class="carousel-container">
    <el-card>
      <template #header>
        <div class="header">
          <span>轮播图管理</span>
          <el-button type="primary" @click="handleAdd">新增轮播图</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="queryForm">
        <el-form-item label="标题">
          <el-input v-model="queryForm.title" placeholder="请输入标题" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="全部状态" clearable style="width: 120px">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="图片" width="150" align="center">
          <template #default="{ row }">
            <el-image 
              :src="getImageUrl(row.imgUrl)" 
              style="width: 120px; height: 60px"
              fit="cover"
              :preview-src-list="[getImageUrl(row.imgUrl)]"
            >
              <template #error>
                <div class="image-slot">
                  <el-icon><Picture /></el-icon>
                  <div>加载失败</div>
                </div>
              </template>
            </el-image>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" />
        <el-table-column label="跳转类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getJumpTypeColor(row.jumpType)">
              {{ getJumpTypeText(row.jumpType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="jumpVal" label="跳转值" width="200" show-overflow-tooltip />
        <el-table-column prop="sort" label="排序" width="80" sortable />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(row)"
            />
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
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入轮播图标题" />
        </el-form-item>
        <el-form-item label="轮播图" prop="imgUrl">
          <div class="upload-container">
            <el-upload
              class="avatar-uploader"
              action="#"
              :show-file-list="false"
              :before-upload="handleBeforeUpload"
              :http-request="handleUpload"
            >
              <el-image
                v-if="form.imgUrl"
                :src="getImageUrl(form.imgUrl)"
                class="avatar"
                fit="cover"
              >
                <template #error>
                  <div class="image-slot">
                    <el-icon><Picture /></el-icon>
                  </div>
                </template>
              </el-image>
              <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
            </el-upload>
            <div class="upload-tip">建议尺寸：750x300，支持jpg、png格式</div>
          </div>
        </el-form-item>
        <el-form-item label="跳转类型" prop="jumpType">
          <el-select v-model="form.jumpType" placeholder="请选择跳转类型">
            <el-option label="不跳转" :value="0" />
            <el-option label="商品详情" :value="1" />
            <el-option label="页面路径" :value="2" />
            <el-option label="外部链接" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="form.jumpType !== 0" label="跳转值" prop="jumpVal">
          <el-input 
            v-model="form.jumpVal" 
            :placeholder="getJumpValPlaceholder()" 
          />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" />
          <span style="margin-left: 10px; color: #999;">数值越小越靠前</span>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import request from '../utils/request'

const tableData = ref([])
const pagination = ref({ current: 1, size: 10, total: 0 })
const queryForm = ref({ title: '', status: null })
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const form = ref({})
const submitLoading = ref(false)

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  imgUrl: [{ required: true, message: '请上传轮播图', trigger: 'change' }],
  jumpType: [{ required: true, message: '请选择跳转类型', trigger: 'change' }],
  sort: [{ required: true, message: '请输入排序值', trigger: 'blur' }]
}

const loadData = async () => {
  try {
    const res = await request.get('/carousel/page', {
      params: {
        current: pagination.value.current,
        size: pagination.value.size,
        title: queryForm.value.title || undefined,
        status: queryForm.value.status
      }
    })
    tableData.value = res.data.records || []
    pagination.value.total = res.data.total || 0
  } catch (error) {
    console.error(error)
    ElMessage.error('加载数据失败')
  }
}

const resetQuery = () => {
  queryForm.value = { title: '', status: null }
  pagination.value.current = 1
  loadData()
}

const handleAdd = () => {
  dialogTitle.value = '新增轮播图'
  form.value = {
    title: '',
    imgUrl: '',
    jumpType: 0,
    jumpVal: '',
    sort: 0,
    status: 1,
    tenantId: 1
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑轮播图'
  form.value = { ...row }
  dialogVisible.value = true
}

const handleBeforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB!')
    return false
  }
  return true
}

const handleUpload = async (params) => {
  const formData = new FormData()
  formData.append('file', params.file)
  
  try {
    const res = await request.post('/carousel/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    form.value.imgUrl = res.data
    ElMessage.success('上传成功')
  } catch (error) {
    console.error(error)
    ElMessage.error('上传失败')
  }
}

const handleSubmit = async () => {
  await formRef.value.validate()
  
  submitLoading.value = true
  try {
    if (form.value.id) {
      await request.put('/carousel', form.value)
      ElMessage.success('更新成功')
    } else {
      await request.post('/carousel', form.value)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    console.error(error)
    ElMessage.error(form.value.id ? '更新失败' : '创建失败')
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定要删除该轮播图吗?', '提示', {
    type: 'warning'
  })
  
  try {
    await request.delete(`/carousel/${row.id}`)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    console.error(error)
    ElMessage.error('删除失败')
  }
}

const handleStatusChange = async (row) => {
  try {
    await request.put(`/carousel/${row.id}/status`, null, {
      params: { status: row.status }
    })
    ElMessage.success('状态更新成功')
  } catch (error) {
    console.error(error)
    row.status = row.status === 1 ? 0 : 1 // 恢复原状态
    ElMessage.error('状态更新失败')
  }
}

const getImageUrl = (path) => {
  if (!path) return ''
  if (path.startsWith('http')) return path
  if (path.startsWith('data:')) return path
  
  // 需要加/api前缀（因为context-path是/api）
  if (path.startsWith('/')) {
    return `http://localhost:8080/api${path}`
  }
  
  // 兼容旧的完整本地路径格式
  if (path.includes('upload_img')) {
    const relativePath = path.substring(path.indexOf('upload_img'))
    return `http://localhost:8080/api/${relativePath.replace(/\\/g, '/')}`
  }
  
  return path
}

const getJumpTypeText = (type) => {
  const map = { 0: '不跳转', 1: '商品详情', 2: '页面路径', 3: '外部链接' }
  return map[type] || '未知'
}

const getJumpTypeColor = (type) => {
  const map = { 0: 'info', 1: 'success', 2: 'warning', 3: 'danger' }
  return map[type] || ''
}

const getJumpValPlaceholder = () => {
  const map = {
    1: '请输入商品ID',
    2: '请输入页面路径，如：/pages/product/list',
    3: '请输入完整的URL地址'
  }
  return map[form.value.jumpType] || ''
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.carousel-container {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.upload-container {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.avatar-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  overflow: hidden;
  transition: border-color 0.3s;
}

.avatar-uploader:hover {
  border-color: #409eff;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar {
  width: 178px;
  height: 178px;
  display: block;
}

.upload-tip {
  font-size: 12px;
  color: #999;
}

.image-slot {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  color: #909399;
  font-size: 14px;
}

.image-slot .el-icon {
  font-size: 30px;
  margin-bottom: 5px;
}
</style>
