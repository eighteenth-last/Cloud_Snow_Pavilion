<template>
	<view class="container">
		<!-- 购物车列表 -->
		<view v-if="cartList.length > 0" class="cart-content">
			<view class="cart-list">
				<view 
					v-for="item in cartList" 
					:key="item.id"
					class="cart-item"
				>
					<view 
						:class="['checkbox', item.checked ? 'checked' : '']"
						@click="toggleItem(item.id)"
					>
						<text v-if="item.checked" class="check-icon">✓</text>
					</view>

					<image :src="item.img || '/static/logo.png'" class="product-image" mode="aspectFill" />

					<view class="product-info">
						<text class="product-name">{{ item.name }}</text>
						<text class="product-spec" v-if="item.spec">{{ item.spec }}</text>
						<view class="product-footer">
							<text class="product-price">¥{{ item.price }}</text>
							<view class="quantity-control">
								<view class="btn-minus" @click="decrease(item.id)">-</view>
								<text class="quantity">{{ item.quantity }}</text>
								<view class="btn-plus" @click="increase(item.id)">+</view>
							</view>
						</view>
					</view>
				</view>
			</view>

			<!-- 推荐商品 -->
			<view class="recommend-section">
				<view class="section-title">为你推荐</view>
				<view class="recommend-list">
					<view 
						v-for="product in recommendList" 
						:key="product.id"
						class="recommend-item"
						@click="goToDetail(product.id)"
					>
						<image :src="product.img || '/static/logo.png'" class="recommend-image" mode="aspectFill" />
						<text class="recommend-name">{{ product.name }}</text>
						<view class="recommend-footer">
							<text class="recommend-price">¥{{ product.price }}</text>
							<view class="btn-add">+</view>
						</view>
					</view>
				</view>
			</view>
		</view>

		<!-- 空购物车 -->
		<view v-else class="empty-cart">
			<text class="empty-icon">🛒</text>
			<text class="empty-text">购物车空空如也</text>
			<view class="btn-go-shop" @click="goToIndex">去逛逛</view>
		</view>

		<!-- 底部结算栏 -->
		<view v-if="cartList.length > 0" class="bottom-bar">
			<view 
				:class="['checkbox', allChecked ? 'checked' : '']"
				@click="toggleAll"
			>
				<text v-if="allChecked" class="check-icon">✓</text>
			</view>
			<text class="all-text">全选</text>

			<view class="price-info">
				<text class="price-label">合计：</text>
				<text class="total-price">¥{{ totalPrice.toFixed(2) }}</text>
			</view>

			<view 
				:class="['btn-checkout', checkedCount > 0 ? '' : 'disabled']"
				@click="checkout"
			>
				去结算({{ checkedCount }})
			</view>
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			cartList: [],
			recommendList: []
		}
	},
	computed: {
		allChecked() {
			return this.cartList.length > 0 && this.cartList.every(item => item.checked)
		},
		checkedCount() {
			return this.cartList.filter(item => item.checked).length
		},
		totalPrice() {
			return this.cartList
				.filter(item => item.checked)
				.reduce((sum, item) => sum + item.price * item.quantity, 0)
		}
	},
	onShow() {
		this.loadCart()
		this.loadRecommend()
	},
	methods: {
		loadCart() {
			// 从本地存储加载购物车
			const cart = uni.getStorageSync('cart') || []
			this.cartList = cart.map(item => ({
				...item,
				checked: true
			}))
		},
		saveCart() {
			uni.setStorageSync('cart', this.cartList)
		},
		async loadRecommend() {
			try {
				const res = await uni.request({
					url: 'http://localhost:8080/api/product/recommend',
					method: 'GET'
				})
				if (res.data.code === 200) {
					this.recommendList = res.data.data || []
				}
			} catch (error) {
				console.error('加载推荐商品失败', error)
			}
		},
		toggleItem(id) {
			const item = this.cartList.find(i => i.id === id)
			if (item) {
				item.checked = !item.checked
			}
		},
		toggleAll() {
			const checked = !this.allChecked
			this.cartList.forEach(item => {
				item.checked = checked
			})
		},
		decrease(id) {
			const item = this.cartList.find(i => i.id === id)
			if (item && item.quantity > 1) {
				item.quantity--
				this.saveCart()
			}
		},
		increase(id) {
			const item = this.cartList.find(i => i.id === id)
			if (item) {
				item.quantity++
				this.saveCart()
			}
		},
		goToDetail(id) {
			uni.navigateTo({
				url: `/pages/product/detail?id=${id}`
			})
		},
		goToIndex() {
			uni.switchTab({
				url: '/pages/index/index'
			})
		},
		checkout() {
			if (this.checkedCount === 0) {
				return
			}

			const checkedItems = this.cartList.filter(item => item.checked)
			
			// 保存选中的商品到临时存储
			uni.setStorageSync('checkoutItems', checkedItems)

			uni.navigateTo({
				url: '/pages/order/confirm'
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

.cart-content {
	padding: 20rpx 0;
}

.cart-list {
	background-color: #fff;
	padding: 0 30rpx;
}

.cart-item {
	display: flex;
	align-items: center;
	padding: 30rpx 0;
	border-bottom: 2rpx solid #f5f5f5;
}

.cart-item:last-child {
	border-bottom: none;
}

.checkbox {
	width: 40rpx;
	height: 40rpx;
	border: 2rpx solid #ccc;
	border-radius: 50%;
	margin-right: 20rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

.checkbox.checked {
	background-color: #3cc51f;
	border-color: #3cc51f;
}

.check-icon {
	color: #fff;
	font-size: 28rpx;
	font-weight: bold;
}

.product-image {
	width: 160rpx;
	height: 160rpx;
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
	font-size: 30rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 10rpx;
}

.product-spec {
	font-size: 24rpx;
	color: #999;
	margin-bottom: 10rpx;
}

.product-footer {
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.product-price {
	font-size: 32rpx;
	color: #ff6b6b;
	font-weight: bold;
}

.quantity-control {
	display: flex;
	align-items: center;
}

.btn-minus,
.btn-plus {
	width: 50rpx;
	height: 50rpx;
	border: 2rpx solid #e5e5e5;
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 32rpx;
	color: #666;
}

.quantity {
	margin: 0 25rpx;
	font-size: 28rpx;
	font-weight: bold;
	min-width: 40rpx;
	text-align: center;
}

.recommend-section {
	margin-top: 20rpx;
	background-color: #fff;
	padding: 30rpx;
}

.section-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 30rpx;
}

.recommend-list {
	display: flex;
	flex-wrap: wrap;
	gap: 20rpx;
}

.recommend-item {
	width: 320rpx;
	background-color: #f5f5f5;
	border-radius: 15rpx;
	padding: 20rpx;
}

.recommend-image {
	width: 100%;
	height: 280rpx;
	border-radius: 10rpx;
	margin-bottom: 15rpx;
}

.recommend-name {
	display: block;
	font-size: 28rpx;
	color: #333;
	margin-bottom: 15rpx;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}

.recommend-footer {
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.recommend-price {
	font-size: 32rpx;
	color: #ff6b6b;
	font-weight: bold;
}

.btn-add {
	width: 50rpx;
	height: 50rpx;
	background-color: #3cc51f;
	color: #fff;
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 36rpx;
}

.empty-cart {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding: 200rpx 0;
}

.empty-icon {
	font-size: 120rpx;
	margin-bottom: 40rpx;
}

.empty-text {
	font-size: 28rpx;
	color: #999;
	margin-bottom: 60rpx;
}

.btn-go-shop {
	background-color: #3cc51f;
	color: #fff;
	padding: 25rpx 80rpx;
	border-radius: 50rpx;
	font-size: 28rpx;
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

.all-text {
	font-size: 28rpx;
	color: #333;
	margin-right: auto;
	margin-left: 15rpx;
}

.price-info {
	margin-right: 30rpx;
}

.price-label {
	font-size: 28rpx;
	color: #666;
}

.total-price {
	font-size: 36rpx;
	color: #ff6b6b;
	font-weight: bold;
}

.btn-checkout {
	background-color: #3cc51f;
	color: #fff;
	padding: 25rpx 50rpx;
	border-radius: 50rpx;
	font-size: 28rpx;
}

.btn-checkout.disabled {
	background-color: #ccc;
}
</style>
