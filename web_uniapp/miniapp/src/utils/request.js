/**
 * 统一请求封装
 */

// API基础URL
const BASE_URL = 'http://localhost:8080/api'

/**
 * 发起请求
 * @param {Object} options 请求配置
 * @returns {Promise}
 */
function request(options) {
	return new Promise((resolve, reject) => {
		// 获取token
		const token = uni.getStorageSync('token')
		
		// 构建请求配置
		const config = {
			url: BASE_URL + options.url,
			method: options.method || 'GET',
			data: options.data || {},
			header: {
				'Content-Type': 'application/json',
				...options.header
			}
		}
		
		// 添加token
		if (token) {
			config.header['Authorization'] = 'Bearer ' + token
		}
		
		// 发起请求
		uni.request({
			...config,
			success: (res) => {
				// 请求成功
				if (res.statusCode === 200) {
					const data = res.data
					
					// 业务成功
					if (data.code === 200) {
						resolve(data)
					} else {
						// 业务失败
						handleError(data)
						reject(data)
					}
				} else {
					// HTTP错误
					handleError({
						code: res.statusCode,
						msg: '网络请求失败'
					})
					reject(res)
				}
			},
			fail: (err) => {
				// 请求失败
				handleError({
					code: -1,
					msg: '网络连接失败'
				})
				reject(err)
			}
		})
	})
}

/**
 * 处理错误
 * @param {Object} error 错误信息
 */
function handleError(error) {
	const code = error.code
	const msg = error.msg || '未知错误'
	
	// 未登录，跳转到登录页
	if (code === 401 || msg.includes('未登录')) {
		uni.removeStorageSync('token')
		uni.removeStorageSync('userId')
		uni.removeStorageSync('userInfo')
		
		uni.showToast({
			title: '请先登录',
			icon: 'none',
			duration: 2000
		})
		
		setTimeout(() => {
			uni.navigateTo({
				url: '/pages/login/login'
			})
		}, 2000)
		return
	}
	
	// 其他错误提示
	uni.showToast({
		title: msg,
		icon: 'none',
		duration: 2000
	})
}

/**
 * GET请求
 */
export function get(url, data = {}) {
	return request({
		url,
		method: 'GET',
		data
	})
}

/**
 * POST请求
 */
export function post(url, data = {}) {
	return request({
		url,
		method: 'POST',
		data
	})
}

/**
 * PUT请求
 */
export function put(url, data = {}) {
	return request({
		url,
		method: 'PUT',
		data
	})
}

/**
 * DELETE请求
 */
export function del(url, data = {}) {
	return request({
		url,
		method: 'DELETE',
		data
	})
}

export default request
