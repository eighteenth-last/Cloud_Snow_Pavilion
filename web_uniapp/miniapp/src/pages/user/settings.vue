<template>
	<view class="container">
		<!-- 账号设置 -->
		<view class="section">
			<view class="section-title">账号设置</view>
			<view class="menu-list">
				<view class="menu-item" @click="editNickname">
					<text class="label">昵称</text>
					<view class="value-wrap">
						<text class="value">{{ userInfo.nick || '未设置' }}</text>
						<text class="arrow">›</text>
					</view>
				</view>
				<view class="menu-item" @click="editAvatar">
					<text class="label">头像</text>
					<view class="value-wrap">
					<image :src="getImageUrl(userInfo.avatar)" class="avatar-preview" mode="aspectFill" />
						<text class="arrow">›</text>
					</view>
				</view>
				<view class="menu-item" @click="editMobile">
					<text class="label">手机号</text>
					<view class="value-wrap">
						<text class="value">{{ userInfo.mobile || '未绑定' }}</text>
						<text class="arrow">›</text>
					</view>
				</view>
				<view class="menu-item">
					<text class="label">会员等级</text>
					<view class="value-wrap">
						<text class="value">VIP{{ userInfo.vipLevel || 0 }}</text>
					</view>
				</view>
				<view class="menu-item">
					<text class="label">用户ID</text>
					<view class="value-wrap">
						<text class="value">{{ userInfo.userId }}</text>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			userInfo: {}
		}
	},
	onLoad() {
		this.loadUserInfo()
	},
	methods: {
		async loadUserInfo() {
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
		editNickname() {
			uni.showModal({
				title: '修改昵称',
				editable: true,
				placeholderText: '请输入新昵称',
				success: async (res) => {
					if (res.confirm && res.content) {
						await this.updateUserInfo({ nick: res.content })
					}
				}
			})
		},
		editAvatar() {
			uni.chooseImage({
				count: 1,
				sizeType: ['compressed'],
				sourceType: ['album', 'camera'],
				success: async (res) => {
					const tempFilePath = res.tempFilePaths[0]
					uni.showLoading({ title: '上传中...' })
					
					try {
						const token = uni.getStorageSync('token')
						const uploadRes = await uni.uploadFile({
							url: 'http://localhost:8080/api/file/upload',
							filePath: tempFilePath,
							name: 'file',
							header: {
								'Authorization': `Bearer ${token}`
							}
						})
						
						const data = JSON.parse(uploadRes.data)
						if (data.code === 200) {
							await this.updateUserInfo({ avatar: data.data })
						}
					} catch (error) {
						console.error('上传头像失败', error)
						uni.showToast({
							title: '上传失败',
							icon: 'none'
						})
					} finally {
						uni.hideLoading()
					}
				}
			})
		},
		editMobile() {
			uni.showModal({
				title: '修改手机号',
				editable: true,
				placeholderText: '请输入新手机号',
				success: async (res) => {
					if (res.confirm && res.content) {
						// 验证手机号格式
						const mobileReg = /^1[3-9]\d{9}$/
						if (!mobileReg.test(res.content)) {
							uni.showToast({
								title: '请输入正确的手机号',
								icon: 'none'
							})
							return
						}
						await this.updateUserInfo({ mobile: res.content })
					}
				}
			})
		},
		async updateUserInfo(data) {
			try {
				const token = uni.getStorageSync('token')
				const res = await uni.request({
					url: 'http://localhost:8080/api/user/info',
					method: 'PUT',
					header: {
						'Authorization': `Bearer ${token}`
					},
					data: data
				})
				
				if (res.data.code === 200) {
					uni.showToast({
						title: '修改成功',
						icon: 'success'
					})
					this.loadUserInfo()
				} else {
					uni.showToast({
						title: res.data.msg || '修改失败',
						icon: 'none'
					})
				}
			} catch (error) {
				console.error('更新用户信息失败', error)
				uni.showToast({
					title: '修改失败',
					icon: 'none'
				})
			}
		},
		getImageUrl(img) {
			if (!img) {
				return '/static/logo.png'
			}
			// 如果是相对路径，转换为完整URL
			if (img.startsWith('/upload_img/')) {
				return `http://localhost:8080/api${img}`
			}
			// 如果已经是完整URL
			if (img.startsWith('http://') || img.startsWith('https://')) {
				return img
			}
			return '/static/logo.png'
		}
	}
}
</script>

<style scoped>
.container {
	background-color: #f5f5f5;
	min-height: 100vh;
}

.section {
	margin-top: 20rpx;
	background-color: #fff;
}

.section-title {
	padding: 30rpx;
	font-size: 28rpx;
	color: #999;
	border-bottom: 2rpx solid #f5f5f5;
}

.menu-list {
	background-color: #fff;
}

.menu-item {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 30rpx;
	border-bottom: 2rpx solid #f5f5f5;
}

.menu-item:last-child {
	border-bottom: none;
}

.label {
	font-size: 32rpx;
	color: #333;
}

.value-wrap {
	display: flex;
	align-items: center;
	gap: 20rpx;
}

.value {
	font-size: 28rpx;
	color: #999;
}

.avatar-preview {
	width: 80rpx;
	height: 80rpx;
	border-radius: 50%;
}

.arrow {
	font-size: 32rpx;
	color: #ccc;
}
</style>
