<template>
	<view class="container">
		<!-- 轮播图 -->
		<view class="banner-section">
			<swiper class="banner-swiper" indicator-dots circular autoplay :interval="3000" :duration="500">
				<swiper-item v-for="(item, index) in banners" :key="item.id || index">
					<view class="banner-item" @click="handleBannerClick(item)">
						<image :src="getBannerUrl(item.imgUrl)" class="banner-image" mode="aspectFill" />
					</view>
				</swiper-item>
			</swiper>
		</view>

		<!-- 到店自取/外送 -->
		<view class="quick-entry">
			<view class="entry-item" @click="goToMenu('take')">
				<text class="entry-text">到店自取</text>
			</view>
			<view class="entry-divider"></view>
			<view class="entry-item" @click="goToMenu('delivery')">
				<text class="entry-text">外送</text>
			</view>
		</view>

		<!-- 券、福利、周边入口 -->
		<view class="welfare-bar" @click="goToWelfare">
			<text class="welfare-text">券、福利、周边入口</text>
		</view>

		<!-- 热卖产品 -->
		<view class="hot-section">
			<view class="section-title">
				<text class="title-text">热卖产品</text>
			</view>
			<view class="product-list">
				<view 
					v-for="product in hotProducts" 
					:key="product.id"
					class="product-item"
					@click="goToProduct(product.id)"
				>
					<image :src="getImageUrl(product.img)" class="product-image" mode="aspectFill" />
					<text class="product-name">{{ product.name }}</text>
					<text class="product-price">¥{{ product.price }}</text>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			banners: [],
			hotProducts: []
		}
	},
	onLoad() {
		this.loadBanners()
		this.loadHotProducts()
	},
	methods: {
		async loadBanners() {
			try {
				console.log('开始加载轮播图...')
				const res = await uni.request({
					url: 'http://localhost:8080/api/carousel/list',
					method: 'GET'
				})
				console.log('轮播图API响应:', res)
				console.log('状态码:', res.statusCode)
				console.log('返回数据:', res.data)
				
				if (res.statusCode === 200 && res.data.code === 200) {
					this.banners = res.data.data || []
					console.log('轮播图数量:', this.banners.length)
					console.log('轮播图数据:', this.banners)
				} else {
					console.error('API返回错误:', res.data)
					this.banners = []
				}
			} catch (error) {
				console.error('加载轮播图失败', error)
				// 加载失败时使用默认图片
				this.banners = [{ imgUrl: '/static/logo.png', jumpType: 0 }]
			}
		},
		async loadHotProducts() {
			try {
				const res = await uni.request({
					url: 'http://localhost:8080/api/product/recommend',
					method: 'GET',
					data: { limit: 6 }
				})
				if (res.data.code === 200) {
					this.hotProducts = res.data.data || []
				}
			} catch (error) {
				console.error('加载热卖产品失败', error)
			}
		},
		goToMenu(type) {
			uni.switchTab({
				url: '/pages/menu/menu',
				success: () => {
					uni.$emit('orderType', type)
				}
			})
		},
		goToWelfare() {
			uni.navigateTo({
				url: '/pages/coupon/list'
			})
		},
		goToProduct(id) {
			uni.navigateTo({
				url: `/pages/product/detail?id=${id}`
			})
		},
		getBannerUrl(imgUrl) {
			if (!imgUrl) {
				console.log('图片URL为空，使用默认图片')
				return '/static/logo.png'
			}
			console.log('原始图片路径:', imgUrl)
			
			// 如果是完整的HTTP URL，直接返回
			if (imgUrl.startsWith('http://') || imgUrl.startsWith('https://')) {
				console.log('完整HTTP URL:', imgUrl)
				return imgUrl
			}
			// 如果是相对路径，拼接API地址（需要加/api前缀）
			if (imgUrl.startsWith('/')) {
				const fullUrl = `http://localhost:8080/api${imgUrl}`
				console.log('拼接后的URL:', fullUrl)
				return fullUrl
			}
			// 如果是本地路径，转换为可访问的URL
			if (imgUrl.includes('upload_img')) {
				const relativePath = imgUrl.substring(imgUrl.indexOf('upload_img'))
				const fullUrl = `http://localhost:8080/api/${relativePath.replace(/\\/g, '/')}`
				console.log('转换后的URL:', fullUrl)
				return fullUrl
			}
			console.log('无法处理的路径，使用默认图片')
			return '/static/logo.png'
		},
		handleBannerClick(banner) {
			if (!banner.jumpType || banner.jumpType === 0) {
				return // 不跳转
			}
			
			switch (banner.jumpType) {
				case 1: // 商品详情
					if (banner.jumpVal) {
						uni.navigateTo({
							url: `/pages/product/detail?id=${banner.jumpVal}`
						})
					}
					break
				case 2: // 页面路径
					if (banner.jumpVal) {
						uni.navigateTo({
							url: banner.jumpVal
						})
					}
					break
				case 3: // 外部链接
					if (banner.jumpVal) {
						// #ifdef H5
						window.open(banner.jumpVal)
						// #endif
						// #ifdef MP
						uni.showModal({
							title: '提示',
							content: '请在浏览器中打开此链接',
							showCancel: false
						})
						// #endif
					}
					break
			}
		},
		getImageUrl(img) {
			if (!img) {
				return '/static/logo.png'
			}
			// 如果是相对路径，转换为完整的本地服务器URL
			if (img.startsWith('/upload_img/')) {
				return `http://localhost:8080/api${img}`
			}
			// 如果是完整的本地路径
			if (img.startsWith('http://localhost')) {
				return img
			}
			// 其他情况返回默认图片
			return '/static/logo.png'
		}
	}
}
</script>

<style scoped>
.container {
	background: linear-gradient(180deg, #f8f9fa 0%, #ffffff 100%);
	min-height: 100vh;
	padding-bottom: 40rpx;
}

/* 轮播图 */
.banner-section {
	margin: 20rpx;
	border-radius: 24rpx;
	overflow: hidden;
	box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.08);
}

.banner-swiper {
	height: 360rpx;
}

.banner-item {
	width: 100%;
	height: 100%;
}

.banner-image {
	width: 100%;
	height: 100%;
}

/* 快捷入口 */
.quick-entry {
	display: flex;
	margin: 20rpx;
	background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
	border-radius: 24rpx;
	overflow: hidden;
	box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
}

.entry-item {
	flex: 1;
	height: 220rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	transition: all 0.3s;
	position: relative;
}

.entry-item:active {
	background-color: rgba(255, 77, 79, 0.05);
}

.entry-divider {
	width: 1rpx;
	height: 120rpx;
	background: linear-gradient(180deg, 
		transparent 0%, 
		rgba(255, 77, 79, 0.2) 50%, 
		transparent 100%);
	align-self: center;
}

.entry-text {
	font-size: 36rpx;
	font-weight: 600;
	color: #ff4d4f;
	letter-spacing: 2rpx;
}

/* 福利栏 */
.welfare-bar {
	margin: 20rpx;
	padding: 32rpx 40rpx;
	background: linear-gradient(135deg, #ff6b6b 0%, #ff4d4f 100%);
	border-radius: 24rpx;
	text-align: center;
	box-shadow: 0 8rpx 24rpx rgba(255, 77, 79, 0.25);
	transition: all 0.3s;
}

.welfare-bar:active {
	transform: scale(0.98);
}

.welfare-text {
	font-size: 32rpx;
	color: #ffffff;
	font-weight: 600;
	letter-spacing: 1rpx;
}

/* 热卖产品 */
.hot-section {
	margin: 20rpx;
	background-color: #fff;
	border-radius: 24rpx;
	padding: 40rpx 30rpx 20rpx;
	box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.04);
}

.section-title {
	margin-bottom: 30rpx;
	padding-bottom: 20rpx;
	border-bottom: 3rpx solid #ff4d4f;
	display: inline-block;
}

.title-text {
	font-size: 38rpx;
	font-weight: 700;
	color: #333;
	letter-spacing: 1rpx;
}

.product-list {
	display: flex;
	flex-wrap: wrap;
	justify-content: space-between;
}

.product-item {
	width: 48%;
	margin-bottom: 30rpx;
	display: flex;
	flex-direction: column;
	border-radius: 16rpx;
	overflow: hidden;
	background-color: #fafafa;
	transition: all 0.3s;
}

.product-item:active {
	transform: translateY(-4rpx);
	box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.12);
}

.product-image {
	width: 100%;
	height: 280rpx;
	background-color: #f5f5f5;
}

.product-name {
	font-size: 28rpx;
	color: #333;
	margin: 15rpx 15rpx 8rpx;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
	font-weight: 500;
}

.product-price {
	font-size: 34rpx;
	color: #ff4d4f;
	font-weight: 700;
	margin: 0 15rpx 15rpx;
}
</style>
