<template>
	<view class="login-container">
		<view class="login-box">
				<view class="logo-wrapper">
					<image src="/static/logo.png" class="logo" mode="aspectFit" />
					<view class="logo-shine"></view>
				</view>
				
				<view class="login-title">手机号登录</view>
				<view class="login-subtitle">欢迎来到云阶雪阁</view>
			
			<view class="input-group">
				<view class="input-item">
					<view class="input-icon">📱</view>
					<input 
						v-model="mobile" 
						type="number" 
						maxlength="11"
						placeholder="请输入手机号"
						class="input"
					/>
				</view>
				<view class="input-item">
					<view class="input-icon">🔐</view>
					<input 
						v-model="code" 
						type="number" 
						maxlength="6"
						placeholder="请输入验证码"
						class="input"
					/>
					<button class="code-btn" :disabled="countdown > 0" @click="sendCode">{{ codeText }}</button>
				</view>
			</view>
			
			<button class="login-btn" @click="login">
				<text class="login-text">登录</text>
			</button>
			
			<view class="tips">
				<text class="tips-text">登录即表示同意</text>
				<text class="tips-link">《用户协议》</text>
				<text class="tips-text">和</text>
				<text class="tips-link">《隐私政策》</text>
			</view>
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			mobile: '',
			code: '',
			codeText: '获取验证码',
			countdown: 0
		}
	},
	methods: {
		async sendCode() {
			if (!this.mobile || this.mobile.length !== 11) {
				uni.showToast({
					title: '请输入正确的手机号',
					icon: 'none'
				})
				return
			}
			
			if (this.countdown > 0) {
				return
			}
			
			try {
				uni.showLoading({
					title: '发送中...'
				})
				
				// 调用后端验证码接口
				const res = await uni.request({
					url: 'http://localhost:8080/api/auth/getSmsCode?mobile=' + this.mobile,
					method: 'POST'
				})
				
				uni.hideLoading()
				
				if (res.data.code === 200) {
					uni.showToast({
						title: '验证码已发送',
						icon: 'success'
					})
					
					// 控制台输出验证码（开发环境）
					console.log('验证码:', res.data.data)
					
					this.countdown = 60
					this.codeText = `${this.countdown}秒后重试`
					
					const timer = setInterval(() => {
						this.countdown--
						if (this.countdown <= 0) {
							clearInterval(timer)
							this.codeText = '获取验证码'
						} else {
							this.codeText = `${this.countdown}秒后重试`
						}
					}, 1000)
				} else {
					uni.showToast({
						title: res.data.msg || '发送失败',
						icon: 'none'
					})
				}
			} catch (error) {
				uni.hideLoading()
				console.error('发送验证码失败', error)
				uni.showToast({
					title: '发送失败，请重试',
					icon: 'none'
				})
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
				uni.showLoading({
					title: '登录中...'
				})
				
				// 调用后端登录接口
				const res = await uni.request({
					url: 'http://localhost:8080/api/auth/login',
					method: 'POST',
					header: {
						'Content-Type': 'application/json'
					},
					data: {
						mobile: this.mobile,
						code: this.code
					}
				})
				
				uni.hideLoading()
				
				if (res.data.code === 200) {
					// 保存token
					uni.setStorageSync('token', res.data.data.token)
					
					uni.showToast({
						title: '登录成功',
						icon: 'success'
					})
					
					setTimeout(() => {
						uni.switchTab({
							url: '/pages/index/index'
						})
					}, 1500)
				} else {
					uni.showToast({
						title: res.data.msg || '登录失败',
						icon: 'none'
					})
				}
			} catch (error) {
				uni.hideLoading()
				console.error('登录失败', error)
				uni.showToast({
					title: '登录失败，请重试',
					icon: 'none'
				})
			}
		}
	}
}
</script>

<style scoped>
.login-container {
	min-height: 100vh;
	display: flex;
	align-items: center;
	justify-content: center;
	padding: 60rpx 40rpx;
	background: linear-gradient(135deg, #94d888 0%, #7bc46f 100%);
	box-sizing: border-box;
}

.login-box {
	background: rgba(255, 255, 255, 0.98);
	border-radius: 40rpx;
	padding: 80rpx 60rpx 60rpx;
	box-shadow: 0 20rpx 60rpx rgba(0, 0, 0, 0.2);
	backdrop-filter: blur(10px);
	position: relative;
	z-index: 1;
}

.logo-wrapper {
	position: relative;
	width: 180rpx;
	height: 180rpx;
	margin: 0 auto 30rpx;
}

.logo {
	width: 180rpx;
	height: 180rpx;
	display: block;
	border-radius: 50%;
	box-shadow: 0 10rpx 30rpx rgba(102, 126, 234, 0.3);
}

.logo-shine {
	position: absolute;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	border-radius: 50%;
	background: linear-gradient(135deg, rgba(255, 255, 255, 0.5) 0%, transparent 50%);
}

.login-title {
	font-size: 52rpx;
	font-weight: bold;
	color: #333;
	text-align: center;
	margin-bottom: 10rpx;
	letter-spacing: 2rpx;
}

.login-subtitle {
	font-size: 28rpx;
	color: #999;
	text-align: center;
	margin-bottom: 70rpx;
}

.input-group {
	margin-bottom: 50rpx;
}

.input-item {
	position: relative;
	margin-bottom: 35rpx;
	display: flex;
	align-items: center;
	background: #f8f9fa;
	border-radius: 50rpx;
	padding: 25rpx 30rpx;
	transition: all 0.3s;
	border: 2rpx solid transparent;
}

.input-item:focus-within {
	background: #fff;
	border-color: #94d888;
	box-shadow: 0 4rpx 20rpx rgba(148, 216, 136, 0.2);
	transform: translateY(-2rpx);
}

.input-icon {
	font-size: 40rpx;
	margin-right: 20rpx;
	opacity: 0.8;
}

.input {
	flex: 1;
	font-size: 32rpx;
	color: #333;
	background: transparent;
}

.code-btn {
	padding: 15rpx 35rpx;
	background: linear-gradient(135deg, #94d888 0%, #7bc46f 100%);
	color: #fff;
	border-radius: 40rpx;
	font-size: 26rpx;
	border: none;
	line-height: 1;
	box-shadow: 0 4rpx 12rpx rgba(148, 216, 136, 0.3);
	transition: all 0.3s;
}

.code-btn:disabled {
	background: #e0e0e0;
	color: #999;
	box-shadow: none;
}

.login-btn {
	width: 100%;
	background: linear-gradient(135deg, #94d888 0%, #7bc46f 100%);
	color: #fff;
	border: none;
	border-radius: 50rpx;
	padding: 35rpx 0;
	font-size: 38rpx;
	font-weight: bold;
	box-shadow: 0 10rpx 30rpx rgba(148, 216, 136, 0.4);
	position: relative;
	overflow: hidden;
	transition: all 0.3s;
}

.login-btn:active {
	transform: scale(0.98);
	box-shadow: 0 5rpx 15rpx rgba(148, 216, 136, 0.3);
}

.login-text {
	position: relative;
	z-index: 1;
}

.tips {
	text-align: center;
	margin-top: 40rpx;
	padding-top: 30rpx;
	border-top: 1rpx solid #f0f0f0;
}

.tips-text {
	font-size: 24rpx;
	color: #999;
}

.tips-link {
	font-size: 24rpx;
	color: #667eea;
	margin: 0 4rpx;
}
</style>