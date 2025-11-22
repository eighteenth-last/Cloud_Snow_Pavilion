<template>
	<view class="container">
		<!-- 轮播图 -->
		<view class="banner">
			<text class="banner-title">💰 领券中心</text>
			<text class="banner-subtitle">精选优惠券，等你来领</text>
		</view>

		<!-- 优惠券列表 -->
		<view class="coupon-list">
			<view 
				v-for="coupon in couponList" 
				:key="coupon.id"
				class="coupon-item"
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
					<text class="coupon-stock">仅剩 {{ coupon.stock }} 张</text>
					
					<view class="btn-claim" @click="claimCoupon(coupon)">
						{{ coupon.claimed ? '已领取' : '立即领取' }}
					</view>
				</view>

				<view class="coupon-tag" v-if="coupon.isNew">
					<text>NEW</text>
				</view>
			</view>

			<view v-if="couponList.length === 0" class="empty">
				<text class="empty-icon">🎫</text>
				<text class="empty-text">暂无可领取的优惠券</text>
			</view>
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			couponList: []
		}
	},
	onLoad() {
		this.loadCouponList()
	},
	methods: {
		async loadCouponList() {
			try {
				const res = await uni.request({
					url: 'http://localhost:8080/api/coupon/available',
					method: 'GET'
				})

				if (res.data.code === 200) {
					this.couponList = res.data.data || []
				}
			} catch (error) {
				console.error('加载优惠券列表失败', error)
			}
		},
		async claimCoupon(coupon) {
			if (coupon.claimed) {
				uni.showToast({
					title: '您已领取过该优惠券',
					icon: 'none'
				})
				return
			}

			if (coupon.stock <= 0) {
				uni.showToast({
					title: '优惠券已领完',
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
					title: '领取中...'
				})

				const res = await uni.request({
					url: `http://localhost:8080/api/coupon/${coupon.couponId}/claim`,
					method: 'POST',
					header: {
						'Authorization': `Bearer ${token}`
					}
				})

				uni.hideLoading()

				if (res.data.code === 200) {
					uni.showToast({
						title: '领取成功',
						icon: 'success'
					})
					
					// 更新状态
					coupon.claimed = true
					coupon.stock--
				} else {
					uni.showToast({
						title: res.data.msg || '领取失败',
						icon: 'none'
					})
				}
			} catch (error) {
				uni.hideLoading()
				console.error('领取优惠券失败', error)
				uni.showToast({
					title: '领取失败',
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
}

.banner {
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	padding: 60rpx 30rpx;
	text-align: center;
}

.banner-title {
	display: block;
	font-size: 48rpx;
	font-weight: bold;
	color: #fff;
	margin-bottom: 20rpx;
}

.banner-subtitle {
	display: block;
	font-size: 28rpx;
	color: rgba(255, 255, 255, 0.9);
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
	margin-bottom: 5rpx;
}

.coupon-stock {
	font-size: 24rpx;
	color: rgba(255, 255, 255, 0.8);
	margin-bottom: 15rpx;
}

.btn-claim {
	align-self: flex-end;
	background-color: #fff;
	color: #ff6b6b;
	padding: 10rpx 30rpx;
	border-radius: 30rpx;
	font-size: 24rpx;
	font-weight: bold;
}

.coupon-tag {
	position: absolute;
	top: 0;
	right: 0;
	background-color: #ffd700;
	color: #333;
	padding: 10rpx 20rpx;
	border-bottom-left-radius: 15rpx;
	font-size: 22rpx;
	font-weight: bold;
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
</style>
