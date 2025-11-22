<template>
	<view class="container">
		<!-- 商品图片 -->
		<image :src="product.img || '/static/logo.png'" class="product-banner" mode="aspectFill" />

		<!-- 商品信息 -->
		<view class="product-info">
			<view class="product-header">
				<text class="product-name">{{ product.name }}</text>
				<text class="product-subtitle">{{ product.subTitle }}</text>
			</view>
			<view class="price-box">
				<text class="current-price">¥{{ product.price }}</text>
				<text v-if="product.originPrice" class="origin-price">¥{{ product.originPrice }}</text>
			</view>
		</view>

		<!-- 规格选择 -->
		<view class="spec-section">
			<view class="section-title">选择规格</view>
			<view class="spec-list">
				<view class="spec-item active">默认</view>
			</view>
		</view>

		<!-- 商品详情 -->
		<view class="detail-section">
			<view class="section-title">商品详情</view>
			<view class="detail-content">
				<text>{{ product.name }}</text>
			</view>
		</view>

		<!-- 底部操作栏 -->
		<view class="bottom-bar">
			<view class="quantity-box">
				<view class="btn-minus" @click="decreaseQuantity">-</view>
				<text class="quantity">{{ quantity }}</text>
				<view class="btn-plus" @click="increaseQuantity">+</view>
			</view>
			<view class="btn-add-cart" @click="addToCart">加入购物车</view>
			<view class="btn-buy-now" @click="buyNow">立即购买</view>
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			productId: null,
			product: {},
			quantity: 1
		}
	},
	onLoad(options) {
		this.productId = options.id
		this.loadProductDetail()
	},
	methods: {
		async loadProductDetail() {
			try {
				const res = await uni.request({
					url: `http://localhost:8080/api/product/${this.productId}`,
					method: 'GET'
				})
				if (res.data.code === 200) {
					this.product = res.data.data || {}
				}
			} catch (error) {
				console.error('加载商品详情失败', error)
			}
		},
		decreaseQuantity() {
			if (this.quantity > 1) {
				this.quantity--
			}
		},
		increaseQuantity() {
			this.quantity++
		},
		addToCart() {
			if (!this.product.productId) {
				return
			}

			// 获取购物车
			let cart = uni.getStorageSync('cart') || []
			
			// 查找是否已存在
			const existIndex = cart.findIndex(item => item.id === this.product.productId)
			
			if (existIndex > -1) {
				// 已存在，增加数量
				cart[existIndex].quantity += this.quantity
			} else {
				// 不存在，添加新商品
				cart.push({
					id: this.product.productId,
					name: this.product.name,
					img: this.product.img,
					price: this.product.price,
					quantity: this.quantity,
					spec: '默认'
				})
			}
			
			// 保存到本地存储
			uni.setStorageSync('cart', cart)
			
			uni.showToast({
				title: '已加入购物车',
				icon: 'success'
			})
		},
		buyNow() {
			uni.showToast({
				title: '立即购买功能开发中',
				icon: 'none'
			})
		}
	}
}
</script>

<style scoped>
.container {
	background-color: #f5f5f5;
	min-height: 100vh;
	padding-bottom: 120rpx;
}

.product-banner {
	width: 100%;
	height: 600rpx;
}

.product-info {
	background-color: #fff;
	padding: 30rpx;
	margin-bottom: 20rpx;
}

.product-header {
	margin-bottom: 20rpx;
}

.product-name {
	font-size: 36rpx;
	font-weight: bold;
	color: #333;
	display: block;
	margin-bottom: 10rpx;
}

.product-subtitle {
	font-size: 28rpx;
	color: #999;
}

.price-box {
	display: flex;
	align-items: baseline;
}

.current-price {
	font-size: 48rpx;
	color: #ff6b6b;
	font-weight: bold;
	margin-right: 20rpx;
}

.origin-price {
	font-size: 28rpx;
	color: #999;
	text-decoration: line-through;
}

.spec-section,
.detail-section {
	background-color: #fff;
	padding: 30rpx;
	margin-bottom: 20rpx;
}

.section-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 20rpx;
}

.spec-list {
	display: flex;
	flex-wrap: wrap;
}

.spec-item {
	padding: 15rpx 40rpx;
	border: 2rpx solid #e5e5e5;
	border-radius: 10rpx;
	margin-right: 20rpx;
	margin-bottom: 20rpx;
	font-size: 28rpx;
	color: #666;
}

.spec-item.active {
	border-color: #3cc51f;
	color: #3cc51f;
}

.detail-content {
	font-size: 28rpx;
	color: #666;
	line-height: 1.6;
}

.bottom-bar {
	position: fixed;
	bottom: 0;
	left: 0;
	right: 0;
	background-color: #fff;
	padding: 20rpx 30rpx;
	display: flex;
	align-items: center;
	box-shadow: 0 -4rpx 20rpx rgba(0, 0, 0, 0.1);
}

.quantity-box {
	display: flex;
	align-items: center;
	margin-right: 20rpx;
}

.btn-minus,
.btn-plus {
	width: 60rpx;
	height: 60rpx;
	border: 2rpx solid #e5e5e5;
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 36rpx;
	color: #666;
}

.quantity {
	margin: 0 30rpx;
	font-size: 32rpx;
	font-weight: bold;
}

.btn-add-cart {
	flex: 1;
	background-color: #ffa500;
	color: #fff;
	text-align: center;
	padding: 25rpx 0;
	border-radius: 50rpx;
	margin-right: 20rpx;
	font-size: 28rpx;
}

.btn-buy-now {
	flex: 1;
	background-color: #3cc51f;
	color: #fff;
	text-align: center;
	padding: 25rpx 0;
	border-radius: 50rpx;
	font-size: 28rpx;
}
</style>
