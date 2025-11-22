<template>
	<view class="container">
		<!-- 用户信息 -->
		<view class="user-info">
			<image :src="userInfo.avatar || '/static/logo.png'" class="avatar" mode="aspectFill" />
			<view class="user-detail">
				<text class="user-name">{{ userInfo.nick || '点击登录' }}</text>
				<text class="user-mobile">{{ userInfo.mobile || '' }}</text>
			</view>
			<view class="vip-badge" v-if="userInfo.vipLevel > 0">
				VIP{{ userInfo.vipLevel }}
			</view>
		</view>

		<!-- 会员信息 -->
		<view class="member-info" v-if="isLogin">
			<view class="info-item">
				<text class="info-value">{{ userInfo.points || 0 }}</text>
				<text class="info-label">积分</text>
			</view>
			<view class="info-item">
				<text class="info-value">¥{{ userInfo.balance || 0 }}</text>
				<text class="info-label">余额</text>
			</view>
			<view class="info-item">
				<text class="info-value">{{ couponCount }}</text>
				<text class="info-label">优惠券</text>
			</view>
		</view>

		<!-- 订单入口 -->
		<view class="order-section">
			<view class="section-title">我的订单</view>
			<view class="order-types">
				<view class="order-type" @click="goToOrders(0)">
					<view class="type-icon">📝</view>
					<text class="type-name">待支付</text>
				</view>
				<view class="order-type" @click="goToOrders(1)">
					<view class="type-icon">✅</view>
					<text class="type-name">已支付</text>
				</view>
				<view class="order-type" @click="goToOrders(2)">
					<view class="type-icon">🔧</view>
					<text class="type-name">制作中</text>
				</view>
				<view class="order-type" @click="goToOrders(3)">
					<view class="type-icon">✨</view>
					<text class="type-name">已完成</text>
				</view>
			</view>
		</view>

		<!-- 功能菜单 -->
		<view class="menu-section">
			<view class="menu-item" @click="goToPage('coupon')">
				<text class="menu-icon">🎫</text>
				<text class="menu-name">我的优惠券</text>
				<text class="menu-arrow">></text>
			</view>
			<view class="menu-item" @click="goToPage('address')">
				<text class="menu-icon">📍</text>
				<text class="menu-name">收货地址</text>
				<text class="menu-arrow">></text>
			</view>
			<view class="menu-item" @click="goToPage('settings')">
				<text class="menu-icon">⚙️</text>
				<text class="menu-name">设置</text>
				<text class="menu-arrow">></text>
			</view>
		</view>

		<!-- 登录/退出按钮 -->
		<view class="action-section">
			<view v-if="!isLogin" class="btn-login" @click="login">
				立即登录
			</view>
			<view v-else class="btn-logout" @click="logout">
				退出登录
			</view>
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			isLogin: false,
			userInfo: {},
			couponCount: 0
		}
	},
	onShow() {
		this.checkLogin()
	},
	methods: {
		checkLogin() {
			const token = uni.getStorageSync('token')
			if (token) {
				this.isLogin = true
				this.loadUserInfo()
				this.loadCouponCount()
			} else {
				this.isLogin = false
				this.userInfo = {}
			}
		},
		async loadUserInfo() {
			try {
				const token = uni.getStorageSync('token')
				const res = await uni.request({
					url: 'http://localhost:8080/api/user/info',
					method: 'GET',
					header: {
						'Authorization': `Bearer ${token}`
					}
				})
				if (res.data.code === 200) {
					this.userInfo = res.data.data || {}
				}
			} catch (error) {
				console.error('加载用户信息失败', error)
			}
		},
		async loadCouponCount() {
			try {
				const token = uni.getStorageSync('token')
				const res = await uni.request({
					url: 'http://localhost:8080/api/coupon/my',
					method: 'GET',
					header: {
						'Authorization': `Bearer ${token}`
					},
					data: {
						status: 0
					}
				})
				if (res.data.code === 200) {
					this.couponCount = res.data.data.length || 0
				}
			} catch (error) {
				console.error('加载优惠券数量失败', error)
			}
		},
		login() {
			uni.navigateTo({
				url: '/pages/login/login'
			})
		},
		logout() {
			uni.showModal({
				title: '提示',
				content: '确定要退出登录吗？',
				success: (res) => {
					if (res.confirm) {
						uni.removeStorageSync('token')
						this.isLogin = false
						this.userInfo = {}
						uni.showToast({
							title: '已退出登录',
							icon: 'success'
						})
					}
				}
			})
		},
		goToOrders(status) {
			uni.switchTab({
				url: '/pages/order/list'
			})
		},
		goToPage(page) {
			const routes = {
				coupon: '/pages/coupon/list',
				address: '/pages/address/list',
				settings: '/pages/user/settings'
			}
			
			if (routes[page]) {
				uni.navigateTo({
					url: routes[page]
				})
			} else {
				uni.showToast({
					title: '功能开发中',
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
	padding-bottom: 40rpx;
}

.user-info {
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	padding: 60rpx 30rpx;
	display: flex;
	align-items: center;
}

.avatar {
	width: 120rpx;
	height: 120rpx;
	border-radius: 50%;
	margin-right: 30rpx;
	border: 4rpx solid rgba(255, 255, 255, 0.3);
}

.user-detail {
	flex: 1;
	display: flex;
	flex-direction: column;
}

.user-name {
	font-size: 36rpx;
	font-weight: bold;
	color: #fff;
	margin-bottom: 10rpx;
}

.user-mobile {
	font-size: 28rpx;
	color: rgba(255, 255, 255, 0.8);
}

.vip-badge {
	background-color: #ffd700;
	color: #333;
	padding: 10rpx 20rpx;
	border-radius: 20rpx;
	font-size: 24rpx;
	font-weight: bold;
}

.member-info {
	background-color: #fff;
	display: flex;
	padding: 40rpx 30rpx;
	margin-bottom: 20rpx;
}

.info-item {
	flex: 1;
	display: flex;
	flex-direction: column;
	align-items: center;
}

.info-value {
	font-size: 36rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 10rpx;
}

.info-label {
	font-size: 24rpx;
	color: #999;
}

.order-section {
	background-color: #fff;
	padding: 30rpx;
	margin-bottom: 20rpx;
}

.section-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 30rpx;
}

.order-types {
	display: flex;
	justify-content: space-around;
}

.order-type {
	display: flex;
	flex-direction: column;
	align-items: center;
}

.type-icon {
	font-size: 48rpx;
	margin-bottom: 15rpx;
}

.type-name {
	font-size: 26rpx;
	color: #666;
}

.menu-section {
	background-color: #fff;
	margin-bottom: 20rpx;
}

.menu-item {
	display: flex;
	align-items: center;
	padding: 35rpx 30rpx;
	border-bottom: 2rpx solid #f5f5f5;
}

.menu-item:last-child {
	border-bottom: none;
}

.menu-icon {
	font-size: 40rpx;
	margin-right: 20rpx;
}

.menu-name {
	flex: 1;
	font-size: 30rpx;
	color: #333;
}

.menu-arrow {
	font-size: 28rpx;
	color: #999;
}

.action-section {
	padding: 0 30rpx;
}

.btn-login,
.btn-logout {
	background-color: #3cc51f;
	color: #fff;
	text-align: center;
	padding: 30rpx 0;
	border-radius: 50rpx;
	font-size: 32rpx;
	font-weight: bold;
}

.btn-logout {
	background-color: #fff;
	color: #666;
	border: 2rpx solid #e5e5e5;
}
</style>
