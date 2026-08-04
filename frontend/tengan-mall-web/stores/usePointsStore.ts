import { defineStore } from 'pinia'
import { pointsService, type ServiceOptions } from '~/services/pointsService'
import type {
  PointAccountSummary,
  TierInfo,
  TierBenefit,
  PointBatch,
  PointFaqItem,
  PointTransaction,
  PointTransactionFilterType,
  PointDateRangeFilter,
  TransactionQuery,
} from '~/types/points'

const DEFAULT_PAGE_SIZE = 8

export const usePointsStore = defineStore('points', () => {
  // 摘要卡片：可用 / 待入帳 / 即將到期
  const summary = ref<PointAccountSummary | null>(null)
  const summaryLoading = ref(false)
  const summaryError = ref<string | null>(null)

  // 會員資格與回饋比例
  const tierCurrent = ref<TierInfo | null>(null)
  const tierBenefits = ref<TierBenefit[]>([])
  const tierLoading = ref(false)
  const tierError = ref<string | null>(null)

  // 即將到期點數批次
  const expiringBatches = ref<PointBatch[]>([])
  const expiringLoading = ref(false)
  const expiringError = ref<string | null>(null)

  // 點數使用規則 FAQ
  const faqList = ref<PointFaqItem[]>([])
  const faqLoading = ref(false)
  const faqError = ref<string | null>(null)

  // 交易明細列表 + 篩選條件
  const transactions = ref<PointTransaction[]>([])
  const transactionsTotal = ref(0)
  const transactionsLoading = ref(false)
  const transactionsError = ref<string | null>(null)

  const filter = reactive<TransactionQuery>({
    type: 'ALL',
    dateRange: 'ALL',
    keyword: '',
    page: 1,
    pageSize: DEFAULT_PAGE_SIZE,
  })

  const totalPages = computed(() => Math.max(1, Math.ceil(transactionsTotal.value / filter.pageSize)))

  // 交易詳情 Dialog
  const isDetailOpen = ref(false)
  const selectedTransaction = ref<PointTransaction | null>(null)
  const selectedTransactionId = ref<string | null>(null)
  const detailLoading = ref(false)
  const detailError = ref<string | null>(null)

  async function loadSummary(opts?: ServiceOptions) {
    summaryLoading.value = true
    summaryError.value = null
    try {
      summary.value = await pointsService.fetchSummary(opts)
    } catch (e) {
      summaryError.value = e instanceof Error ? e.message : '點數摘要載入失敗'
    } finally {
      summaryLoading.value = false
    }
  }

  async function loadTier(opts?: ServiceOptions) {
    tierLoading.value = true
    tierError.value = null
    try {
      const { current, benefits } = await pointsService.fetchTierInfo(opts)
      tierCurrent.value = current
      tierBenefits.value = benefits
    } catch (e) {
      tierError.value = e instanceof Error ? e.message : '會員資格載入失敗'
    } finally {
      tierLoading.value = false
    }
  }

  async function loadExpiring(opts?: ServiceOptions) {
    expiringLoading.value = true
    expiringError.value = null
    try {
      expiringBatches.value = await pointsService.fetchExpiringBatches(opts)
    } catch (e) {
      expiringError.value = e instanceof Error ? e.message : '即將到期點數載入失敗'
    } finally {
      expiringLoading.value = false
    }
  }

  async function loadFaq(opts?: ServiceOptions) {
    faqLoading.value = true
    faqError.value = null
    try {
      faqList.value = await pointsService.fetchFaq(opts)
    } catch (e) {
      faqError.value = e instanceof Error ? e.message : 'FAQ 載入失敗'
    } finally {
      faqLoading.value = false
    }
  }

  async function loadTransactions(opts?: ServiceOptions) {
    transactionsLoading.value = true
    transactionsError.value = null
    try {
      const result = await pointsService.fetchTransactions({ ...filter }, opts)
      transactions.value = result.items
      transactionsTotal.value = result.total
    } catch (e) {
      transactionsError.value = e instanceof Error ? e.message : '交易明細載入失敗'
    } finally {
      transactionsLoading.value = false
    }
  }

  function setFilterType(type: PointTransactionFilterType) {
    if (filter.type === type) return
    filter.type = type
    filter.page = 1
    loadTransactions()
  }

  function setDateRange(dateRange: PointDateRangeFilter) {
    if (filter.dateRange === dateRange) return
    filter.dateRange = dateRange
    filter.page = 1
    loadTransactions()
  }

  function setKeyword(keyword: string) {
    filter.keyword = keyword
    filter.page = 1
    loadTransactions()
  }

  function setPage(page: number) {
    if (page < 1 || page > totalPages.value || page === filter.page) return
    filter.page = page
    loadTransactions()
  }

  async function openTransactionDetail(id: string, opts?: ServiceOptions) {
    selectedTransactionId.value = id
    isDetailOpen.value = true
    detailLoading.value = true
    detailError.value = null
    selectedTransaction.value = null
    try {
      selectedTransaction.value = await pointsService.fetchTransactionDetail(id, opts)
    } catch (e) {
      detailError.value = e instanceof Error ? e.message : '交易詳情載入失敗'
    } finally {
      detailLoading.value = false
    }
  }

  function retryDetail() {
    if (selectedTransactionId.value) openTransactionDetail(selectedTransactionId.value)
  }

  function closeDetail() {
    isDetailOpen.value = false
    selectedTransaction.value = null
    selectedTransactionId.value = null
    detailError.value = null
  }

  function initAll() {
    loadSummary()
    loadTier()
    loadExpiring()
    loadFaq()
    loadTransactions()
  }

  return {
    summary,
    summaryLoading,
    summaryError,
    tierCurrent,
    tierBenefits,
    tierLoading,
    tierError,
    expiringBatches,
    expiringLoading,
    expiringError,
    faqList,
    faqLoading,
    faqError,
    transactions,
    transactionsTotal,
    transactionsLoading,
    transactionsError,
    filter,
    totalPages,
    isDetailOpen,
    selectedTransaction,
    selectedTransactionId,
    detailLoading,
    detailError,
    loadSummary,
    loadTier,
    loadExpiring,
    loadFaq,
    loadTransactions,
    setFilterType,
    setDateRange,
    setKeyword,
    setPage,
    openTransactionDetail,
    retryDetail,
    closeDetail,
    initAll,
  }
})
