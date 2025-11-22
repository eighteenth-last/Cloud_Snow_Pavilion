<template>
  <div class="pos-container">
    <!-- 商品列表区域 -->
    <div class="product-section">
      <!-- 搜索栏 -->
      <el-card class="search-card">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索商品名称..."
          clearable
          @clear="handleSearch"
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
          <template #append>
            <el-button @click="handleSearch">搜索</el-button>
          </template>
        </el-input>
      </el-card>

      <!-- 分类标签（移到搜索框下方） -->
      <div class="category-tabs">
        <div
          v-for="cat in allCategories"
          :key="cat.id"
          class="category-tab"
          :class="{ active: activeCategory === cat.id }"
          @click="activeCategory = cat.id; handleCategoryChange()"
        >
          {{ cat.name }}
        </div>
      </div>

      <!-- 商品网格 -->
      <div class="product-grid-wrapper">
        <div class="product-grid">
          <el-card
            v-for="product in filteredProducts"
            :key="product.id"
            class="product-card"
            shadow="hover"
            @click="addToCart(product)"
          >
            <div class="product-image">
              <el-image :src="product.img || '/default-product.png'" fit="contain">
                <template #error>
                  <div class="image-slot">
                    <el-icon><Picture /></el-icon>
                  </div>
                </template>
              </el-image>
            </div>
            <div class="product-info">
              <div class="product-name" :title="product.name">{{ product.name }}</div>
              <div class="product-price">¥{{ product.price }}</div>
            </div>
          </el-card>
        </div>
      </div>
    </div>

    <!-- 购物车按钮（右下角固定） -->
    <div class="cart-button" @click="cartVisible = true">
      <el-badge :value="cartItems.length" :hidden="cartItems.length === 0" :max="99">
        <el-icon :size="40"><ShoppingCart /></el-icon>
      </el-badge>
      <div class="cart-text">购物车</div>
    </div>

    <!-- 购物车弹窗 -->
    <el-drawer
      v-model="cartVisible"
      title="购物车"
      direction="rtl"
      size="450px"
    >
      <div class="cart-drawer-content">
        <el-empty v-if="cartItems.length === 0" description="购物车为空" :image-size="100" />
        
        <div v-else class="cart-items">
          <div v-for="item in cartItems" :key="item.id" class="cart-item">
            <div class="item-info">
              <div class="item-name">{{ item.name }}</div>
              <div class="item-price">¥{{ item.price }}</div>
            </div>
            <div class="item-actions">
              <el-button-group>
                <el-button size="small" @click="decreaseQuantity(item)">
                  <el-icon><Minus /></el-icon>
                </el-button>
                <el-button size="small" disabled>{{ item.quantity }}</el-button>
                <el-button size="small" @click="increaseQuantity(item)">
                  <el-icon><Plus /></el-icon>
                </el-button>
              </el-button-group>
              <el-button
                link
                type="danger"
                size="small"
                @click="removeFromCart(item)"
                style="margin-left: 10px;"
              >
                <el-icon><Delete /></el-icon>
              </el-button>
            </div>
          </div>
        </div>

        <div class="cart-footer" v-if="cartItems.length > 0">
          <div class="order-type">
            <span>订单类型：</span>
            <el-radio-group v-model="orderType" size="small">
              <el-radio-button label="take">自取</el-radio-button>
              <el-radio-button label="delivery">外卖</el-radio-button>
            </el-radio-group>
          </div>
          
          <div class="total-section">
            <div class="total-row">
              <span>商品总计：</span>
              <span class="total-amount">¥{{ totalAmount.toFixed(2) }}</span>
            </div>
            <div class="total-row discount" v-if="discount > 0">
              <span>优惠：</span>
              <span class="discount-amount">-¥{{ discount.toFixed(2) }}</span>
            </div>
            <div class="total-row final">
              <span>应收：</span>
              <span class="final-amount">¥{{ finalAmount.toFixed(2) }}</span>
            </div>
          </div>

          <div class="action-buttons">
            <el-button @click="clearCart" size="large">清空</el-button>
            <el-button type="primary" size="large" @click="handleCheckout" class="checkout-btn">
              立即下单
            </el-button>
          </div>
        </div>
      </div>
    </el-drawer>

    <!-- 下单确认弹窗 -->
    <el-dialog v-model="checkoutVisible" title="确认下单" width="500px">
      <el-form :model="orderForm" label-width="100px">
        <el-form-item label="订单类型">
          <el-tag :type="orderType === 'take' ? 'success' : 'warning'">
            {{ orderType === 'take' ? '自取' : '外卖' }}
          </el-tag>
        </el-form-item>
        <el-form-item label="商品明细">
          <div class="order-items">
            <div v-for="item in cartItems" :key="item.id" class="order-item-row">
              <span>{{ item.name }} x {{ item.quantity }}</span>
              <span>¥{{ (item.price * item.quantity).toFixed(2) }}</span>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="应收金额">
          <el-text type="danger" size="large" style="font-weight: bold;">
            ¥{{ finalAmount.toFixed(2) }}
          </el-text>
        </el-form-item>
        <el-form-item label="支付方式">
          <el-radio-group v-model="paymentMethod">
            <el-radio label="cash">现金</el-radio>
            <el-radio label="wechat">微信</el-radio>
            <el-radio label="alipay">支付宝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" v-if="orderType === 'delivery'">
          <el-input v-model="orderForm.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="checkoutVisible = false">取消</el-button>
        <el-button type="primary" @click="submitOrder" :loading="submitting">确认下单</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Picture, Minus, Plus, Delete, Search, ShoppingCart } from '@element-plus/icons-vue'
import request from '../utils/request'

const API_BASE_URL = 'http://localhost:8080/api'

const activeCategory = ref('all')
const categories = ref([])
const allCategories = ref([]) // 所有分类（包括“全部”）
const products = ref([])
const allProducts = ref([]) // 保存所有商品
const searchKeyword = ref('')
const cartItems = ref([])
const orderType = ref('take')
const checkoutVisible = ref(false)
const cartVisible = ref(false) // 购物车弹窗显示状态
const paymentMethod = ref('cash')
const submitting = ref(false)
const discount = ref(0)
const orderForm = ref({
  remark: ''
})

// 过滤商品
const filteredProducts = computed(() => {
  console.log('当前分类:', activeCategory.value)
  console.log('商品总数:', products.value.length)
  
  let result = products.value
  
  // 按分类过滤
  if (activeCategory.value !== 'all') {
    result = result.filter(p => {
      const match = String(p.categoryId) === String(activeCategory.value)
      if (!match) {
        console.log(`商品${p.name} categoryId:${p.categoryId} vs 选中:${activeCategory.value}`)
      }
      return match
    })
  }
  
  console.log('过滤后商品数:', result.length)
  return result
})

// 总金额
const totalAmount = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + item.price * item.quantity, 0)
})

// 最终金额
const finalAmount = computed(() => {
  return totalAmount.value - discount.value
})

// 加载分类列表
const loadCategories = async () => {
  try {
    const res = await request.get('/category/list')
    console.log('后端返回的分类:', res.data)
    
    categories.value = res.data || []
    // 添加“全部”选项
    allCategories.value = [
      { id: 'all', name: '全部' },
      ...categories.value
    ]
    
    console.log('所有分类（包括全部）:', allCategories.value)
  } catch (error) {
    console.error('加载分类失败:', error)
    // 新租户没有数据是正常的，不显示错误
    categories.value = []
    allCategories.value = [{ id: 'all', name: '全部' }]
  }
}

// 加载商品列表
const loadProducts = async () => {
  try {
    const res = await request.get('/product/list')
    console.log('后端返回的商品:', res.data)
    
    // 处理图片URL
    const productList = (res.data || []).map(p => ({
      ...p,
      img: getImageUrl(p.img)
    }))
    
    console.log('处理后的商品:', productList)
    
    allProducts.value = productList
    products.value = productList
  } catch (error) {
    console.error('加载商品失败:', error)
    // 新租户没有数据是正常的，不显示错误
    allProducts.value = []
    products.value = []
  }
}

// 搜索商品
const handleSearch = () => {
  if (!searchKeyword.value.trim()) {
    products.value = allProducts.value
    return
  }
  
  const keyword = searchKeyword.value.trim().toLowerCase()
  products.value = allProducts.value.filter(p => 
    p.name.toLowerCase().includes(keyword) || 
    (p.subTitle && p.subTitle.toLowerCase().includes(keyword))
  )
}

// 获取图片完整URL
const getImageUrl = (img) => {
  if (!img) return ''
  if (img.startsWith('http')) return img
  return `${API_BASE_URL}${img}`
}

// 切换分类
const handleCategoryChange = () => {
  console.log('切换分类到:', activeCategory.value)
  // 切换分类时清空搜索
  searchKeyword.value = ''
  products.value = allProducts.value
}

// 添加到购物车
const addToCart = (product) => {
  const existItem = cartItems.value.find(item => item.id === product.id)
  if (existItem) {
    existItem.quantity++
  } else {
    cartItems.value.push({
      ...product,
      quantity: 1
    })
  }
  ElMessage.success(`已添加 ${product.name}`)
}

// 增加数量
const increaseQuantity = (item) => {
  item.quantity++
}

// 减少数量
const decreaseQuantity = (item) => {
  if (item.quantity > 1) {
    item.quantity--
  } else {
    removeFromCart(item)
  }
}

// 从购物车移除
const removeFromCart = (item) => {
  const index = cartItems.value.findIndex(i => i.id === item.id)
  if (index > -1) {
    cartItems.value.splice(index, 1)
  }
}

// 清空购物车
const clearCart = () => {
  cartItems.value = []
  ElMessage.success('已清空购物车')
}

// 结算
const handleCheckout = () => {
  if (cartItems.value.length === 0) {
    ElMessage.warning('购物车为空')
    return
  }
  checkoutVisible.value = true
}

// 提交订单
const submitOrder = async () => {
  submitting.value = true
  try {
    const orderData = {
      orderType: orderType.value,
      items: cartItems.value.map(item => ({
        productId: item.id,
        quantity: item.quantity,
        price: item.price
      })),
      amount: finalAmount.value,
      paymentMethod: paymentMethod.value,
      remark: orderForm.value.remark
    }
    
    await request.post('/order/create', orderData)
    
    ElMessage.success('下单成功！')
    checkoutVisible.value = false
    
    // 清空购物车
    cartItems.value = []
    orderForm.value.remark = ''
    paymentMethod.value = 'cash'
  } catch (error) {
    console.error(error)
    ElMessage.error('下单失败，请重试')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadCategories()
  loadProducts()
})
</script>

<style scoped>
.pos-container {
  padding: 20px;
  padding-bottom: 80px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 60px);
}

/* 商品区域 */
.product-section {
  max-width: 1400px;
  margin: 0 auto;
}

.search-card {
  margin-bottom: 15px;
  flex-shrink: 0;
}

/* 分类标签 - 按钮式 */
.category-tabs {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  flex-wrap: wrap;
  padding: 15px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.category-tab {
  padding: 8px 20px;
  border-radius: 20px;
  background-color: #f5f7fa;
  color: #606266;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 14px;
  user-select: none;
}

.category-tab:hover {
  background-color: #ecf5ff;
  color: #409eff;
}

.category-tab.active {
  background-color: #409eff;
  color: white;
  font-weight: 500;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
  padding: 10px 0;
}

.product-card {
  cursor: pointer;
  transition: all 0.3s;
  border-radius: 12px;
  overflow: hidden;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

.product-image {
  width: 100%;
  height: 200px;
  overflow: hidden;
  background-color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 10px;
}

.product-image .el-image {
  width: 100%;
  height: 100%;
}

.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background-color: #f5f7fa;
  color: #909399;
  font-size: 40px;
}

.product-info {
  text-align: center;
  padding: 10px;
}

.product-name {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-price {
  font-size: 18px;
  font-weight: bold;
  color: #f56c6c;
}

/* 购物车按钮 - 右下角固定 */
.cart-button {
  position: fixed;
  right: 30px;
  bottom: 30px;
  width: 100px;
  height: 100px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.4);
  transition: all 0.3s;
  z-index: 1000;
  color: white;
}

.cart-button:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 30px rgba(102, 126, 234, 0.6);
}

.cart-button .el-badge {
  line-height: 1;
}

.cart-text {
  font-size: 12px;
  margin-top: 5px;
  font-weight: 500;
}

/* 购物车弹窗内容 */
.cart-drawer-content {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.cart-items {
  flex: 1;
  overflow-y: auto;
  padding: 10px 0;
}

.cart-item {
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 8px;
  margin-bottom: 15px;
}

.item-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

.item-name {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.item-price {
  font-size: 14px;
  font-weight: bold;
  color: #f56c6c;
}

.item-actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.cart-footer {
  border-top: 1px solid #ebeef5;
  padding: 20px 0 0 0;
  margin-top: auto;
}

.order-type {
  margin-bottom: 15px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.total-section {
  margin: 15px 0;
}

.total-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 14px;
}

.total-row.discount {
  color: #67c23a;
}

.total-row.final {
  font-size: 18px;
  font-weight: bold;
  padding-top: 10px;
  border-top: 1px solid #ebeef5;
}

.total-amount {
  color: #303133;
}

.discount-amount {
  color: #67c23a;
}

.final-amount {
  color: #f56c6c;
  font-size: 24px;
}

.action-buttons {
  display: flex;
  gap: 10px;
  margin-top: 15px;
}

.action-buttons .el-button {
  flex: 1;
}

.checkout-btn {
  font-weight: bold;
}

/* 订单详情 */
.order-items {
  width: 100%;
}

.order-item-row {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid #ebeef5;
}

.order-item-row:last-child {
  border-bottom: none;
}
</style>