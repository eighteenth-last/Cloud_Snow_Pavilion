<template>
	<view class="container">
		<!-- 标签栏 -->
		<view class="tabs">
			<view 
				v-for="(tab, index) in tabs" 
				:key="index"
				:class="['tab-item', currentTab === index ? 'active' : '']"
				@click="selectTab(index)"
			>
				{{ tab.name }}
			</view>
		</view>

		<!-- 优惠券列表 -->
		<view class="coupon-list">
			<view 
				v-for="coupon in couponList" 
				:key="coupon.id"
				:class="['coupon-item', getCouponClass(coupon)]"
			>
				<view class="coupon-left">
					<view class="coupon-amount">
						<text class="amount-symbol">¥</text>
						<text class="amount-value" v-if="coupon.type === 'amount'">{{ coupon.amount }}</text>
						<text class="amount-value" v-else>{{ coupon.discount }}</text>
						<text class="amount-unit" v-if="coupon.type === 'discount'">折</text>
					</view>
					<text class="coupon-condition" v-if="coupon.minAmount > 0">
						满{{ coupon.minAmount }}可用
					</text>
					<text class="coupon-condition" v-else>
						无门槛
					</text>
				</view>

				<view class="coupon-right">
					<text class="coupon-name">{{ coupon.name }}</text>
					<text class="coupon-time">有效期至 {{ coupon.endTime }}</text>
					
					<view class="coupon-action">
						<view 
							v-if="currentTab === 0 && coupon.status === 0"
							class="btn-use"
							@click="useCoupon(coupon)"
						>
							立即使用
						</view>
						<text v-else-if="coupon.status === 1" class="status-text">已使用</text>
						<text v-else-if="coupon.status === 2" class="status-text">已过期</text>
					</view>
				</view>

				<view class="coupon-corner"></view>
			</view>

			<view v-if="couponList.length === 0" class="empty">
				<text class="empty-icon">🎫</text>
				<text class="empty-text">{{ emptyText }}</text>
			</view>
		</view>

		<!-- 领券中心入口 -->
		<view class="claim-center" @click="goToClaimCenter">
			<text class="claim-icon">🎁</text>
			<text class="claim-text">去领券中心看看</text>
			<text class="claim-arrow">></text>
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			currentTab: 0,
			tabs: [
				{ name: '未使用', status: 0 },
				{ name: '已使用', status: 1 },
				{ name: '已过期', status: 2 }
			],
			couponList: []
		}
	},
	computed: {
		emptyText() {
			const texts = ['暂无可用优惠券', '暂无已使用优惠券', '暂无过期优惠券']
			return texts[this.currentTab]
		}
	},
	onShow() {
		this.loadCouponList()
	},
	methods: {
		selectTab(index) {
			this.currentTab = index
			this.loadCouponList()
		},
		async loadCouponList() {
			try {
				const token = uni.getStorageSync('token')
				if (!token) {
					uni.showToast({
						title: '请先登录',
						icon: 'none'
					})
					return
				}

				const res = await uni.request({
					url: 'http://localhost:8080/api/coupon/my',
					method: 'GET',
					header: {
						'Authorization': `Bearer ${token}`
					},
					data: {
						status: this.tabs[this.currentTab].status
					}
				})

				if (res.data.code === 200) {
					this.couponList = res.data.data || []
				}
			} catch (error) {
				console.error('加载优惠券列表失败', error)
			}
		},
		getCouponClass(coupon) {
			if (coupon.status === 1) return 'used'
			if (coupon.status === 2) return 'expired'
			return ''
		},
		useCoupon(coupon) {
			// 跳转到首页或商品列表
			uni.switchTab({
				url: '/pages/index/index'
			})
		},
		goToClaimCenter() {
			uni.navigateTo({
				url: '/pages/coupon/claim'
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

.tabs {
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
	color: #3cc51f;
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
	background-color: #3cc51f;
}

.coupon-list {
	padding: 20rpx 30rpx;
}

.coupon-item {
	background: linear-gradient(135deg, #ff6b6b 0%, #ff8e53 100%);
	border-radius: 20rpx;
	margin-bottom: 20rpx;
	padding: 30rpx;
	display: flex;
	position: relative;
	overflow: hidden;
}

.coupon-item.used,
.coupon-item.expired {
	background: linear-gradient(135deg, #999 0%, #bbb 100%);
}

.coupon-left {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding-right: 30rpx;
	border-right: 2rpx dashed rgba(255, 255, 255, 0.5);
	min-width: 200rpx;
}

.coupon-amount {
	display: flex;
	align-items: baseline;
	margin-bottom: 10rpx;
}

.amount-symbol {
	font-size: 32rpx;
	color: #fff;
	font-weight: bold;
}

.amount-value {
	font-size: 64rpx;
	color: #fff;
	font-weight: bold;
	line-height: 1;
}

.amount-unit {
	font-size: 32rpx;
	color: #fff;
	font-weight: bold;
}

.coupon-condition {
	font-size: 24rpx;
	color: rgba(255, 255, 255, 0.9);
}

.coupon-right {
	flex: 1;
	display: flex;
	flex-direction: column;
	justify-content: space-between;
	padding-left: 30rpx;
}

.coupon-name {
	font-size: 32rpx;
	font-weight: bold;
	color: #fff;
	margin-bottom: 10rpx;
}

.coupon-time {
	font-size: 24rpx;
	color: rgba(255, 255, 255, 0.8);
	margin-bottom: 20rpx;
}

.coupon-action {
	display: flex;
	justify-content: flex-end;
}

.btn-use {
	background-color: #fff;
	color: #ff6b6b;
	padding: 10rpx 30rpx;
	border-radius: 30rpx;
	font-size: 24rpx;
	font-weight: bold;
}

.status-text {
	font-size: 24rpx;
	color: rgba(255, 255, 255, 0.8);
}

.coupon-corner {
	position: absolute;
	top: 20rpx;
	right: 20rpx;
	width: 0;
	height: 0;
	border-top: 40rpx solid rgba(255, 255, 255, 0.2);
	border-left: 40rpx solid transparent;
}

.empty {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding: 150rpx 0;
}

.empty-icon {
	font-size: 120rpx;
	margin-bottom: 40rpx;
}

.empty-text {
	font-size: 28rpx;
	color: #999;
}

.claim-center {
	display: flex;
	align-items: center;
	justify-content: center;
	background-color: #fff;
	margin: 20rpx 30rpx;
	padding: 40rpx;
	border-radius: 20rpx;
	box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
}

.claim-icon {
	font-size: 48rpx;
	margin-right: 20rpx;
}

.claim-text {
	flex: 1;
	font-size: 30rpx;
	color: #333;
	font-weight: bold;
}

.claim-arrow {
	font-size: 28rpx;
	color: #999;
}
</style>
