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

    <!-- 品牌 -->
    <SearchFilterCheckboxRow
      v-if="brandOptions.length > 0"
      label="品牌"
      :options="brandOptions"
      :selected="selectedBrandNames"
      :expanded="expandedBrands"
      @toggle="toggleBrand"
      @expand="expandedBrands = !expandedBrands"
    />

    <!-- 動態屬性（依目前查詢條件回傳的 searchable 屬性，BaseAttr/SaleAttr 都可能出現） -->
    <div v-if="props.aggregations.attrs.length > 0" class="flex items-start gap-4 px-5 py-3">
      <span class="text-sm text-gray-500 w-16 shrink-0">其他條件</span>
      <div class="flex-1 flex flex-wrap gap-x-6 gap-y-2">
        <div v-for="attr in visibleAttrs" :key="attr.attrKey" class="relative">
          <button
            class="flex items-center gap-0.5 text-sm transition-colors"
            :class="openAttrKey === attr.attrKey
              ? 'text-blue-600 font-medium'
              : attrSelections[attr.attrKey]?.length
                ? 'text-blue-500 font-medium'
                : 'text-gray-600 hover:text-blue-500'"
            @click.stop="toggleAttrDropdown(attr.attrKey)"
          >
            {{ attr.attrName }}
            <span v-if="attrSelections[attr.attrKey]?.length" class="text-blue-500">
              ({{ attrSelections[attr.attrKey].length }})
            </span>
            <UIcon
              :name="openAttrKey === attr.attrKey ? 'i-heroicons-chevron-up' : 'i-heroicons-chevron-down'"
              class="w-3.5 h-3.5 ml-0.5"
            />
          </button>

          <!-- 下拉選單 -->
          <div
            v-if="openAttrKey === attr.attrKey"
            class="absolute top-full left-0 mt-1 z-30 bg-white shadow-lg rounded-lg border border-gray-100 py-2 min-w-36"
            @click.stop
          >
            <label
              v-for="opt in attr.values"
              :key="opt.value"
              class="flex items-center gap-2 px-3 py-1.5 cursor-pointer hover:bg-gray-50 group"
              @click.prevent="toggleAttrOption(attr.attrKey, opt.value)"
            >
              <span
                class="w-4 h-4 rounded border flex items-center justify-center shrink-0 transition-colors"
                :class="isAttrSelected(attr.attrKey, opt.value)
                  ? 'bg-blue-500 border-blue-500'
                  : 'border-gray-300 group-hover:border-blue-400'"
              >
                <UIcon v-if="isAttrSelected(attr.attrKey, opt.value)" name="i-heroicons-check" class="w-2.5 h-2.5 text-white" />
              </span>
              <span
                class="text-sm whitespace-nowrap"
                :class="isAttrSelected(attr.attrKey, opt.value) ? 'text-blue-600 font-medium' : 'text-gray-700'"
              >
                {{ opt.value }}
              </span>
            </label>
          </div>
        </div>
      </div>

      <button
        v-if="props.aggregations.attrs.length > 4"
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
import type { SearchAggregations } from '~/composables/useProductSearch'

const props = defineProps<{
  aggregations: SearchAggregations
}>()

// ── 品牌 ──
const brandOptions = computed(() => props.aggregations.brands.map(b => b.brandName))
const brandNameToId = computed(() => new Map(props.aggregations.brands.map(b => [b.brandName, b.brandId])))
const selectedBrandIds = ref<number[]>([])
const selectedBrandNames = computed(() => {
  const idToName = new Map(props.aggregations.brands.map(b => [b.brandId, b.brandName]))
  return selectedBrandIds.value.map(id => idToName.get(id)).filter((n): n is string => !!n)
})
const expandedBrands = ref(false)

function toggleBrand(brandName: string) {
  const id = brandNameToId.value.get(brandName)
  if (id === undefined) return
  const idx = selectedBrandIds.value.indexOf(id)
  if (idx === -1) selectedBrandIds.value.push(id)
  else selectedBrandIds.value.splice(idx, 1)
  emit('change', currentState())
}

// ── 動態屬性 ──
const attrSelections = reactive<Record<string, string[]>>({})
const expandedAttrs = ref(false)
const openAttrKey = ref<string | null>(null)

const visibleAttrs = computed(() =>
  expandedAttrs.value ? props.aggregations.attrs : props.aggregations.attrs.slice(0, 4)
)

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

// ── Active filters chips ──
interface ActiveFilter { id: string; label: string; group: 'brand' | 'attr'; value: string; attrKey?: string }

const activeFilters = computed<ActiveFilter[]>(() => {
  const result: ActiveFilter[] = []
  selectedBrandNames.value.forEach(name => result.push({ id: `br_${name}`, label: name, group: 'brand', value: name }))
  Object.entries(attrSelections).forEach(([key, vals]) =>
    vals.forEach(v => result.push({ id: `attr_${key}_${v}`, label: v, group: 'attr', value: v, attrKey: key }))
  )
  return result
})

function removeFilter(f: ActiveFilter) {
  if (f.group === 'attr' && f.attrKey) {
    const arr = attrSelections[f.attrKey]
    if (arr) { const i = arr.indexOf(f.value); if (i !== -1) arr.splice(i, 1) }
  } else {
    const id = brandNameToId.value.get(f.value)
    if (id !== undefined) {
      const i = selectedBrandIds.value.indexOf(id)
      if (i !== -1) selectedBrandIds.value.splice(i, 1)
    }
  }
  emit('change', currentState())
}

function clearAll() {
  selectedBrandIds.value = []
  Object.keys(attrSelections).forEach(k => { attrSelections[k] = [] })
  emit('change', currentState())
}

// 點外部關閉 dropdown
function onOutsideClick() { openAttrKey.value = null }
onMounted(()   => document.addEventListener('click', onOutsideClick))
onUnmounted(() => document.removeEventListener('click', onOutsideClick))

// ── Emit ──
export interface FilterState {
  brandIds: number[]
  attrs: Record<string, string[]>
}

const emit = defineEmits<{ change: [FilterState] }>()

function currentState(): FilterState {
  return {
    brandIds: [...selectedBrandIds.value],
    attrs: Object.fromEntries(Object.entries(attrSelections).map(([k, v]) => [k, [...v]])),
  }
}
</script>
