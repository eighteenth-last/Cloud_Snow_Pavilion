<template>
  <div class="product-container">
    <div class="layout-wrapper">
      <!-- 左侧分类树 -->
      <el-card class="category-panel">
        <template #header>
          <div class="panel-header">
            <span>商品分类</span>
          </div>
        </template>
        <el-tree
          :data="categoryTree"
          :props="treeProps"
          node-key="id"
          highlight-current
          :expand-on-click-node="false"
          @node-click="handleCategoryClick"
        >
          <template #default="{ node, data }">
            <span class="custom-tree-node">
              <span>{{ data.name }}</span>
              <span class="node-count">({{ data.productCount || 0 }})</span>
            </span>
          </template>
        </el-tree>
      </el-card>

      <!-- 右侧商品列表 -->
      <div class="product-panel">
        <el-card>
      <template #header>
        <div class="header">
          <span>商品管理</span>
          <el-button type="primary" @click="handleAdd">新增商品</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="queryForm">
        <el-form-item label="商品名称">
          <el-input v-model="queryForm.name" placeholder="请输入商品名称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" style="width: 100%" :height="tableHeight">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="商品图片" width="100" align="center">
          <template #default="{ row }">
            <el-image
              v-if="row.img"
              :src="getImageUrl(row.img)"
              :preview-src-list="[getImageUrl(row.img)]"
              fit="cover"
              style="width: 60px; height: 60px; border-radius: 4px;"
              @error="handleImageError"
            >
              <template #error>
                <div class="image-slot">
                  <el-icon><Picture /></el-icon>
                </div>
              </template>
            </el-image>
            <span v-else style="color: #ccc;">暂无图片</span>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="商品名称" show-overflow-tooltip />
        <el-table-column label="分类" width="210" align="center">
          <template #default="{ row }">
            <el-tag v-if="getCategoryName(row.categoryId)" type="primary" size="default">
              {{ getCategoryName(row.categoryId) }}
            </el-tag>
            <el-tag v-else type="info" size="default">未分类</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="price" label="价格" width="120" align="center">
          <template #default="{ row }">
            ¥{{ row.price }}
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="100" align="center" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right" align="center">
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
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
        style="margin-top: 20px; justify-content: flex-end;"
      />
        </el-card>
      </div>
    </div>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="副标题" prop="subTitle">
          <el-input v-model="form.subTitle" />
        </el-form-item>
        <el-form-item label="商品分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择分类" style="width: 100%;">
            <el-option
              v-for="cat in flatCategories"
              :key="cat.id"
              :label="cat.name"
              :value="cat.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="商品图片" prop="img">
          <el-upload
            class="avatar-uploader"
            :action="uploadUrl"
            :show-file-list="false"
            :on-change="handleFileChange"
            :before-upload="beforeUpload"
            :auto-upload="false"
            ref="uploadRef"
          >
            <img v-if="previewImage" :src="previewImage" class="avatar" :key="previewImage" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
          <div style="color: #999; font-size: 12px; margin-top: 5px;">
            点击选择图片，支持 jpg/png 格式，大小不超过 2MB
          </div>
          <el-button v-if="pendingFile" type="danger" size="small" style="margin-top: 10px;" @click="clearPendingFile">清除图片</el-button>
        </el-form-item>
        <el-form-item label="售价" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="原价" prop="originPrice">
          <el-input-number v-model="form.originPrice" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="form.stock" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">上架</el-radio>
            <el-radio :label="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Picture } from '@element-plus/icons-vue'
import request from '../utils/request'

const API_BASE_URL = 'http://localhost:8080/api'
const uploadUrl = `${API_BASE_URL}/file/upload`
const uploadHeaders = computed(() => {
  const token = localStorage.getItem('token')
  return token ? { 'Authorization': `Bearer ${token}` } : {}
})

const queryForm = ref({ name: '', categoryId: null })
const tableData = ref([])
const pagination = ref({ current: 1, size: 10, total: 0 })
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const form = ref({})
const categoryTree = ref([])
const flatCategories = ref([])
const selectedCategoryId = ref(null)
const tableHeight = ref(600) // 表格高度
const uploadRef = ref(null) // upload组件引用
const pendingFile = ref(null) // 待上传的文件
const previewImage = ref('') // 预览图片URL（本地或服务器）

const treeProps = {
  children: 'children',
  label: 'name'
}

const rules = {
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  price: [{ required: true, message: '请输入售价', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择商品分类', trigger: 'change' }]
}

// 加载分类列表
const loadCategories = async () => {
  try {
    const res = await request.get('/category/list')
    const categories = res.data || []
    
    // 构建分类树
    categoryTree.value = buildTree(categories)
    flatCategories.value = categories
    
    // 统计每个分类的商品数量
    await loadProductCounts()
  } catch (error) {
    console.error(error)
  }
}

// 构建树结构
const buildTree = (categories) => {
  const tree = []
  const map = new Map()
  
  // 先将所有分类放入map
  categories.forEach(cat => {
    map.set(cat.id, { ...cat, children: [], productCount: 0 })
  })
  
  // 构建树形结构
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
  
  // 添加“全部”节点
  tree.unshift({
    id: null,
    name: '全部商品',
    children: [],
    productCount: 0
  })
  
  return tree
}

// 加载商品数量统计
const loadProductCounts = async () => {
  try {
    const res = await request.get('/product/list')
    const products = res.data || []
    
    // 统计每个分类的商品数
    const countMap = new Map()
    products.forEach(p => {
      const count = countMap.get(p.categoryId) || 0
      countMap.set(p.categoryId, count + 1)
    })
    
    // 更新树节点的商品数
    const updateCount = (nodes) => {
      nodes.forEach(node => {
        if (node.id === null) {
          node.productCount = products.length
        } else {
          node.productCount = countMap.get(node.id) || 0
        }
        if (node.children && node.children.length > 0) {
          updateCount(node.children)
        }
      })
    }
    
    updateCount(categoryTree.value)
  } catch (error) {
    console.error(error)
  }
}

// 点击分类节点
const handleCategoryClick = (data) => {
  selectedCategoryId.value = data.id
  queryForm.value.categoryId = data.id
  pagination.value.current = 1 // 重置到第一页
  loadData()
}

// 分页变化事件
const handlePageChange = (page) => {
  console.log('切换到第', page, '页')
  pagination.value.current = page
  loadData()
}

const handleSizeChange = (size) => {
  console.log('每页条数变为', size)
  pagination.value.size = size
  pagination.value.current = 1 // 改变每页条数时重置到第一页
  loadData()
}

const loadData = async () => {
  try {
    const params = {
      current: pagination.value.current,
      size: pagination.value.size
    }
    
    if (queryForm.value.name) {
      params.name = queryForm.value.name
    }
    
    // 只有当categoryId不为null且不是'all'时才传递
    if (queryForm.value.categoryId !== null && 
        queryForm.value.categoryId !== undefined && 
        queryForm.value.categoryId !== 'all') {
      params.categoryId = queryForm.value.categoryId
    }
    
    console.log('请求参数:', params)
    
    const res = await request.get('/product/page', { params })
    
    console.log('后端返回:', res)
    console.log('records:', res.data?.records)
    console.log('total:', res.data?.total)
    
    if (res.data && res.data.records) {
      tableData.value = res.data.records
      pagination.value.total = res.data.total || 0
      console.log('设置表格数据:', tableData.value.length, '条')
      console.log('总数:', pagination.value.total)
    } else {
      tableData.value = []
      pagination.value.total = 0
    }
  } catch (error) {
    console.error('加载商品失败:', error)
    // 新租户没有数据是正常的，不显示错误
    tableData.value = []
    pagination.value.total = 0
  }
}

const resetQuery = () => {
  queryForm.value = { name: '', categoryId: null }
  selectedCategoryId.value = null
  pagination.value.current = 1 // 重置到第一页
  loadData()
}

const handleAdd = () => {
  dialogTitle.value = '新增商品'
  form.value = { 
    status: 1, 
    stock: 0, 
    price: 0, 
    originPrice: 0, 
    img: '',
    categoryId: selectedCategoryId.value 
  }
  pendingFile.value = null // 清空待上传文件
  previewImage.value = '' // 清空预览
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑商品'
  form.value = { ...row }
  pendingFile.value = null // 清空待上传文件
  
  // 如果有原图片，显示原图片
  if (row.img) {
    previewImage.value = getImageUrl(row.img)
    console.log('编辑商品，原图片URL:', previewImage.value)
  } else {
    previewImage.value = ''
  }
  
  dialogVisible.value = true
}

// 图片加载失败处理
const handleImageError = (error) => {
  console.error('图片加载失败:', error)
}

// 获取分类名称
const getCategoryName = (categoryId) => {
  const category = flatCategories.value.find(c => c.id === categoryId)
  return category ? category.name : ''
}

// 图片上传前验证
const beforeUpload = (file) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG) {
    ElMessage.error('图片只能是 JPG/PNG 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

// 图片选择后 - 仅预览，不上传
const handleFileChange = (file) => {
  console.log('选择文件:', file)
  
  // 验证文件
  const isJPG = file.raw.type === 'image/jpeg' || file.raw.type === 'image/png'
  const isLt2M = file.raw.size / 1024 / 1024 < 2

  if (!isJPG) {
    ElMessage.error('图片只能是 JPG/PNG 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  
  // 保存待上传的文件
  pendingFile.value = file.raw
  
  // 创建本地预览URL
  const reader = new FileReader()
  reader.onload = (e) => {
    previewImage.value = e.target.result
    console.log('预览图片已生成')
  }
  reader.readAsDataURL(file.raw)
  
  ElMessage.success('图片已选择，点击确定后上传并保存')
}

// 清除待上传文件
const clearPendingFile = () => {
  pendingFile.value = null
  // 如果是编辑状态，恢复原图片；否则清空
  if (form.value.img) {
    previewImage.value = getImageUrl(form.value.img)
  } else {
    previewImage.value = ''
  }
  ElMessage.info('已清除选择的图片')
}

// 实际上传图片到服务器
const uploadImage = async () => {
  if (!pendingFile.value) {
    return null // 没有待上传文件
  }
  
  const formData = new FormData()
  formData.append('file', pendingFile.value)
  
  try {
    const token = localStorage.getItem('token')
    console.log('开始上传图片...')
    console.log('Token:', token ? '存在' : '不存在')
    
    const headers = {}
    if (token) {
      headers['Authorization'] = `Bearer ${token}`
      headers['satoken'] = token // 添加satoken头
    }
    
    const response = await fetch(uploadUrl, {
      method: 'POST',
      headers: headers,
      body: formData
    })
    
    const result = await response.json()
    console.log('上传响应:', result)
    
    if (result.code === 0) {
      console.log('图片上传成功:', result.data)
      return result.data // 返回图片URL
    } else {
      throw new Error(result.message || '上传失败')
    }
  } catch (error) {
    console.error('上传错误:', error)
    ElMessage.error('图片上传失败：' + error.message)
    return null
  }
}

// 获取图片完整URL
const getImageUrl = (img) => {
  if (!img) {
    console.log('图片URL为空')
    return ''
  }
  if (img.startsWith('http')) {
    console.log('完整URL:', img)
    return img
  }
  if (img.startsWith('data:')) {
    // Base64图片
    console.log('Base64图片')
    return img
  }
  const fullUrl = `${API_BASE_URL}${img}`
  console.log('拼接URL:', fullUrl)
  return fullUrl
}

const handleSubmit = async () => {
  await formRef.value.validate()
  
  try {
    // 如果有待上传的文件，先上传
    if (pendingFile.value) {
      console.log('检测到待上传文件，开始上传...')
      const uploadedUrl = await uploadImage()
      
      if (!uploadedUrl) {
        ElMessage.error('图片上传失败，请重试')
        return
      }
      
      form.value.img = uploadedUrl
      console.log('图片上传成功，URL:', uploadedUrl)
    }
    
    // 保存商品数据
    if (form.value.id) {
      await request.put('/product', form.value)
      ElMessage.success('更新成功')
    } else {
      await request.post('/product', form.value)
      ElMessage.success('创建成功')
    }
    
    dialogVisible.value = false
    pendingFile.value = null // 清空待上传文件
    previewImage.value = '' // 清空预览
    loadData()
    loadProductCounts() // 重新加载分类统计
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败：' + (error.message || '未知错误'))
  }
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定要删除该商品吗?', '提示', {
    type: 'warning'
  })
  
  try {
    await request.delete(`/product/${row.id}`)
    ElMessage.success('删除成功')
    loadData()
    loadProductCounts() // 重新加载分类统计
  } catch (error) {
    console.error(error)
  }
}

// 取消编辑
const handleCancel = () => {
  pendingFile.value = null // 清空待上传文件，不上传
  previewImage.value = '' // 清空预览
  dialogVisible.value = false
  ElMessage.info('已取消')
}

onMounted(() => {
  loadCategories()
  pagination.value.current = 1 // 确保从第一页开始
  loadData()
})
</script>

<style scoped>
.product-container {
  padding: 20px;
  min-height: calc(100vh - 100px);
}

.layout-wrapper {
  display: flex;
  gap: 20px;
  min-height: calc(100vh - 140px);
}

.category-panel {
  width: 200px;
  flex-shrink: 0;
  max-height: calc(100vh - 140px);
  overflow-y: auto;
}

.product-panel {
  flex: 1;
  min-width: 0;
}

.panel-header {
  font-weight: bold;
  font-size: 16px;
}

.custom-tree-node {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  padding-right: 10px;
}

.node-count {
  color: #909399;
  font-size: 12px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.avatar-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s;
}

.avatar-uploader:hover {
  border-color: #409eff;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 150px;
  height: 150px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar {
  width: 150px;
  height: 150px;
  display: block;
  object-fit: cover;
}

.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  color: #909399;
  font-size: 30px;
}
</style>
