import type { Ref } from 'vue'

/**
 * 倒數到 target（ISO 字串/Date/null，null 或過去時間視為已結束）。原本 SeckillSection.vue／
 * item/[spuId].vue 各自複製一份「remaining ref + setInterval」邏輯，這次「距開賣」倒數會再多兩處
 * （商品明細頁、首頁即將開賣預告區塊），抽成共用 composable 統一維護。
 *
 * hh 沒有 24 小時上限（開賣前可能還有好幾天），跟既有秒殺倒數行為一致。
 */
export function useCountdown(target: Ref<string | Date | null | undefined>) {
  const remaining = ref(0)

  function update() {
    const value = target.value
    if (!value) {
      remaining.value = 0
      return
    }
    const targetMs = value instanceof Date ? value.getTime() : new Date(value).getTime()
    remaining.value = Math.max(0, Math.floor((targetMs - Date.now()) / 1000))
  }

  const hh = computed(() => String(Math.floor(remaining.value / 3600)).padStart(2, '0'))
  const mm = computed(() => String(Math.floor((remaining.value % 3600) / 60)).padStart(2, '0'))
  const ss = computed(() => String(remaining.value % 60).padStart(2, '0'))
  const isPast = computed(() => remaining.value <= 0)

  let timer: ReturnType<typeof setInterval> | null = null

  onMounted(() => {
    update()
    timer = setInterval(update, 1000)
  })
  onUnmounted(() => {
    if (timer) clearInterval(timer)
  })

  watch(target, update)

  return { remaining, hh, mm, ss, isPast, update }
}
