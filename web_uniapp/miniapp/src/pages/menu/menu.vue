<template>
	<view class="container">
		<!-- 门店选择栏 -->
		<view class="store-selector" @click="showStoreSelector = true">
			<view class="store-info">
				<text class="store-icon">📍</text>
				<view class="store-detail">
					<text class="store-name">{{ selectedStore ? selectedStore.name : '请选择门店' }}</text>
					<text v-if="selectedStore" class="store-address">{{ selectedStore.address }}</text>
				</view>
			</view>
			<text class="arrow">›</text>
		</view>

		<!-- 未选择门店提示 -->
		<view v-if="!selectedStore" class="empty-tip">
			<image src="/static/store-empty.png" mode="aspectFit" class="empty-icon" />
			<text class="empty-text">请先选择门店</text>
		</view>

		<!-- 商品内容区 -->
		<view v-else class="content">
			<!-- 左侧分类 -->
			<view class="category-list">
				<view 
					v-for="(category, index) in categories" 
					:key="category.id"
					:class="['category-item', currentCategoryIndex === index ? 'active' : '']"
					@click="selectCategory(index)"
				>
					<text class="category-name">{{ category.name }}</text>
				</view>
			</view>

			<!-- 右侧商品列表 -->
			<scroll-view class="product-list" scroll-y>
				<view v-if="currentProducts.length === 0" class="no-product">
					<text>暂无商品</text>
				</view>
				<view v-else>
					<view 
						v-for="product in currentProducts" 
						:key="product.id"
						class="product-item"
					>
						<image 
							:src="getImageUrl(product.img)" 
							mode="aspectFill"
							class="product-img"
							@click="goToDetail(product.id)"
						/>
						<view class="product-info">
							<text class="product-name">{{ product.name }}</text>
							<text class="product-subtitle">{{ product.subTitle }}</text>
							<view class="product-bottom">
								<view class="price-box">
									<text class="price">¥{{ product.price }}</text>
									<text v-if="product.originPrice && product.originPrice > product.price" class="origin-price">¥{{ product.originPrice }}</text>
								</view>
								<view class="cart-control">
									<view v-if="getCartCount(product.id) > 0" class="control-btn minus" @click="removeFromCart(product)">
										<text>-</text>
									</view>
									<text v-if="getCartCount(product.id) > 0" class="count">{{ getCartCount(product.id) }}</text>
									<view class="control-btn plus" @click="addToCart(product)">
										<text>+</text>
									</view>
								</view>
							</view>
						</view>
					</view>
				</view>
			</scroll-view>
		</view>

		<!-- 底部购物车栏 -->
		<view v-if="selectedStore && cartTotalCount > 0" class="cart-bar">
			<view class="cart-left" @click="goToCart">
				<view class="cart-icon-wrapper">
					<text class="cart-icon">🛒</text>
					<view class="cart-badge">{{ cartTotalCount }}</view>
				</view>
				<view class="price-info">
					<view class="price-row">
						<text class="price-label">到手价</text>
						<text class="price-value">¥{{ cartTotalPrice.toFixed(2) }}</text>
					</view>
					<text class="price-tip">再买¥3，可再减¥0.5起</text>
				</view>
			</view>
			<view class="checkout-btn" @click="goToCheckout">
				去结算
			</view>
		</view>

		<!-- 门店选择弹窗 -->
		<view v-if="showStoreSelector" class="popup-mask" @click="showStoreSelector = false">
			<view class="store-popup" @click.stop>
				<view class="popup-header">
					<text class="popup-title">选择门店</text>
					<text class="popup-close" @click="showStoreSelector = false">×</text>
				</view>
				<scroll-view class="store-list" scroll-y>
					<view 
						v-for="store in storeList" 
						:key="store.id"
						:class="['store-item', selectedStore && selectedStore.id === store.id ? 'selected' : '']"
						@click="selectStore(store)"
					>
						<view class="store-main">
							<text class="store-name">{{ store.name }}</text>
							<text class="store-address">{{ store.address }}</text>
							<text class="store-time">营业时间：{{ store.businessHours || '9:00-22:00' }}</text>
						</view>
						<text v-if="selectedStore && selectedStore.id === store.id" class="check-icon">✓</text>
					</view>
				</scroll-view>
			</view>
		</view>
	</view>
</template>

<script>
import { productApi, storeApi } from '@/api/index.js'

export default {
	data() {
		return {
			showStoreSelector: false,
			selectedStore: null,
			storeList: [],
			categories: [],
			products: [],
			currentCategoryIndex: 0,
			cart: [] // { productId, name, price, count, img }
		}
	},
	computed: {
		// 当前选中分类的商品
		currentProducts() {
			if (this.categories.length === 0) return []
			const currentCategory = this.categories[this.currentCategoryIndex]
			return this.products.filter(p => p.categoryId === currentCategory.id)
		},
		// 购物车总数量
		cartTotalCount() {
			return this.cart.reduce((sum, item) => sum + item.count, 0)
		},
		// 购物车总价格
		cartTotalPrice() {
			return this.cart.reduce((sum, item) => sum + item.price * item.count, 0)
		}
	},
	onLoad() {
		// 尝试从缓存读取已选门店
		const cachedStore = uni.getStorageSync('selectedStore')
		if (cachedStore) {
			this.selectedStore = cachedStore
			this.loadProducts()
		}
		this.loadStores()
		this.loadCategories()
	},
	methods: {
		// 加载门店列表
		async loadStores() {
			try {
				const res = await storeApi.getList()
				if (res.code === 200) {
					this.storeList = res.data || []
				}
			} catch (error) {
				console.error('加载门店列表失败', error)
			}
		},
		// 选择门店
		selectStore(store) {
			this.selectedStore = store
			uni.setStorageSync('selectedStore', store)
			this.showStoreSelector = false
			this.loadProducts()
			uni.showToast({
				title: '已选择' + store.name,
				icon: 'success'
			})
		},
		// 加载商品分类
		async loadCategories() {
			try {
				const res = await productApi.getCategories()
				if (res.code === 200) {
					this.categories = res.data || []
				}
			} catch (error) {
				console.error('加载分类失败', error)
			}
		},
		// 加载商品列表
		async loadProducts() {
			try {
				const res = await productApi.getList()
				if (res.code === 200) {
					this.products = res.data || []
				}
			} catch (error) {
				console.error('加载商品失败', error)
			}
		},
		// 选择分类
		selectCategory(index) {
			this.currentCategoryIndex = index
		},
		// 获取商品在购物车中的数量
		getCartCount(productId) {
			const item = this.cart.find(i => i.productId === productId)
			return item ? item.count : 0
		},
		// 添加到购物车
		addToCart(product) {
			const existItem = this.cart.find(i => i.productId === product.id)
			if (existItem) {
				existItem.count++
			} else {
				this.cart.push({
					productId: product.id,
					name: product.name,
					price: product.price,
					img: product.img,
					count: 1
				})
			}
			// 保存到缓存
			uni.setStorageSync('cart', this.cart)
		},
		// 从购物车移除
		removeFromCart(product) {
			const existItem = this.cart.find(i => i.productId === product.id)
			if (existItem) {
				existItem.count--
				if (existItem.count <= 0) {
					this.cart = this.cart.filter(i => i.productId !== product.id)
				}
			}
			// 保存到缓存
			uni.setStorageSync('cart', this.cart)
		},
		// 查看商品详情
		goToDetail(productId) {
			uni.navigateTo({
				url: `/pages/product/detail?id=${productId}`
			})
		},
		// 去购物车
		goToCart() {
			uni.navigateTo({
				url: '/pages/cart/cart'
			})
		},
		// 去结算
		goToCheckout() {
			if (this.cartTotalCount === 0) {
				uni.showToast({
					title: '购物车是空的',
					icon: 'none'
				})
				return
			}
			
			if (!this.selectedStore) {
				uni.showToast({
					title: '请先选择门店',
					icon: 'none'
				})
				return
			}
			
			// 保存门店信息到缓存
			uni.setStorageSync('checkoutStore', this.selectedStore)
			
			// 保存商品信息到缓存（转换为订单格式）
			const checkoutItems = this.cart.map(item => ({
				id: item.productId,
				name: item.name,
				price: item.price,
				img: item.img,
				quantity: item.count
			}))
			uni.setStorageSync('checkoutItems', checkoutItems)
			
			uni.navigateTo({
				url: '/pages/order/confirm'
			})
		},
		// 处理图片URL
		getImageUrl(img) {
			if (!img) {
				return '/static/product-default.png'
			}
			// 如果是相对路径，转换为完整URL
			if (img.startsWith('/upload_img/')) {
				return `http://localhost:8080/api${img}`
			}
			// 如果已经是完整URL
			if (img.startsWith('http://') || img.startsWith('https://')) {
				return img
			}
			return '/static/product-default.png'
		}
	}
}
</script>

<style scoped>
.container {
	display: flex;
	flex-direction: column;
	height: 100vh;
	background-color: #f8f9fa;
}

/* 门店选择栏 */
.store-selector {
	background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
	padding: 24rpx 30rpx;
	display: flex;
	justify-content: space-between;
	align-items: center;
	box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
	transition: all 0.3s;
}

.store-info {
	display: flex;
	align-items: center;
	flex: 1;
}

.store-icon {
	font-size: 40rpx;
	margin-right: 20rpx;
}

.store-detail {
	display: flex;
	flex-direction: column;
}

.store-name {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 8rpx;
}

.store-address {
	font-size: 24rpx;
	color: #999;
}

.arrow {
	font-size: 32rpx;
	color: #999;
}

/* 空状态提示 */
.empty-tip {
	flex: 1;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
}

.empty-icon {
	width: 300rpx;
	height: 300rpx;
	margin-bottom: 40rpx;
}

.empty-text {
	font-size: 28rpx;
	color: #999;
}

/* 内容区 */
.content {
	flex: 1;
	display: flex;
	overflow: hidden;
}

/* 左侧分类 */
.category-list {
	width: 180rpx;
	background-color: #f8f9fa;
	overflow-y: auto;
}

.category-item {
	padding: 32rpx 20rpx;
	text-align: center;
	background-color: #f8f9fa;
	position: relative;
	transition: all 0.3s;
	margin: 8rpx 0;
}

.category-item.active {
	background-color: #fff;
	border-radius: 0 24rpx 24rpx 0;
	box-shadow: 2rpx 0 8rpx rgba(60, 197, 31, 0.1);
}

.category-item.active::before {
	content: '';
	position: absolute;
	left: 0;
	top: 50%;
	transform: translateY(-50%);
	width: 6rpx;
	height: 40rpx;
	background: linear-gradient(180deg, #94d888 0%, #7bc46f 100%);
	border-radius: 0 6rpx 6rpx 0;
}

.category-name {
	font-size: 28rpx;
	color: #666;
	transition: all 0.3s;
}

.category-item.active .category-name {
	color: #94d888;
	font-weight: 600;
}

/* 右侧商品列表 */
.product-list {
	flex: 1;
	background-color: #fff;
	padding: 20rpx;
}

.no-product {
	padding: 100rpx;
	text-align: center;
	color: #999;
}

.product-item {
	display: flex;
	padding: 24rpx 20rpx;
	margin-bottom: 16rpx;
	border-radius: 16rpx;
	background-color: #fafafa;
	transition: all 0.3s;
}

.product-item:active {
	background-color: #f0f0f0;
	transform: scale(0.98);
}

.product-img {
	width: 160rpx;
	height: 160rpx;
	border-radius: 16rpx;
	margin-right: 20rpx;
	box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.08);
}

.product-info {
	flex: 1;
	display: flex;
	flex-direction: column;
	justify-content: space-between;
}

.product-name {
	font-size: 30rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 8rpx;
}

.product-subtitle {
	font-size: 24rpx;
	color: #999;
	margin-bottom: 12rpx;
}

.product-bottom {
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.price-box {
	display: flex;
	align-items: baseline;
}

.price {
	font-size: 34rpx;
	font-weight: 700;
	color: #ff4d4f;
}

.origin-price {
	font-size: 24rpx;
	color: #999;
	text-decoration: line-through;
	margin-left: 10rpx;
}

.cart-control {
	display: flex;
	align-items: center;
}

.control-btn {
	width: 52rpx;
	height: 52rpx;
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 36rpx;
	font-weight: bold;
	transition: all 0.3s;
	box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
}

.control-btn.minus {
	background-color: #f5f5f5;
	color: #666;
}

.control-btn.minus:active {
	background-color: #e8e8e8;
	transform: scale(0.9);
}

.control-btn.plus {
	background: linear-gradient(135deg, #94d888 0%, #7bc46f 100%);
	color: #fff;
}

.control-btn.plus:active {
	transform: scale(0.9);
}

.count {
	margin: 0 20rpx;
	font-size: 28rpx;
	color: #333;
	min-width: 40rpx;
	text-align: center;
}

/* 底部购物车栏 */
.cart-bar {
	display: flex;
	justify-content: space-between;
	align-items: center;
	background: linear-gradient(135deg, #fff 0%, #fafafa 100%);
	padding: 20rpx 24rpx;
	box-shadow: 0 -8rpx 24rpx rgba(0, 0, 0, 0.08);
	border-top: 1rpx solid #f0f0f0;
}

.cart-left {
	display: flex;
	align-items: center;
	flex: 1;
	gap: 20rpx;
}

.cart-icon-wrapper {
	position: relative;
	width: 88rpx;
	height: 88rpx;
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	box-shadow: 0 6rpx 20rpx rgba(148, 216, 136, 0.35);
	flexShrink: 0;
}

.cart-icon {
	font-size: 48rpx;
}

.cart-badge {
	position: absolute;
	top: -4rpx;
	right: -4rpx;
	background: linear-gradient(135deg, #ff5252 0%, #f44336 100%);
	color: #fff;
	font-size: 20rpx;
	font-weight: 600;
	padding: 4rpx 10rpx;
	border-radius: 24rpx;
	min-width: 32rpx;
	text-align: center;
	box-shadow: 0 4rpx 12rpx rgba(244, 67, 54, 0.4);
	border: 2rpx solid #fff;
	height: 32rpx;
	line-height: 24rpx;
}

.price-info {
	display: flex;
	flex-direction: column;
	gap: 6rpx;
	flex: 1;
}

.price-row {
	display: flex;
	align-items: baseline;
	gap: 8rpx;
}

.price-label {
	font-size: 24rpx;
	color: #666;
	font-weight: 500;
}

.price-value {
	font-size: 40rpx;
	font-weight: 700;
	color: #ff4d4f;
	letter-spacing: 0;
	line-height: 1;
}

.price-tip {
	font-size: 22rpx;
	color: #999;
	padding: 4rpx 12rpx;
	border-radius: 8rpx;
	display: inline-block;
	align-self: flex-start;
	border: 1rpx solid #ffd666;
}

.checkout-btn {
	background: linear-gradient(135deg, #94d888 0%, #7bc46f 100%);
	color: #fff;
	padding: 28rpx 48rpx;
	border-radius: 60rpx;
	font-size: 32rpx;
	font-weight: 700;
	box-shadow: 0 6rpx 20rpx rgba(148, 216, 136, 0.35);
	transition: all 0.3s;
	letter-spacing: 2rpx;
	flexShrink: 0;
}

.checkout-btn:active {
	transform: scale(0.96);
	box-shadow: 0 4rpx 16rpx rgba(148, 216, 136, 0.4);
}

/* 门店选择弹窗 */
.popup-mask {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background-color: rgba(0, 0, 0, 0.5);
	z-index: 999;
	display: flex;
	align-items: flex-end;
}

.store-popup {
	width: 100%;
	background-color: #fff;
	border-radius: 30rpx 30rpx 0 0;
	max-height: 80vh;
	display: flex;
	flex-direction: column;
}

.popup-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 30rpx;
	border-bottom: 2rpx solid #eee;
}

.popup-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
}

.popup-close {
	font-size: 60rpx;
	color: #999;
	line-height: 1;
}

.store-list {
	flex: 1;
	padding: 20rpx;
}

.store-item {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 30rpx;
	margin-bottom: 20rpx;
	background-color: #f5f5f5;
	border-radius: 12rpx;
}

.store-item.selected {
	background-color: #e8f5e9;
	border: 2rpx solid #94d888;
}

.store-main {
	flex: 1;
	display: flex;
	flex-direction: column;
}

.store-item .store-name {
	font-size: 30rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 10rpx;
}

.store-item .store-address {
	font-size: 26rpx;
	color: #666;
	margin-bottom: 8rpx;
}

.store-time {
	font-size: 24rpx;
	color: #999;
}

.check-icon {
	font-size: 40rpx;
	color: #94d888;
	font-weight: bold;
}
</style>
