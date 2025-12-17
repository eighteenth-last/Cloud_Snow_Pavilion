<template>
	<view class="container">
		<!-- 订单状态标签 -->
		<view class="status-tabs">
			<view 
				v-for="(item, index) in statusList" 
				:key="index"
				:class="['tab-item', currentStatus === item.value ? 'active' : '']"
				@click="selectStatus(item.value)"
			>
				{{ item.label }}
			</view>
		</view>

		<!-- 订单列表 -->
		<view class="order-list">
			<view 
				v-for="order in orderList" 
				:key="order.orderId"
				class="order-item"
			>
				<view class="order-header">
					<text class="order-no">订单号：{{ order.orderId }}</text>
					<text :class="['order-status', getStatusClass(order.status)]">
						{{ getStatusText(order.status) }}
					</text>
				</view>

				<view class="order-content">
					<view class="order-info">
						<text class="order-type">{{ order.orderType === 'take' ? '自取' : '外卖' }}</text>
						<text class="order-time">{{ order.createTime }}</text>
					</view>
					<view class="order-price">
						<text class="price-label">实付：</text>
						<text class="price-value">¥{{ order.amount }}</text>
					</view>
				</view>

				<view class="order-footer">
					<view class="btn-cancel" v-if="order.status === 0" @click="cancelOrder(order.orderId)">
						取消订单
					</view>
					<view class="btn-pay" v-if="order.status === 0" @click="payOrder(order.orderId)">
						去支付
					</view>
					<view class="btn-detail" @click="viewDetail(order.orderId)">
						订单详情
					</view>
				</view>
			</view>

			<view v-if="orderList.length === 0" class="empty">
				<text>暂无订单</text>
			</view>
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			currentStatus: null,
			statusList: [
				{ label: '全部', value: null },
				{ label: '待支付', value: 0 },
				{ label: '已支付', value: 1 },
				{ label: '制作中', value: 2 },
				{ label: '已完成', value: 3 }
			],
			orderList: []
		}
	},
	onShow() {
		this.loadOrders()
	},
	methods: {
		selectStatus(status) {
			this.currentStatus = status
			this.loadOrders()
		},
		async loadOrders() {
			try {
				const token = uni.getStorageSync('token')
				if (!token) {
					uni.showToast({
						title: '请先登录',
						icon: 'none'
					})
					return
				}

				// 构建请求参数，只有status不为null时才添加
				const params = {}
				if (this.currentStatus !== null) {
					params.status = this.currentStatus
				}

				const res = await uni.request({
					url: 'http://localhost:8080/api/order/my',
					method: 'GET',
					header: {
						'Authorization': `Bearer ${token}`
					},
					data: params
				})
				if (res.data.code === 200) {
					this.orderList = res.data.data.records || []
				}
			} catch (error) {
				console.error('加载订单失败', error)
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
		async cancelOrder(orderId) {
			const res = await uni.showModal({
				title: '提示',
				content: '确定要取消订单吗？'
			})
			if (res.confirm) {
				try {
					const token = uni.getStorageSync('token')
					await uni.request({
						url: `http://localhost:8080/api/order/${orderId}/cancel`,
						method: 'POST',
						header: {
							'Authorization': `Bearer ${token}`
						}
					})
					uni.showToast({
						title: '订单已取消',
						icon: 'success'
					})
					this.loadOrders()
				} catch (error) {
					console.error('取消订单失败', error)
				}
			}
		},
		payOrder(orderId) {
			uni.showToast({
				title: '支付功能开发中',
				icon: 'none'
			})
		},
		viewDetail(orderId) {
			uni.navigateTo({
				url: `/pages/order/detail?id=${orderId}`
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

.status-tabs {
	display: flex;
	background-color: #fff;
	padding: 20rpx 0;
	position: sticky;
	top: 0;
	z-index: 10;
}

.tab-item {
	flex: 1;
	text-align: center;
	padding: 15rpx 0;
	font-size: 28rpx;
	color: #666;
	position: relative;
}

.tab-item.active {
	color: #94d888;
	font-weight: bold;
}

.tab-item.active::after {
	content: '';
	position: absolute;
	bottom: 0;
	left: 50%;
	transform: translateX(-50%);
	width: 60rpx;
	height: 4rpx;
	background-color: #94d888;
}

.order-list {
	padding: 20rpx 30rpx;
}

.order-item {
	background-color: #fff;
	border-radius: 20rpx;
	padding: 30rpx;
	margin-bottom: 20rpx;
}

.order-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 20rpx;
	padding-bottom: 20rpx;
	border-bottom: 2rpx solid #f5f5f5;
}

.order-no {
	font-size: 28rpx;
	color: #666;
}

.order-status {
	font-size: 28rpx;
	font-weight: bold;
}

.status-wait {
	color: #ff9800;
}

.status-paid {
	color: #94d888;
}

.status-making {
	color: #2196f3;
}

.status-done {
	color: #999;
}

.status-cancel {
	color: #f44336;
}

.order-content {
	margin-bottom: 20rpx;
}

.order-info {
	display: flex;
	justify-content: space-between;
	margin-bottom: 15rpx;
	font-size: 28rpx;
	color: #666;
}

.order-price {
	text-align: right;
}

.price-label {
	font-size: 28rpx;
	color: #666;
}

.price-value {
	font-size: 36rpx;
	color: #ff6b6b;
	font-weight: bold;
}

.order-footer {
	display: flex;
	justify-content: flex-end;
	gap: 20rpx;
}

.btn-cancel,
.btn-pay,
.btn-detail {
	padding: 15rpx 40rpx;
	border-radius: 40rpx;
	font-size: 26rpx;
	border: 2rpx solid #e5e5e5;
	color: #666;
}

.btn-pay {
	background-color: #94d888;
	color: #fff;
	border-color: #94d888;
}

.empty {
	text-align: center;
	padding: 100rpx 0;
	color: #999;
	font-size: 28rpx;
}
</style>
