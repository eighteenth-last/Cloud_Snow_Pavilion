<template>
	<view class="container">
		<!-- 地址列表 -->
		<view v-if="addressList.length > 0" class="address-list">
			<view 
				v-for="item in addressList" 
				:key="item.id"
				class="address-item"
			>
				<view class="address-content" @click="selectAddress(item)">
					<view class="address-header">
						<text class="receiver">{{ item.receiver }}</text>
						<text class="mobile">{{ item.mobile }}</text>
						<view v-if="item.isDefault" class="default-tag">默认</view>
					</view>
					<text class="address-detail">{{ item.address }}</text>
				</view>
				
				<view class="address-actions">
					<view class="btn-edit" @click="editAddress(item)">
						<text class="action-icon">✏️</text>
						<text>编辑</text>
					</view>
					<view class="btn-delete" @click="deleteAddress(item.id)">
						<text class="action-icon">🗑️</text>
						<text>删除</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 空状态 -->
		<view v-else class="empty">
			<text class="empty-icon">📍</text>
			<text class="empty-text">暂无收货地址</text>
		</view>

		<!-- 添加地址按钮 -->
		<view class="add-btn" @click="addAddress">
			+ 新增收货地址
		</view>
	</view>
</template>

<script>
import { addressApi } from '@/api/index.js'

export default {
	data() {
		return {
			addressList: [],
			fromOrder: false
		}
	},
	onLoad(options) {
		this.fromOrder = options.from === 'order'
		this.loadAddressList()
	},
	methods: {
		async loadAddressList() {
			try {
				const res = await addressApi.getList()
				this.addressList = res.data || []
			} catch (error) {
				console.error('加载地址列表失败', error)
			}
		},
		selectAddress(item) {
			if (this.fromOrder) {
				// 从订单页面过来，选择地址后返回
				uni.navigateBack({
					success: () => {
						uni.$emit('addressSelected', item)
					}
				})
			}
		},
		addAddress() {
			uni.navigateTo({
				url: '/pages/address/edit'
			})
		},
		editAddress(item) {
			uni.navigateTo({
				url: `/pages/address/edit?id=${item.id}`
			})
		},
		async deleteAddress(id) {
			const res = await uni.showModal({
				title: '提示',
				content: '确定要删除该地址吗？'
			})

			if (res.confirm) {
				try {
					await addressApi.delete(id)
					
					uni.showToast({
						title: '删除成功',
						icon: 'success'
					})
					this.loadAddressList()
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
	padding: 20rpx 30rpx 120rpx;
}

.address-list {
	margin-bottom: 20rpx;
}

.address-item {
	background-color: #fff;
	border-radius: 20rpx;
	margin-bottom: 20rpx;
	overflow: hidden;
}

.address-content {
	padding: 30rpx;
}

.address-header {
	display: flex;
	align-items: center;
	margin-bottom: 20rpx;
}

.receiver {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
	margin-right: 30rpx;
}

.mobile {
	font-size: 28rpx;
	color: #666;
	flex: 1;
}

.default-tag {
	background-color: #ff6b6b;
	color: #fff;
	font-size: 22rpx;
	padding: 5rpx 15rpx;
	border-radius: 20rpx;
}

.address-detail {
	font-size: 28rpx;
	color: #666;
	line-height: 1.6;
}

.address-actions {
	display: flex;
	border-top: 2rpx solid #f5f5f5;
}

.btn-edit,
.btn-delete {
	flex: 1;
	display: flex;
	align-items: center;
	justify-content: center;
	padding: 30rpx 0;
	font-size: 28rpx;
	color: #666;
}

.btn-edit {
	border-right: 2rpx solid #f5f5f5;
}

.action-icon {
	font-size: 32rpx;
	margin-right: 10rpx;
}

.empty {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding: 200rpx 0;
}

.empty-icon {
	font-size: 120rpx;
	margin-bottom: 40rpx;
}

.empty-text {
	font-size: 28rpx;
	color: #999;
}

.add-btn {
	position: fixed;
	bottom: 30rpx;
	left: 30rpx;
	right: 30rpx;
	background-color: #94d888;
	color: #fff;
	text-align: center;
	padding: 30rpx 0;
	border-radius: 50rpx;
	font-size: 32rpx;
	font-weight: bold;
	box-shadow: 0 4rpx 20rpx rgba(60, 197, 31, 0.3);
}
</style>
