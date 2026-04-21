// 农产品图标映射 - 使用 Element Plus 图标代替图片
import { Food, Apple, Orange, Bowl, IceCream, Coffee, Dessert } from '@element-plus/icons-vue'

// 分类图标映射
const CATEGORY_ICONS = {
  '蔬菜': Food,
  '水果': Apple,
  '粮油': Bowl,
  '畜牧': Dessert,
  '畜禽': Dessert,
  '水产品': IceCream,
  'default': Food
}

// 分类颜色配置
const CATEGORY_COLORS = {
  '蔬菜': '#22c55e',    // 绿色
  '水果': '#f97316',    // 橙色
  '粮油': '#eab308',    // 黄色
  '畜牧': '#ef4444',    // 红色
  '畜禽': '#ef4444',    // 红色
  '水产品': '#3b82f6',  // 蓝色
  'default': '#22c55e'  // 默认绿色
}

// 分类背景色
const CATEGORY_BG_COLORS = {
  '蔬菜': '#dcfce7',    // 浅绿色
  '水果': '#ffedd5',    // 浅橙色
  '粮油': '#fef9c3',    // 浅黄色
  '畜牧': '#fee2e2',    // 浅红色
  '畜禽': '#fee2e2',    // 浅红色
  '水产品': '#dbeafe',  // 浅蓝色
  'default': '#dcfce7'  // 默认浅绿色
}

/**
 * 获取分类图标组件
 * @param {string} category - 分类名称
 * @returns {Component} 图标组件
 */
export function getCategoryIcon(category) {
  const categoryMap = {
    '蔬菜': '蔬菜',
    '水果': '水果',
    '粮油': '粮油',
    '畜禽': '畜牧',
    '畜牧': '畜牧',
    '水产品': '水产品'
  }
  const mappedCategory = categoryMap[category] || category
  return CATEGORY_ICONS[mappedCategory] || CATEGORY_ICONS['default']
}

/**
 * 获取分类颜色
 * @param {string} category - 分类名称
 * @returns {string} 颜色值
 */
export function getCategoryColor(category) {
  const categoryMap = {
    '蔬菜': '蔬菜',
    '水果': '水果',
    '粮油': '粮油',
    '畜禽': '畜牧',
    '畜牧': '畜牧',
    '水产品': '水产品'
  }
  const mappedCategory = categoryMap[category] || category
  return CATEGORY_COLORS[mappedCategory] || CATEGORY_COLORS['default']
}

/**
 * 获取分类背景色
 * @param {string} category - 分类名称
 * @returns {string} 背景色值
 */
export function getCategoryBgColor(category) {
  const categoryMap = {
    '蔬菜': '蔬菜',
    '水果': '水果',
    '粮油': '粮油',
    '畜禽': '畜牧',
    '畜牧': '畜牧',
    '水产品': '水产品'
  }
  const mappedCategory = categoryMap[category] || category
  return CATEGORY_BG_COLORS[mappedCategory] || CATEGORY_BG_COLORS['default']
}

/**
 * 获取产品图片（现在返回null，使用图标替代）
 * @param {string} productName - 产品名称
 * @param {string} category - 产品分类
 * @returns {null} 返回null表示使用图标
 */
export function getProductImage(productName, category) {
  return null
}

/**
 * 获取产品图片（带降级处理）
 * @param {string} productName - 产品名称
 * @param {string} category - 产品分类
 * @returns {null} 返回null表示使用图标
 */
export function getProductImageWithFallback(productName, category) {
  return null
}

/**
 * 根据分类获取分类图片（返回null）
 * @param {string} category - 分类名称
 * @returns {null} 返回null表示使用图标
 */
export function getCategoryImage(category) {
  return null
}

// 导出兼容旧代码的productImages对象（返回空对象）
export const productImages = {}

// 导出图标组件
export { Food, Apple, Orange, Bowl, IceCream, Coffee, Dessert }
