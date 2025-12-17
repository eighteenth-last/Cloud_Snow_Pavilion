// 简化的省市区数据
export const regionData = [
	{
		label: '广东省',
		value: '440000',
		children: [
			{
				label: '广州市',
				value: '440100',
				children: [
					{ label: '天河区', value: '440106' },
					{ label: '海珠区', value: '440105' },
					{ label: '越秀区', value: '440104' },
					{ label: '荔湾区', value: '440103' },
					{ label: '白云区', value: '440111' },
					{ label: '黄埔区', value: '440112' },
					{ label: '番禺区', value: '440113' },
					{ label: '花都区', value: '440114' },
					{ label: '南沙区', value: '440115' },
					{ label: '从化区', value: '440117' },
					{ label: '增城区', value: '440118' }
				]
			},
			{
				label: '深圳市',
				value: '440300',
				children: [
					{ label: '福田区', value: '440304' },
					{ label: '罗湖区', value: '440303' },
					{ label: '南山区', value: '440305' },
					{ label: '宝安区', value: '440306' },
					{ label: '龙岗区', value: '440307' },
					{ label: '盐田区', value: '440308' },
					{ label: '龙华区', value: '440309' },
					{ label: '坪山区', value: '440310' },
					{ label: '光明区', value: '440311' }
				]
			},
			{
				label: '珠海市',
				value: '440400',
				children: [
					{ label: '香洲区', value: '440402' },
					{ label: '斗门区', value: '440403' },
					{ label: '金湾区', value: '440404' }
				]
			},
			{
				label: '佛山市',
				value: '440600',
				children: [
					{ label: '禅城区', value: '440604' },
					{ label: '南海区', value: '440605' },
					{ label: '顺德区', value: '440606' },
					{ label: '三水区', value: '440607' },
					{ label: '高明区', value: '440608' }
				]
			},
			{
				label: '东莞市',
				value: '441900',
				children: [
					{ label: '东莞市', value: '441900' }
				]
			}
		]
	},
	{
		label: '北京市',
		value: '110000',
		children: [
			{
				label: '北京市',
				value: '110100',
				children: [
					{ label: '东城区', value: '110101' },
					{ label: '西城区', value: '110102' },
					{ label: '朝阳区', value: '110105' },
					{ label: '丰台区', value: '110106' },
					{ label: '石景山区', value: '110107' },
					{ label: '海淀区', value: '110108' },
					{ label: '门头沟区', value: '110109' },
					{ label: '房山区', value: '110111' },
					{ label: '通州区', value: '110112' },
					{ label: '顺义区', value: '110113' },
					{ label: '昌平区', value: '110114' },
					{ label: '大兴区', value: '110115' },
					{ label: '怀柔区', value: '110116' },
					{ label: '平谷区', value: '110117' },
					{ label: '密云区', value: '110118' },
					{ label: '延庆区', value: '110119' }
				]
			}
		]
	},
	{
		label: '上海市',
		value: '310000',
		children: [
			{
				label: '上海市',
				value: '310100',
				children: [
					{ label: '黄浦区', value: '310101' },
					{ label: '徐汇区', value: '310104' },
					{ label: '长宁区', value: '310105' },
					{ label: '静安区', value: '310106' },
					{ label: '普陀区', value: '310107' },
					{ label: '虹口区', value: '310109' },
					{ label: '杨浦区', value: '310110' },
					{ label: '闵行区', value: '310112' },
					{ label: '宝山区', value: '310113' },
					{ label: '嘉定区', value: '310114' },
					{ label: '浦东新区', value: '310115' },
					{ label: '金山区', value: '310116' },
					{ label: '松江区', value: '310117' },
					{ label: '青浦区', value: '310118' },
					{ label: '奉贤区', value: '310120' },
					{ label: '崇明区', value: '310151' }
				]
			}
		]
	},
	{
		label: '浙江省',
		value: '330000',
		children: [
			{
				label: '杭州市',
				value: '330100',
				children: [
					{ label: '上城区', value: '330102' },
					{ label: '拱墅区', value: '330105' },
					{ label: '西湖区', value: '330106' },
					{ label: '滨江区', value: '330108' },
					{ label: '萧山区', value: '330109' },
					{ label: '余杭区', value: '330110' },
					{ label: '富阳区', value: '330111' },
					{ label: '临安区', value: '330112' },
					{ label: '临平区', value: '330113' },
					{ label: '钱塘区', value: '330114' }
				]
			},
			{
				label: '宁波市',
				value: '330200',
				children: [
					{ label: '海曙区', value: '330203' },
					{ label: '江北区', value: '330205' },
					{ label: '北仑区', value: '330206' },
					{ label: '镇海区', value: '330211' },
					{ label: '鄞州区', value: '330212' },
					{ label: '奉化区', value: '330213' }
				]
			}
		]
	},
	{
		label: '江苏省',
		value: '320000',
		children: [
			{
				label: '南京市',
				value: '320100',
				children: [
					{ label: '玄武区', value: '320102' },
					{ label: '秦淮区', value: '320104' },
					{ label: '建邺区', value: '320105' },
					{ label: '鼓楼区', value: '320106' },
					{ label: '浦口区', value: '320111' },
					{ label: '栖霞区', value: '320113' },
					{ label: '雨花台区', value: '320114' },
					{ label: '江宁区', value: '320115' },
					{ label: '六合区', value: '320116' },
					{ label: '溧水区', value: '320117' },
					{ label: '高淳区', value: '320118' }
				]
			},
			{
				label: '苏州市',
				value: '320500',
				children: [
					{ label: '姑苏区', value: '320508' },
					{ label: '虎丘区', value: '320505' },
					{ label: '吴中区', value: '320506' },
					{ label: '相城区', value: '320507' },
					{ label: '吴江区', value: '320509' }
				]
			}
		]
	}
]

/**
 * 将多列选择器的值数组转换为省市区对象
 * @param {Array} valueArr - [provinceIndex, cityIndex, districtIndex]
 * @param {Array} columns - 列数据
 * @returns {Object} { province, city, district, provinceCode, cityCode, districtCode }
 */
export function parseRegionValue(valueArr, columns) {
	if (!valueArr || valueArr.length !== 3 || !columns || columns.length !== 3) {
		return null
	}
	
	const [provinceIndex, cityIndex, districtIndex] = valueArr
	const province = columns[0][provinceIndex]
	const city = columns[1][cityIndex]
	const district = columns[2][districtIndex]
	
	return {
		province: province ? province.label : '',
		city: city ? city.label : '',
		district: district ? district.label : '',
		provinceCode: province ? province.value : '',
		cityCode: city ? city.value : '',
		districtCode: district ? district.value : ''
	}
}
