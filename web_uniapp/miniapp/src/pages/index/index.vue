<template>
	<view class="container">
		<!-- 搜索栏 -->
		<view class="search-bar">
			<input class="search-input" placeholder="搜索商品" />
		</view>

		<!-- 分类标签 -->
		<view class="category-tabs">
			<view 
				v-for="(item, index) in categories" 
				:key="index"
				:class="['tab-item', currentCategory === index ? 'active' : '']"
				@click="selectCategory(index)"
			>
				{{ item.name }}
			</view>
		</view>

		<!-- 商品列表 -->
		<view class="product-list">
			<view 
				v-for="product in productList" 
				:key="product.id"
				class="product-item"
				@click="goToDetail(product.id)"
			>
				<image :src="product.img || '/static/logo.png'" class="product-image" mode="aspectFill" />
				<view class="product-info">
					<text class="product-name">{{ product.name }}</text>
					<text class="product-subtitle">{{ product.subTitle }}</text>
				<view class="product-footer">
					<text class="product-price">¥{{ product.price }}</text>
					<view class="add-btn" @click.stop="addToCart(product)">+</view>
				</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
import { productApi } from '@/api/index.js'

export default {
	data() {
		return {
			currentCategory: 0,
			categories: [
				{ name: '全部', id: null },
				{ name: '咖啡', id: 1 },
				{ name: '奶茶', id: 2 },
				{ name: '果汁', id: 3 }
			],
			productList: []
		}
	},
	onLoad() {
		this.loadProducts()
	},
	methods: {
		selectCategory(index) {
			this.currentCategory = index
			this.loadProducts()
		},
		async loadProducts() {
			try {
				const categoryId = this.categories[this.currentCategory].id
				const res = await productApi.getList(categoryId)
				this.productList = res.data || []
			} catch (error) {
				console.error('加载商品失败', error)
			}
		},
		goToDetail(id) {
			uni.navigateTo({
				url: `/pages/product/detail?id=${id}`
			})
		},
		addToCart(product) {
			if (!product.id) {
				return
			}

			// 获取购物车
			let cart = uni.getStorageSync('cart') || []
			
			// 查找是否已存在
			const existIndex = cart.findIndex(item => item.id === product.id)
			
			if (existIndex > -1) {
				// 已存在，增加数量
				cart[existIndex].quantity += 1
			} else {
				// 不存在，添加新商品
				cart.push({
					id: product.id,
					name: product.name,
					img: product.img,
					price: product.price,
					quantity: 1,
					spec: '默认'
				})
			}
			
			// 保存到本地存储
			uni.setStorageSync('cart', cart)
			
			uni.showToast({
				title: '已加入购物车',
				icon: 'success'
			})
		}
	}
}
</script>

<style scoped>
.container {
	background-color: #f5f5f5;
	min-height: 100vh;
}

.search-bar {
	padding: 20rpx 30rpx;
	background-color: #fff;
}

.search-input {
	background-color: #f5f5f5;
	border-radius: 40rpx;
	padding: 15rpx 30rpx;
	font-size: 28rpx;
}

.category-tabs {
	display: flex;
	background-color: #fff;
	padding: 20rpx 30rpx;
	margin-bottom: 20rpx;
}

.tab-item {
	padding: 10rpx 30rpx;
	margin-right: 20rpx;
	border-radius: 30rpx;
	font-size: 28rpx;
	color: #666;
}

.tab-item.active {
	background-color: #3cc51f;
	color: #fff;
}

.product-list {
	padding: 0 30rpx;
}

.product-item {
	display: flex;
	background-color: #fff;
	border-radius: 20rpx;
	margin-bottom: 20rpx;
	padding: 20rpx;
}

.product-image {
	width: 200rpx;
	height: 200rpx;
	border-radius: 15rpx;
	margin-right: 20rpx;
}

.product-info {
	flex: 1;
	display: flex;
	flex-direction: column;
	justify-content: space-between;
}

.product-name {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
}

.product-subtitle {
	font-size: 24rpx;
	color: #999;
	margin-top: 10rpx;
}

.product-footer {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-top: 20rpx;
}

.product-price {
	font-size: 36rpx;
	color: #ff6b6b;
	font-weight: bold;
}

.add-btn {
	width: 60rpx;
	height: 60rpx;
	background-color: #3cc51f;
	color: #fff;
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 40rpx;
}
</style>
