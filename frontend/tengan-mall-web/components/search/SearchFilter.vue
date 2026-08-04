<template>
  <div class="bg-white rounded-xl shadow-sm border border-gray-100 mb-4">

    <!-- 已選條件 -->
    <div v-if="activeFilters.length > 0" class="flex items-center gap-3 px-5 py-3 border-b border-gray-100">
      <span class="text-sm text-gray-500 w-16 shrink-0">條件篩選</span>
      <div class="flex flex-wrap items-center gap-2 flex-1">
        <span
          v-for="f in activeFilters"
          :key="f.id"
          class="flex items-center gap-1 bg-gray-100 text-gray-700 text-xs px-2.5 py-1 rounded-full"
        >
          {{ f.label }}
          <button class="text-gray-400 hover:text-gray-700 ml-0.5" @click="removeFilter(f)">
            <UIcon name="i-heroicons-x-mark" class="w-3 h-3" />
          </button>
        </span>
        <button class="flex items-center gap-1 text-xs text-gray-400 hover:text-red-500 transition-colors" @click="clearAll">
          <UIcon name="i-heroicons-trash" class="w-3.5 h-3.5" />
          清空條件
        </button>
      </div>
    </div>

    <!-- 分類 -->
    <div class="flex items-center gap-4 px-5 py-3 border-b border-gray-100">
      <span class="text-sm text-gray-500 w-16 shrink-0">分類</span>
      <div class="flex flex-wrap gap-x-8 gap-y-1">
        <button
          v-for="c in CATEGORIES"
          :key="c"
          class="text-sm transition-colors"
          :class="selectedCategories.includes(c)
            ? 'text-red-500 font-medium'
            : 'text-gray-600 hover:text-red-500'"
          @click="toggleItem(selectedCategories, c)"
        >
          {{ c }}
        </button>
      </div>
    </div>

    <!-- 品牌 -->
    <SearchFilterCheckboxRow
      label="品牌"
      :options="BRANDS"
      :selected="selectedBrands"
      :expanded="expandedBrands"
      @toggle="toggleItem(selectedBrands, $event)"
      @expand="expandedBrands = !expandedBrands"
    />

    <!-- 類型 -->
    <SearchFilterCheckboxRow
      label="類型"
      :options="TYPES"
      :selected="selectedTypes"
      :expanded="expandedTypes"
      @toggle="toggleItem(selectedTypes, $event)"
      @expand="expandedTypes = !expandedTypes"
    />

    <!-- 款式 -->
    <SearchFilterCheckboxRow
      label="款式"
      :options="STYLES"
      :selected="selectedStyles"
      :expanded="expandedStyles"
      @toggle="toggleItem(selectedStyles, $event)"
      @expand="expandedStyles = !expandedStyles"
    />

    <!-- 其他條件 -->
    <div class="flex items-start gap-4 px-5 py-3">
      <span class="text-sm text-gray-500 w-16 shrink-0 pt-1">其他條件</span>
      <div class="flex-1 flex flex-wrap gap-x-6 gap-y-2">
        <div v-for="attr in visibleAttrs" :key="attr.key" class="relative">
          <button
            class="flex items-center gap-0.5 text-sm transition-colors"
            :class="openAttrKey === attr.key
              ? 'text-blue-600 font-medium'
              : attrSelections[attr.key]?.length
                ? 'text-blue-500 font-medium'
                : 'text-gray-600 hover:text-blue-500'"
            @click.stop="toggleAttrDropdown(attr.key)"
          >
            {{ attr.label }}
            <span v-if="attrSelections[attr.key]?.length" class="text-blue-500">
              ({{ attrSelections[attr.key].length }})
            </span>
            <UIcon
              :name="openAttrKey === attr.key ? 'i-heroicons-chevron-up' : 'i-heroicons-chevron-down'"
              class="w-3.5 h-3.5 ml-0.5"
            />
          </button>

          <!-- 下拉選單 -->
          <div
            v-if="openAttrKey === attr.key"
            class="absolute top-full left-0 mt-1 z-30 bg-white shadow-lg rounded-lg border border-gray-100 py-2 min-w-36"
            @click.stop
          >
            <label
              v-for="opt in attr.options"
              :key="opt"
              class="flex items-center gap-2 px-3 py-1.5 cursor-pointer hover:bg-gray-50 group"
              @click.prevent="toggleAttrOption(attr.key, opt)"
            >
              <span
                class="w-4 h-4 rounded border flex items-center justify-center shrink-0 transition-colors"
                :class="isAttrSelected(attr.key, opt)
                  ? 'bg-blue-500 border-blue-500'
                  : 'border-gray-300 group-hover:border-blue-400'"
              >
                <UIcon v-if="isAttrSelected(attr.key, opt)" name="i-heroicons-check" class="w-2.5 h-2.5 text-white" />
              </span>
              <span
                class="text-sm whitespace-nowrap"
                :class="isAttrSelected(attr.key, opt) ? 'text-blue-600 font-medium' : 'text-gray-700'"
              >
                {{ opt }}
              </span>
            </label>
          </div>
        </div>
      </div>

      <button
        class="flex items-center gap-0.5 text-sm text-blue-500 shrink-0 whitespace-nowrap hover:text-blue-600 transition-colors"
        @click="expandedAttrs = !expandedAttrs"
      >
        {{ expandedAttrs ? '收合' : '選更多' }}
        <UIcon :name="expandedAttrs ? 'i-heroicons-chevron-up' : 'i-heroicons-chevron-down'" class="w-3.5 h-3.5" />
      </button>
    </div>

  </div>
</template>

<script setup lang="ts">

// ── Mock 篩選資料（P7 改為從 ES aggregations 取得）──
const CATEGORIES = ['手機', '保護配件', '耳機', '電腦', '家電', '智慧手錶']
const BRANDS     = ['Apple 蘋果', 'Samsung 三星', 'Sony', 'ASUS 華碩', 'Dyson 戴森', 'LG', 'Nintendo 任天堂', 'Panasonic']
const TYPES      = ['智慧型手機', '無線耳機', '筆記型電腦', '平板電腦', '智慧手錶', '相機', '電視', '掃地機器人']
const STYLES     = ['黑色', '白色', '銀色', '金色', '玫瑰金', '藍色', '紅色', '綠色']

interface AttrDef { key: string; label: string; options: string[] }
const ALL_ATTRS: AttrDef[] = [
  { key: 'size',     label: '尺寸',     options: ['5.4吋', '6.1吋', '6.7吋', '7吋以上'] },
  { key: 'storage',  label: '容量',     options: ['64GB', '128GB', '256GB', '512GB', '1TB'] },
  { key: 'status',   label: '商品狀態', options: ['全新', '福利品', '二手'] },
  { key: 'cpu',      label: '處理器',   options: ['Apple M3', 'Intel i7', 'AMD Ryzen 7', 'Snapdragon 8'] },
  { key: 'pixel',    label: '畫素',     options: ['12MP', '48MP', '108MP', '200MP'] },
  { key: 'target',   label: '適用對象', options: ['男性', '女性', '兒童', '老年人', '通用'] },
  { key: 'feature',  label: '功能',     options: ['防水', '無線充電', '5G', 'Wi-Fi 6E'] },
  { key: 'color',    label: '顏色',     options: ['黑色', '白色', '銀色', '金色', '藍色'] },
  { key: 'material', label: '材質',     options: ['塑膠', '金屬', '玻璃', '皮革', '木質'] },
]

// ── State ──
const selectedCategories = ref<string[]>([])
const selectedBrands     = ref<string[]>([])
const selectedTypes      = ref<string[]>([])
const selectedStyles     = ref<string[]>([])
const attrSelections     = reactive<Record<string, string[]>>({})

const expandedBrands = ref(false)
const expandedTypes  = ref(false)
const expandedStyles = ref(false)
const expandedAttrs  = ref(false)
const openAttrKey    = ref<string | null>(null)

const visibleAttrs = computed(() =>
  expandedAttrs.value ? ALL_ATTRS : ALL_ATTRS.slice(0, 4)
)

// ── Active filters chips ──
interface ActiveFilter { id: string; label: string; group: string; value: string }

const activeFilters = computed<ActiveFilter[]>(() => {
  const result: ActiveFilter[] = []
  selectedCategories.value.forEach(v => result.push({ id: `cat_${v}`, label: v, group: 'categories', value: v }))
  selectedBrands.value.forEach(v     => result.push({ id: `br_${v}`,  label: v, group: 'brands',     value: v }))
  selectedTypes.value.forEach(v      => result.push({ id: `ty_${v}`,  label: v, group: 'types',      value: v }))
  selectedStyles.value.forEach(v     => result.push({ id: `st_${v}`,  label: v, group: 'styles',     value: v }))
  Object.entries(attrSelections).forEach(([key, vals]) =>
    vals.forEach(v => result.push({ id: `attr_${key}_${v}`, label: v, group: `attr_${key}`, value: v }))
  )
  return result
})

// ── Helpers ──
function toggleItem(list: string[], value: string) {
  const idx = list.indexOf(value)
  if (idx === -1) list.push(value)
  else list.splice(idx, 1)
  emit('change', currentState())
}

function toggleAttrDropdown(key: string) {
  openAttrKey.value = openAttrKey.value === key ? null : key
}

function toggleAttrOption(key: string, opt: string) {
  if (!attrSelections[key]) attrSelections[key] = []
  const arr = attrSelections[key]
  const idx = arr.indexOf(opt)
  if (idx === -1) arr.push(opt)
  else arr.splice(idx, 1)
  emit('change', currentState())
}

function isAttrSelected(key: string, opt: string) {
  return attrSelections[key]?.includes(opt) ?? false
}

function removeFilter(f: ActiveFilter) {
  if (f.group.startsWith('attr_')) {
    const key = f.group.slice(5)
    const arr = attrSelections[key]
    if (arr) { const i = arr.indexOf(f.value); if (i !== -1) arr.splice(i, 1) }
  } else {
    const map: Record<string, Ref<string[]>> = {
      categories: selectedCategories,
      brands: selectedBrands,
      types: selectedTypes,
      styles: selectedStyles,
    }
    const list = map[f.group]
    if (list) { const i = list.value.indexOf(f.value); if (i !== -1) list.value.splice(i, 1) }
  }
  emit('change', currentState())
}

function clearAll() {
  selectedCategories.value = []
  selectedBrands.value     = []
  selectedTypes.value      = []
  selectedStyles.value     = []
  Object.keys(attrSelections).forEach(k => { attrSelections[k] = [] })
  emit('change', currentState())
}

// 點外部關閉 dropdown
function onOutsideClick() { openAttrKey.value = null }
onMounted(()   => document.addEventListener('click', onOutsideClick))
onUnmounted(() => document.removeEventListener('click', onOutsideClick))

// ── Emit ──
export interface FilterState {
  categories: string[]
  brands: string[]
  types: string[]
  styles: string[]
  attrs: Record<string, string[]>
}

const emit = defineEmits<{ change: [FilterState] }>()

function currentState(): FilterState {
  return {
    categories: [...selectedCategories.value],
    brands:     [...selectedBrands.value],
    types:      [...selectedTypes.value],
    styles:     [...selectedStyles.value],
    attrs:      Object.fromEntries(Object.entries(attrSelections).map(([k, v]) => [k, [...v]])),
  }
}
</script>
