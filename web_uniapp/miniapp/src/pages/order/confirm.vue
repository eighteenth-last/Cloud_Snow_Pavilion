<template>
	<view class="container">
		<!-- 门店信息 -->
		<view class="store-section">
			<view class="section-header">
				<text class="header-title">门店信息</text>
			</view>
			<view class="store-info">
				<text class="store-name">{{ store.storeName || '请选择门店' }}</text>
				<text class="store-address">{{ store.address }}</text>
			</view>
			<view class="order-type">
				<view 
					:class="['type-btn', orderType === 'take' ? 'active' : '']"
					@click="selectType('take')"
				>
					到店自取
				</view>
				<view 
					:class="['type-btn', orderType === 'delivery' ? 'active' : '']"
					@click="selectType('delivery')"
				>
					外卖配送
				</view>
			</view>
		</view>

		<!-- 配送地址 -->
		<view v-if="orderType === 'delivery'" class="address-section">
			<view class="section-header">
				<text class="header-title">配送地址</text>
				<text class="add-address" @click="addAddress">+ 新增地址</text>
			</view>
			<view v-if="currentAddress" class="address-info">
				<view class="address-top">
					<text class="receiver">{{ currentAddress.receiver }}</text>
					<text class="mobile">{{ currentAddress.mobile }}</text>
				</view>
				<text class="address-detail">{{ currentAddress.address }}</text>
			</view>
			<view v-else class="no-address" @click="addAddress">
				<text>请添加收货地址</text>
			</view>
		</view>

		<!-- 商品列表 -->
		<view class="goods-section">
			<view class="section-header">
				<text class="header-title">商品清单</text>
			</view>
			<view 
				v-for="item in goodsList" 
				:key="item.id"
				class="goods-item"
			>
				<image :src="item.img || '/static/logo.png'" class="goods-image" mode="aspectFill" />
				<view class="goods-info">
					<text class="goods-name">{{ item.name }}</text>
					<text class="goods-spec" v-if="item.spec">{{ item.spec }}</text>
				</view>
				<view class="goods-right">
					<text class="goods-price">¥{{ item.price }}</text>
					<text class="goods-quantity">x{{ item.quantity }}</text>
				</view>
			</view>
		</view>

		<!-- 优惠券 -->
		<view class="coupon-section" @click="selectCoupon">
			<text class="coupon-label">优惠券</text>
			<view class="coupon-right">
				<text class="coupon-text">{{ couponText }}</text>
				<text class="arrow">></text>
			</view>
		</view>

		<!-- 备注 -->
		<view class="remark-section">
			<textarea 
				class="remark-input" 
				v-model="remark" 
				placeholder="选填，可以告诉商家您的特殊需求"
				maxlength="100"
			/>
		</view>

		<!-- 金额明细 -->
		<view class="amount-section">
			<view class="amount-item">
				<text class="amount-label">商品金额</text>
				<text class="amount-value">¥{{ goodsAmount.toFixed(2) }}</text>
			</view>
			<view class="amount-item" v-if="orderType === 'delivery'">
				<text class="amount-label">配送费</text>
				<text class="amount-value">¥{{ deliveryFee.toFixed(2) }}</text>
			</view>
			<view class="amount-item" v-if="couponDiscount > 0">
				<text class="amount-label">优惠券</text>
				<text class="amount-value discount">-¥{{ couponDiscount.toFixed(2) }}</text>
			</view>
		</view>

		<!-- 底部结算栏 -->
		<view class="bottom-bar">
			<view class="total-info">
				<text class="total-label">合计：</text>
				<text class="total-price">¥{{ totalAmount.toFixed(2) }}</text>
			</view>
			<view class="btn-submit" @click="submitOrder">
				提交订单
			</view>
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			orderType: 'take', // take: 自取, delivery: 外卖
			store: {},
			currentAddress: null,
			goodsList: [],
			selectedCoupon: null,
			remark: '',
			deliveryFee: 5.00
		}
	},
	computed: {
		goodsAmount() {
			return this.goodsList.reduce((sum, item) => sum + item.price * item.quantity, 0)
		},
		couponDiscount() {
			if (!this.selectedCoupon) return 0
			if (this.selectedCoupon.type === 'discount') {
				return this.goodsAmount * (1 - this.selectedCoupon.discount / 10)
			} else {
				return this.selectedCoupon.amount
			}
		},
		totalAmount() {
			let amount = this.goodsAmount
			if (this.orderType === 'delivery') {
				amount += this.deliveryFee
			}
			amount -= this.couponDiscount
			return Math.max(amount, 0)
		},
		couponText() {
			if (this.selectedCoupon) {
				return `已选 -¥${this.couponDiscount.toFixed(2)}`
			}
			return '选择优惠券'
		}
	},
	onLoad() {
		this.loadCheckoutData()
		this.loadDefaultStore()
	},
	methods: {
		loadCheckoutData() {
			// 从临时存储获取结算商品
			const items = uni.getStorageSync('checkoutItems') || []
			this.goodsList = items
		},
		async loadDefaultStore() {
			try {
				const res = await uni.request({
					url: 'http://localhost:8080/api/store/list',
					method: 'GET'
				})
				if (res.data.code === 200 && res.data.data.length > 0) {
					this.store = res.data.data[0]
				}
			} catch (error) {
				console.error('加载门店信息失败', error)
			}
		},
		selectType(type) {
			this.orderType = type
		},
		addAddress() {
			uni.showToast({
				title: '地址管理功能开发中',
				icon: 'none'
			})
		},
		selectCoupon() {
			uni.showToast({
				title: '优惠券选择功能开发中',
				icon: 'none'
			})
		},
		async submitOrder() {
			if (!this.store.storeId) {
				uni.showToast({
					title: '请选择门店',
					icon: 'none'
				})
				return
			}

			if (this.orderType === 'delivery' && !this.currentAddress) {
				uni.showToast({
					title: '请添加收货地址',
					icon: 'none'
				})
				return
			}

			if (this.goodsList.length === 0) {
				uni.showToast({
					title: '请选择商品',
					icon: 'none'
				})
				return
			}

			try {
				const token = uni.getStorageSync('token')
				if (!token) {
					uni.showModal({
						title: '提示',
						content: '请先登录',
						success: (res) => {
							if (res.confirm) {
								uni.navigateTo({
									url: '/pages/login/login'
								})
							}
						}
					})
					return
				}

				uni.showLoading({
					title: '提交中...'
				})

				const orderData = {
					storeId: this.store.storeId,
					orderType: this.orderType,
					addressId: this.currentAddress?.id,
					items: this.goodsList.map(item => ({
						productId: item.id,
						quantity: item.quantity,
						price: item.price
					})),
					couponId: this.selectedCoupon?.id,
					remark: this.remark,
					amount: this.totalAmount
				}

				const res = await uni.request({
					url: 'http://localhost:8080/api/order/create',
					method: 'POST',
					header: {
						'Authorization': `Bearer ${token}`
					},
					data: orderData
				})

				uni.hideLoading()

				if (res.data.code === 200) {
					const orderId = res.data.data.orderId
					
					uni.showToast({
						title: '下单成功',
						icon: 'success'
					})

					// 清空购物车中已结算的商品
					const cart = uni.getStorageSync('cart') || []
					const newCart = cart.filter(item => 
						!this.goodsList.some(goods => goods.id === item.id)
					)
					uni.setStorageSync('cart', newCart)

					// 跳转到订单详情或支付页面
					setTimeout(() => {
						uni.redirectTo({
							url: `/pages/order/detail?id=${orderId}`
						})
					}, 1500)
				} else {
					uni.showToast({
						title: res.data.msg || '下单失败',
						icon: 'none'
					})
				}
			} catch (error) {
				uni.hideLoading()
				console.error('提交订单失败', error)
				uni.showToast({
					title: '提交订单失败',
					icon: 'none'
				})
			}
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

.section-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 30rpx;
	border-bottom: 2rpx solid #f5f5f5;
}

.header-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
}

.add-address {
	font-size: 28rpx;
	color: #3cc51f;
}

.store-section {
	background-color: #fff;
	margin-bottom: 20rpx;
}

.store-info {
	padding: 30rpx;
	display: flex;
	flex-direction: column;
}

.store-name {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 10rpx;
}

.store-address {
	font-size: 26rpx;
	color: #999;
}

.order-type {
	display: flex;
	padding: 0 30rpx 30rpx;
	gap: 20rpx;
}

.type-btn {
	flex: 1;
	text-align: center;
	padding: 20rpx 0;
	border: 2rpx solid #e5e5e5;
	border-radius: 10rpx;
	font-size: 28rpx;
	color: #666;
}

.type-btn.active {
	border-color: #3cc51f;
	color: #3cc51f;
	background-color: rgba(60, 197, 31, 0.05);
}

.address-section {
	background-color: #fff;
	margin-bottom: 20rpx;
}

.address-info {
	padding: 30rpx;
}

.address-top {
	display: flex;
	align-items: center;
	margin-bottom: 15rpx;
}

.receiver {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
	margin-right: 30rpx;
}

.mobile {
	font-size: 28rpx;
	color: #666;
}

.address-detail {
	font-size: 28rpx;
	color: #666;
	line-height: 1.5;
}

.no-address {
	padding: 60rpx 30rpx;
	text-align: center;
	color: #999;
	font-size: 28rpx;
}

.goods-section {
	background-color: #fff;
	margin-bottom: 20rpx;
}

.goods-item {
	display: flex;
	align-items: center;
	padding: 30rpx;
	border-bottom: 2rpx solid #f5f5f5;
}

.goods-item:last-child {
	border-bottom: none;
}

.goods-image {
	width: 120rpx;
	height: 120rpx;
	border-radius: 10rpx;
	margin-right: 20rpx;
}

.goods-info {
	flex: 1;
	display: flex;
	flex-direction: column;
}

.goods-name {
	font-size: 28rpx;
	color: #333;
	margin-bottom: 10rpx;
}

.goods-spec {
	font-size: 24rpx;
	color: #999;
}

.goods-right {
	display: flex;
	flex-direction: column;
	align-items: flex-end;
}

.goods-price {
	font-size: 28rpx;
	color: #ff6b6b;
	font-weight: bold;
	margin-bottom: 10rpx;
}

.goods-quantity {
	font-size: 24rpx;
	color: #999;
}

.coupon-section {
	display: flex;
	justify-content: space-between;
	align-items: center;
	background-color: #fff;
	padding: 30rpx;
	margin-bottom: 20rpx;
}

.coupon-label {
	font-size: 28rpx;
	color: #333;
}

.coupon-right {
	display: flex;
	align-items: center;
}

.coupon-text {
	font-size: 28rpx;
	color: #999;
	margin-right: 10rpx;
}

.arrow {
	font-size: 28rpx;
	color: #999;
}

.remark-section {
	background-color: #fff;
	padding: 30rpx;
	margin-bottom: 20rpx;
}

.remark-input {
	width: 100%;
	min-height: 150rpx;
	font-size: 28rpx;
	color: #333;
}

.amount-section {
	background-color: #fff;
	padding: 30rpx;
}

.amount-item {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 20rpx;
}

.amount-item:last-child {
	margin-bottom: 0;
}

.amount-label {
	font-size: 28rpx;
	color: #666;
}

.amount-value {
	font-size: 28rpx;
	color: #333;
}

.amount-value.discount {
	color: #ff6b6b;
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
	justify-content: space-between;
	box-shadow: 0 -4rpx 20rpx rgba(0, 0, 0, 0.1);
}

.total-info {
	display: flex;
	align-items: baseline;
}

.total-label {
	font-size: 28rpx;
	color: #666;
}

.total-price {
	font-size: 40rpx;
	color: #ff6b6b;
	font-weight: bold;
}

.btn-submit {
	background-color: #3cc51f;
	color: #fff;
	padding: 25rpx 80rpx;
	border-radius: 50rpx;
	font-size: 28rpx;
}
</style>
