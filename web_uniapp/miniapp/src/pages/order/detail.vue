<template>
	<view class="container">
		<!-- 订单状态 -->
		<view class="status-section">
			<view :class="['status-icon', getStatusClass(order.status)]">
				{{ getStatusIcon(order.status) }}
			</view>
			<text class="status-text">{{ getStatusText(order.status) }}</text>
			<text class="status-tip" v-if="order.status === 0">请尽快完成支付</text>
		</view>

		<!-- 门店信息 -->
		<view class="store-section" v-if="order.storeId">
			<view class="section-title">
				<text class="title-icon">🏪</text>
				<text>门店信息</text>
			</view>
			<view class="store-info">
				<text class="store-name">{{ store.storeName }}</text>
				<text class="store-address">{{ store.address }}</text>
			</view>
		</view>

		<!-- 配送地址 -->
		<view class="address-section" v-if="order.orderType === 'delivery'">
			<view class="section-title">
				<text class="title-icon">📍</text>
				<text>配送地址</text>
			</view>
			<view class="address-info" v-if="address">
				<view class="address-top">
					<text class="receiver">{{ address.receiver }}</text>
					<text class="mobile">{{ address.mobile }}</text>
				</view>
				<text class="address-detail">{{ address.address }}</text>
			</view>
		</view>

		<!-- 商品清单 -->
		<view class="goods-section">
			<view class="section-title">
				<text class="title-icon">📦</text>
				<text>商品清单</text>
			</view>
			<view 
				v-for="item in orderItems" 
				:key="item.id"
				class="goods-item"
			>
				<image :src="item.img || '/static/logo.png'" class="goods-image" mode="aspectFill" />
				<view class="goods-info">
					<text class="goods-name">{{ item.productName }}</text>
					<text class="goods-spec" v-if="item.spec">{{ item.spec }}</text>
				</view>
				<view class="goods-right">
					<text class="goods-price">¥{{ item.price }}</text>
					<text class="goods-quantity">x{{ item.quantity }}</text>
				</view>
			</view>
		</view>

		<!-- 订单信息 -->
		<view class="order-section">
			<view class="section-title">
				<text class="title-icon">📋</text>
				<text>订单信息</text>
			</view>
			<view class="order-info-list">
				<view class="info-item">
					<text class="info-label">订单编号</text>
					<text class="info-value">{{ order.orderId }}</text>
				</view>
				<view class="info-item">
					<text class="info-label">下单时间</text>
					<text class="info-value">{{ order.createTime }}</text>
				</view>
				<view class="info-item" v-if="order.payTime">
					<text class="info-label">支付时间</text>
					<text class="info-value">{{ order.payTime }}</text>
				</view>
				<view class="info-item" v-if="order.remark">
					<text class="info-label">订单备注</text>
					<text class="info-value">{{ order.remark }}</text>
				</view>
			</view>
		</view>

		<!-- 费用明细 -->
		<view class="amount-section">
			<view class="section-title">
				<text class="title-icon">💰</text>
				<text>费用明细</text>
			</view>
			<view class="amount-list">
				<view class="amount-item">
					<text class="amount-label">商品金额</text>
					<text class="amount-value">¥{{ order.originalAmount || order.amount }}</text>
				</view>
				<view class="amount-item" v-if="order.deliveryFee > 0">
					<text class="amount-label">配送费</text>
					<text class="amount-value">¥{{ order.deliveryFee }}</text>
				</view>
				<view class="amount-item" v-if="order.couponDiscount > 0">
					<text class="amount-label">优惠券</text>
					<text class="amount-value discount">-¥{{ order.couponDiscount }}</text>
				</view>
				<view class="amount-item total">
					<text class="amount-label">实付金额</text>
					<text class="amount-value total-price">¥{{ order.amount }}</text>
				</view>
			</view>
		</view>

		<!-- 底部操作栏 -->
		<view class="bottom-bar">
			<view class="btn-cancel" v-if="order.status === 0" @click="cancelOrder">
				取消订单
			</view>
			<view class="btn-pay" v-if="order.status === 0" @click="payOrder">
				去支付
			</view>
			<view class="btn-contact" @click="contactService">
				联系客服
			</view>
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			orderId: null,
			order: {},
			orderItems: [],
			store: {},
			address: null
		}
	},
	onLoad(options) {
		this.orderId = options.id
		this.loadOrderDetail()
	},
	methods: {
		async loadOrderDetail() {
			try {
				const token = uni.getStorageSync('token')
				if (!token) {
					uni.showToast({
						title: '请先登录',
						icon: 'none'
					})
					return
				}

				uni.showLoading({
					title: '加载中...'
				})

				const res = await uni.request({
					url: `http://localhost:8080/api/order/${this.orderId}`,
					method: 'GET',
					header: {
						'Authorization': `Bearer ${token}`
					}
				})

				uni.hideLoading()

				if (res.data.code === 200) {
					this.order = res.data.data.order || {}
					this.orderItems = res.data.data.items || []
					
					// 加载门店信息
					if (this.order.storeId) {
						this.loadStore(this.order.storeId)
					}

					// 加载地址信息
					if (this.order.addressId) {
						this.loadAddress(this.order.addressId)
					}
				} else {
					uni.showToast({
						title: res.data.msg || '加载失败',
						icon: 'none'
					})
				}
			} catch (error) {
				uni.hideLoading()
				console.error('加载订单详情失败', error)
			}
		},
		async loadStore(storeId) {
			try {
				const res = await uni.request({
					url: `http://localhost:8080/api/store/${storeId}`,
					method: 'GET'
				})
				if (res.data.code === 200) {
					this.store = res.data.data || {}
				}
			} catch (error) {
				console.error('加载门店信息失败', error)
			}
		},
		async loadAddress(addressId) {
			try {
				const token = uni.getStorageSync('token')
				const res = await uni.request({
					url: `http://localhost:8080/api/address/${addressId}`,
					method: 'GET',
					header: {
						'Authorization': `Bearer ${token}`
					}
				})
				if (res.data.code === 200) {
					this.address = res.data.data || {}
				}
			} catch (error) {
				console.error('加载地址信息失败', error)
			}
		},
		getStatusText(status) {
			const map = {
				0: '待支付',
				1: '已支付',
				2: '制作中',
				3: '已完成',
				5: '已取消'
			}
			return map[status] || '未知'
		},
		getStatusClass(status) {
			const map = {
				0: 'status-wait',
				1: 'status-paid',
				2: 'status-making',
				3: 'status-done',
				5: 'status-cancel'
			}
			return map[status] || ''
		},
		getStatusIcon(status) {
			const map = {
				0: '⏰',
				1: '✅',
				2: '🔧',
				3: '✨',
				5: '❌'
			}
			return map[status] || '❓'
		},
		async cancelOrder() {
			const res = await uni.showModal({
				title: '提示',
				content: '确定要取消订单吗？'
			})
			if (res.confirm) {
				try {
					const token = uni.getStorageSync('token')
					const result = await uni.request({
						url: `http://localhost:8080/api/order/${this.orderId}/cancel`,
						method: 'POST',
						header: {
							'Authorization': `Bearer ${token}`
						}
					})
					if (result.data.code === 200) {
						uni.showToast({
							title: '订单已取消',
							icon: 'success'
						})
						this.loadOrderDetail()
					}
				} catch (error) {
					console.error('取消订单失败', error)
				}
			}
		},
		payOrder() {
			uni.showToast({
				title: '支付功能开发中',
				icon: 'none'
			})
		},
		contactService() {
			uni.showToast({
				title: '客服功能开发中',
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

.status-section {
	background-color: #fff;
	padding: 60rpx 30rpx;
	text-align: center;
	margin-bottom: 20rpx;
}

.status-icon {
	font-size: 100rpx;
	margin-bottom: 20rpx;
}

.status-icon.status-wait {
	color: #ff9800;
}

.status-icon.status-paid {
	color: #94d888;
}

.status-icon.status-making {
	color: #2196f3;
}

.status-icon.status-done {
	color: #999;
}

.status-icon.status-cancel {
	color: #f44336;
}

.status-text {
	display: block;
	font-size: 36rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 10rpx;
}

.status-tip {
	display: block;
	font-size: 24rpx;
	color: #999;
}

.section-title {
	display: flex;
	align-items: center;
	padding: 30rpx;
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
	border-bottom: 2rpx solid #f5f5f5;
}

.title-icon {
	font-size: 36rpx;
	margin-right: 15rpx;
}

.store-section,
.address-section,
.goods-section,
.order-section,
.amount-section {
	background-color: #fff;
	margin-bottom: 20rpx;
}

.store-info {
	padding: 30rpx;
	display: flex;
	flex-direction: column;
}

.store-name {
	font-size: 30rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 10rpx;
}

.store-address {
	font-size: 26rpx;
	color: #999;
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
	font-size: 30rpx;
	font-weight: bold;
	color: #333;
	margin-right: 30rpx;
}

.mobile {
	font-size: 28rpx;
	color: #666;
}

.address-detail {
	font-size: 26rpx;
	color: #666;
	line-height: 1.5;
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

.order-info-list,
.amount-list {
	padding: 30rpx;
}

.info-item,
.amount-item {
	display: flex;
	justify-content: space-between;
	align-items: flex-start;
	margin-bottom: 25rpx;
}

.info-item:last-child,
.amount-item:last-child {
	margin-bottom: 0;
}

.info-label,
.amount-label {
	font-size: 28rpx;
	color: #666;
}

.info-value {
	font-size: 28rpx;
	color: #333;
	text-align: right;
	flex: 1;
	margin-left: 40rpx;
}

.amount-value {
	font-size: 28rpx;
	color: #333;
}

.amount-value.discount {
	color: #ff6b6b;
}

.amount-item.total {
	padding-top: 20rpx;
	border-top: 2rpx solid #f5f5f5;
	margin-top: 10rpx;
}

.amount-item.total .amount-label {
	font-size: 30rpx;
	font-weight: bold;
}

.total-price {
	font-size: 36rpx;
	color: #ff6b6b;
	font-weight: bold;
}

.bottom-bar {
	position: fixed;
	bottom: 0;
	left: 0;
	right: 0;
	background-color: #fff;
	padding: 20rpx 30rpx;
	display: flex;
	justify-content: flex-end;
	gap: 20rpx;
	box-shadow: 0 -4rpx 20rpx rgba(0, 0, 0, 0.1);
}

.btn-cancel,
.btn-pay,
.btn-contact {
	padding: 20rpx 40rpx;
	border-radius: 40rpx;
	font-size: 28rpx;
	border: 2rpx solid #e5e5e5;
	color: #666;
}

.btn-pay {
	background-color: #94d888;
	color: #fff;
	border-color: #94d888;
}

.btn-contact {
	border-color: #94d888;
	color: #94d888;
}
</style>
