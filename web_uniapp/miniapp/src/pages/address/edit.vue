<template>
	<view class="container">
		<view class="form-section">
			<view class="form-item">
				<text class="form-label">收货人</text>
				<input 
					class="form-input" 
					v-model="form.receiver" 
					placeholder="请输入收货人姓名"
				/>
			</view>

			<view class="form-item">
				<text class="form-label">手机号</text>
				<input 
					class="form-input" 
					v-model="form.mobile" 
					type="number"
					maxlength="11"
					placeholder="请输入手机号"
				/>
			</view>

			<view class="form-item">
				<text class="form-label">所在地区</text>
				<view class="form-picker" @click="showRegionPicker">
					<text :class="['picker-text', form.region ? '' : 'placeholder']">
						{{ form.region || '请选择省市区' }}
					</text>
					<text class="picker-arrow">></text>
				</view>
			</view>

			<view class="form-item">
				<text class="form-label">详细地址</text>
				<textarea 
					class="form-textarea" 
					v-model="form.detail" 
					placeholder="请输入详细地址（街道、门牌号等）"
					maxlength="200"
				/>
			</view>

			<view class="form-item switch-item">
				<text class="form-label">设为默认地址</text>
				<switch 
					:checked="form.isDefault" 
					@change="onDefaultChange"
					color="#3cc51f"
				/>
			</view>
		</view>

		<view class="save-btn" @click="saveAddress">
			保存
		</view>

		<view v-if="addressId" class="delete-btn" @click="deleteAddress">
			删除地址
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			addressId: null,
			form: {
				receiver: '',
				mobile: '',
				region: '',
				detail: '',
				isDefault: false
			}
		}
	},
	onLoad(options) {
		if (options.id) {
			this.addressId = options.id
			this.loadAddressDetail()
		}
	},
	methods: {
		async loadAddressDetail() {
			try {
				const token = uni.getStorageSync('token')
				const res = await uni.request({
					url: `http://localhost:8080/api/address/${this.addressId}`,
					method: 'GET',
					header: {
						'Authorization': `Bearer ${token}`
					}
				})

				if (res.data.code === 200) {
					const data = res.data.data
					this.form = {
						receiver: data.receiver,
						mobile: data.mobile,
						region: data.province + data.city + data.district,
						detail: data.address,
						isDefault: data.isDefault === 1
					}
				}
			} catch (error) {
				console.error('加载地址详情失败', error)
			}
		},
		showRegionPicker() {
			// 这里可以使用uni-app的picker组件或第三方地区选择组件
			uni.showToast({
				title: '地区选择功能开发中',
				icon: 'none'
			})
		},
		onDefaultChange(e) {
			this.form.isDefault = e.detail.value
		},
		async saveAddress() {
			// 验证表单
			if (!this.form.receiver) {
				uni.showToast({
					title: '请输入收货人姓名',
					icon: 'none'
				})
				return
			}

			if (!this.form.mobile || this.form.mobile.length !== 11) {
				uni.showToast({
					title: '请输入正确的手机号',
					icon: 'none'
				})
				return
			}

			if (!this.form.region) {
				uni.showToast({
					title: '请选择所在地区',
					icon: 'none'
				})
				return
			}

			if (!this.form.detail) {
				uni.showToast({
					title: '请输入详细地址',
					icon: 'none'
				})
				return
			}

			try {
				const token = uni.getStorageSync('token')
				
				// 拼接完整地址
				const fullAddress = this.form.region + this.form.detail

				const data = {
					receiver: this.form.receiver,
					mobile: this.form.mobile,
					province: '广东省', // 应该从地区选择器获取
					city: '深圳市',
					district: '南山区',
					address: fullAddress,
					isDefault: this.form.isDefault ? 1 : 0
				}

				const url = this.addressId 
					? `http://localhost:8080/api/address/${this.addressId}`
					: 'http://localhost:8080/api/address'

				const res = await uni.request({
					url,
					method: this.addressId ? 'PUT' : 'POST',
					header: {
						'Authorization': `Bearer ${token}`
					},
					data
				})

				if (res.data.code === 200) {
					uni.showToast({
						title: '保存成功',
						icon: 'success'
					})

					setTimeout(() => {
						uni.navigateBack()
					}, 1500)
				} else {
					uni.showToast({
						title: res.data.msg || '保存失败',
						icon: 'none'
					})
				}
			} catch (error) {
				console.error('保存地址失败', error)
				uni.showToast({
					title: '保存失败',
					icon: 'none'
				})
			}
		},
		async deleteAddress() {
			const res = await uni.showModal({
				title: '提示',
				content: '确定要删除该地址吗？'
			})

			if (res.confirm) {
				try {
					const token = uni.getStorageSync('token')
					const result = await uni.request({
						url: `http://localhost:8080/api/address/${this.addressId}`,
						method: 'DELETE',
						header: {
							'Authorization': `Bearer ${token}`
						}
					})

					if (result.data.code === 200) {
						uni.showToast({
							title: '删除成功',
							icon: 'success'
						})

						setTimeout(() => {
							uni.navigateBack()
						}, 1500)
					}
				} catch (error) {
					console.error('删除地址失败', error)
				}
			}
		}
	}
}
</script>

<style scoped>
.container {
	background-color: #f5f5f5;
	min-height: 100vh;
	padding: 20rpx 30rpx 200rpx;
}

.form-section {
	background-color: #fff;
	border-radius: 20rpx;
	overflow: hidden;
}

.form-item {
	padding: 30rpx;
	border-bottom: 2rpx solid #f5f5f5;
	display: flex;
	align-items: center;
}

.form-item:last-child {
	border-bottom: none;
}

.form-label {
	font-size: 28rpx;
	color: #333;
	width: 150rpx;
	flex-shrink: 0;
}

.form-input {
	flex: 1;
	font-size: 28rpx;
	color: #333;
}

.form-picker {
	flex: 1;
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.picker-text {
	font-size: 28rpx;
	color: #333;
}

.picker-text.placeholder {
	color: #999;
}

.picker-arrow {
	font-size: 28rpx;
	color: #999;
}

.form-textarea {
	flex: 1;
	font-size: 28rpx;
	color: #333;
	min-height: 150rpx;
}

.switch-item {
	justify-content: space-between;
}

.save-btn {
	position: fixed;
	bottom: 120rpx;
	left: 30rpx;
	right: 30rpx;
	background-color: #3cc51f;
	color: #fff;
	text-align: center;
	padding: 30rpx 0;
	border-radius: 50rpx;
	font-size: 32rpx;
	font-weight: bold;
}

.delete-btn {
	position: fixed;
	bottom: 30rpx;
	left: 30rpx;
	right: 30rpx;
	background-color: #fff;
	color: #f44336;
	text-align: center;
	padding: 30rpx 0;
	border-radius: 50rpx;
	font-size: 32rpx;
	border: 2rpx solid #f44336;
}
</style>
