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
				<picker 
					mode="multiSelector" 
					:value="regionValue" 
					:range="regionColumns"
					range-key="label"
					@change="onRegionChange"
					@columnchange="onRegionColumnChange"
				>
					<view class="form-picker">
						<text :class="['picker-text', form.region ? '' : 'placeholder']">
							{{ form.region || '请选择省市区' }}
						</text>
						<text class="picker-arrow">></text>
					</view>
				</picker>
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
					color="#94d888"
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
import { regionData, parseRegionValue } from '@/utils/region-data.js'

export default {
	data() {
		return {
			addressId: null,
			form: {
				receiver: '',
				mobile: '',
				region: '',
				province: '',
				city: '',
				district: '',
				detail: '',
				isDefault: false
			},
			// 地区选择器相关
			regionValue: [0, 0, 0], // 当前选中的索引 [省索引, 市索引, 区索引]
			regionColumns: [[], [], []], // 三列数据
			regionData: regionData // 原始数据
		}
	},
	onLoad(options) {
		if (options.id) {
			this.addressId = options.id
			this.loadAddressDetail()
		}
		// 初始化地区选择器
		this.initRegionPicker()
	},
	methods: {
		// 初始化地区选择器
		initRegionPicker() {
			// 初始化省份列
			this.regionColumns[0] = this.regionData
			// 初始化城市列（默认第一个省的城市）
			this.regionColumns[1] = this.regionData[0].children || []
			// 初始化区县列（默认第一个省第一个市的区县）
			this.regionColumns[2] = this.regionData[0].children[0]?.children || []
		},
		// 地区选择器列变化
		onRegionColumnChange(e) {
			const { column, value } = e.detail
			const newValue = [...this.regionValue]
			newValue[column] = value
			
			if (column === 0) {
				// 省份变化，更新城市和区县
				newValue[1] = 0
				newValue[2] = 0
				this.regionColumns[1] = this.regionData[value].children || []
				this.regionColumns[2] = this.regionData[value].children[0]?.children || []
			} else if (column === 1) {
				// 城市变化，更新区县
				newValue[2] = 0
				const provinceIndex = newValue[0]
				this.regionColumns[2] = this.regionData[provinceIndex].children[value]?.children || []
			}
			
			this.regionValue = newValue
		},
		// 地区选择确认
		onRegionChange(e) {
			const valueArr = e.detail.value
			const result = parseRegionValue(valueArr, this.regionColumns)
			
			if (result) {
				this.form.province = result.province
				this.form.city = result.city
				this.form.district = result.district
				this.form.region = result.province + result.city + result.district
				this.regionValue = valueArr
			}
		},
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
						province: data.province,
						city: data.city,
						district: data.district,
						region: data.province + data.city + data.district,
						detail: data.address,
						isDefault: data.isDefault === 1
					}
					// 根据已有地址数据，定位地区选择器的位置
					this.locateRegionValue()
				}
			} catch (error) {
				console.error('加载地址详情失败', error)
			}
		},
		// 根据省市区名称定位选择器位置
		locateRegionValue() {
			const { province, city, district } = this.form
			if (!province || !city || !district) return
			
			// 查找省份索引
			const provinceIndex = this.regionData.findIndex(p => p.label === province)
			if (provinceIndex === -1) return
			
			// 查找城市索引
			const cities = this.regionData[provinceIndex].children || []
			const cityIndex = cities.findIndex(c => c.label === city)
			if (cityIndex === -1) return
			
			// 查找区县索引
			const districts = cities[cityIndex].children || []
			const districtIndex = districts.findIndex(d => d.label === district)
			if (districtIndex === -1) return
			
			// 更新选择器数据
			this.regionValue = [provinceIndex, cityIndex, districtIndex]
			this.regionColumns[1] = cities
			this.regionColumns[2] = districts
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
					province: this.form.province,
					city: this.form.city,
					district: this.form.district,
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
	background-color: #94d888;
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
