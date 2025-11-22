<template>
	<view class="container">
		<view class="logo-section">
			<image src="/static/logo.png" class="logo" mode="aspectFit" />
			<text class="app-name">雪阁咖啡</text>
			<text class="slogan">品质生活，从一杯咖啡开始</text>
		</view>

		<view class="login-section">
			<view class="login-title">手机号登录</view>
			
			<view class="input-group">
				<input 
					class="input-field" 
					v-model="mobile" 
					type="number" 
					maxlength="11"
					placeholder="请输入手机号"
				/>
			</view>

			<view class="input-group">
				<input 
					class="input-field code-input" 
					v-model="code" 
					type="number" 
					maxlength="6"
					placeholder="请输入验证码"
				/>
				<view 
					:class="['btn-code', countdown > 0 ? 'disabled' : '']"
					@click="getCode"
				>
					{{ countdown > 0 ? `${countdown}秒后重试` : '获取验证码' }}
				</view>
			</view>

			<view class="btn-login" @click="login">
				登录
			</view>

			<view class="agreement">
				<text class="agreement-text">
					登录即表示同意
					<text class="link">《用户协议》</text>
					和
					<text class="link">《隐私政策》</text>
				</text>
			</view>
		</view>

		<view class="wechat-login">
			<button class="btn-wechat" open-type="getPhoneNumber" @getphonenumber="getPhoneNumber">
				<text class="wechat-icon">📱</text>
				微信一键登录
			</button>
		</view>
	</view>
</template>

<script>
import { authApi } from '@/api/index.js'

export default {
	data() {
		return {
			mobile: '',
			code: '',
			countdown: 0
		}
	},
	methods: {
		async getCode() {
			if (this.countdown > 0) return
			
			if (!this.mobile || this.mobile.length !== 11) {
				uni.showToast({
					title: '请输入正确的手机号',
					icon: 'none'
				})
				return
			}

			try {
				const res = await authApi.getSmsCode(this.mobile)
				
				uni.showToast({
					title: '验证码已发送',
					icon: 'success'
				})
				
				// 开发环境显示验证码
				if (res.data) {
					console.log('验证码:', res.data)
				}
				
				// 开始倒计时
				this.countdown = 60
				const timer = setInterval(() => {
					this.countdown--
					if (this.countdown <= 0) {
						clearInterval(timer)
					}
				}, 1000)
			} catch (error) {
				console.error('获取验证码失败', error)
			}
		},
		async login() {
			if (!this.mobile || this.mobile.length !== 11) {
				uni.showToast({
					title: '请输入正确的手机号',
					icon: 'none'
				})
				return
			}

			if (!this.code) {
				uni.showToast({
					title: '请输入验证码',
					icon: 'none'
				})
				return
			}

			try {
				const res = await authApi.login(this.mobile, this.code)
				
				const { token, userId, nick } = res.data
				
				// 保存token和用户信息
				uni.setStorageSync('token', token)
				uni.setStorageSync('userId', userId)
				uni.setStorageSync('userInfo', { nick, mobile: this.mobile })

				uni.showToast({
					title: '登录成功',
					icon: 'success'
				})

				// 延迟跳转
				setTimeout(() => {
					uni.switchTab({
						url: '/pages/index/index'
					})
				}, 1500)
			} catch (error) {
				console.error('登录失败', error)
			}
		},
		getPhoneNumber(e) {
			console.log('获取手机号', e)
			if (e.detail.errMsg === 'getPhoneNumber:ok') {
				// 这里需要将e.detail.code发送到后端解密获取手机号
				uni.showToast({
					title: '微信登录功能需要配置AppID',
					icon: 'none'
				})
			}
		}
	}
}
</script>

<style scoped>
.container {
	min-height: 100vh;
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	padding: 100rpx 60rpx;
	display: flex;
	flex-direction: column;
	justify-content: space-between;
}

.logo-section {
	text-align: center;
	margin-bottom: 100rpx;
}

.logo {
	width: 200rpx;
	height: 200rpx;
	margin-bottom: 40rpx;
}

.app-name {
	display: block;
	font-size: 48rpx;
	font-weight: bold;
	color: #fff;
	margin-bottom: 20rpx;
}

.slogan {
	display: block;
	font-size: 28rpx;
	color: rgba(255, 255, 255, 0.8);
}

.login-section {
	background-color: #fff;
	border-radius: 30rpx;
	padding: 60rpx 40rpx;
}

.login-title {
	font-size: 36rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 60rpx;
}

.input-group {
	display: flex;
	align-items: center;
	border-bottom: 2rpx solid #e5e5e5;
	margin-bottom: 40rpx;
	position: relative;
}

.input-field {
	flex: 1;
	padding: 20rpx 0;
	font-size: 30rpx;
}

.code-input {
	flex: 1;
}

.btn-code {
	padding: 15rpx 30rpx;
	font-size: 26rpx;
	color: #667eea;
	white-space: nowrap;
}

.btn-code.disabled {
	color: #999;
}

.btn-login {
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	color: #fff;
	text-align: center;
	padding: 30rpx 0;
	border-radius: 50rpx;
	font-size: 32rpx;
	font-weight: bold;
	margin-top: 40rpx;
}

.agreement {
	text-align: center;
	margin-top: 40rpx;
}

.agreement-text {
	font-size: 24rpx;
	color: #999;
}

.link {
	color: #667eea;
}

.wechat-login {
	margin-top: 60rpx;
}

.btn-wechat {
	background-color: #09bb07;
	color: #fff;
	border-radius: 50rpx;
	padding: 25rpx 0;
	font-size: 30rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	border: none;
}

.btn-wechat::after {
	border: none;
}

.wechat-icon {
	font-size: 40rpx;
	margin-right: 15rpx;
}
</style>
