/**
 * API接口统一管理
 */
import { get, post, put, del } from '../utils/request.js'

// ==================== 认证相关 ====================
export const authApi = {
	// 获取短信验证码
	getSmsCode: (mobile) => post(`/auth/getSmsCode?mobile=${mobile}`),
	
	// 手机号验证码登录
	login: (mobile, code) => post('/auth/login', { mobile, code }),
	
	// 微信登录
	wxLogin: (code, encryptedData, iv) => post('/auth/wxLogin', { code, encryptedData, iv }),
	
	// 退出登录
	logout: () => post('/auth/logout'),
	
	// 获取用户信息
	getUserInfo: () => get('/auth/getUserInfo'),
	
	// 检查登录状态
	checkLogin: () => get('/auth/checkLogin')
}

// ==================== 商品相关 ====================
export const productApi = {
	// 获取商品列表
	getList: (categoryId) => get('/product/list', { categoryId }),
	
	// 分页查询商品
	getPage: (current, size, name, categoryId) => get('/product/page', { current, size, name, categoryId }),
	
	// 获取商品详情
	getDetail: (id) => get(`/product/${id}`),
	
	// 搜索商品
	search: (keyword) => get('/product/search', { keyword })
}

// ==================== 订单相关 ====================
export const orderApi = {
	// 创建订单
	create: (orderData) => post('/order/create', orderData),
	
	// 获取订单列表
	getList: (status) => get('/order/list', { status }),
	
	// 获取订单详情
	getDetail: (id) => get(`/order/${id}`),
	
	// 取消订单
	cancel: (id) => post(`/order/${id}/cancel`),
	
	// 支付订单
	pay: (id) => post(`/order/${id}/pay`)
}

// ==================== 地址相关 ====================
export const addressApi = {
	// 获取地址列表
	getList: () => get('/address/list'),
	
	// 获取地址详情
	getDetail: (id) => get(`/address/${id}`),
	
	// 获取默认地址
	getDefault: () => get('/address/default'),
	
	// 新增地址
	add: (address) => post('/address', address),
	
	// 更新地址
	update: (id, address) => put(`/address/${id}`, address),
	
	// 删除地址
	delete: (id) => del(`/address/${id}`),
	
	// 设置默认地址
	setDefault: (id) => post(`/address/${id}/setDefault`)
}

// ==================== 优惠券相关 ====================
export const couponApi = {
	// 获取优惠券列表
	getList: (status) => get('/coupon/list', { status }),
	
	// 获取可用优惠券
	getAvailable: () => get('/coupon/available'),
	
	// 领取优惠券
	claim: (couponId) => post(`/coupon/${couponId}/claim`)
}

// ==================== 门店相关 ====================
export const storeApi = {
	// 获取门店列表
	getList: () => get('/store/list'),
	
	// 获取门店详情
	getDetail: (id) => get(`/store/${id}`),
	
	// 获取附近门店
	getNearby: (latitude, longitude) => get('/store/nearby', { latitude, longitude })
}

// ==================== 用户相关 ====================
export const userApi = {
	// 获取用户信息
	getInfo: () => get('/user/info'),
	
	// 更新用户信息
	update: (userInfo) => put('/user/update', userInfo)
}
