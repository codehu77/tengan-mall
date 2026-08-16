<template>
  <div class="space-y-4">

      <div>
        <h1 class="text-2xl font-semibold text-gray-800">我的點數</h1>
        <p class="text-base text-gray-400 mt-1">查看您的點數餘額、會員回饋比例與交易明細</p>
      </div>

      <!-- 開發除錯用：模擬各區塊載入失敗，驗證 Error State -->
      <div v-if="isDev" class="bg-gray-800 rounded-lg px-4 py-2.5 flex flex-wrap items-center gap-2 text-xs">
        <span class="text-gray-400 mr-1">開發除錯：</span>
        <button class="px-2.5 py-1 rounded bg-gray-700 text-gray-200 hover:bg-gray-600 transition" @click="store.loadSummary({ simulateError: true })">摘要失敗</button>
        <button class="px-2.5 py-1 rounded bg-gray-700 text-gray-200 hover:bg-gray-600 transition" @click="store.loadExpiring({ simulateError: true })">到期清單失敗</button>
        <button class="px-2.5 py-1 rounded bg-gray-700 text-gray-200 hover:bg-gray-600 transition" @click="store.loadTransactions({ simulateError: true })">交易明細失敗</button>
        <button class="px-2.5 py-1 rounded bg-gray-700 text-gray-200 hover:bg-gray-600 transition" @click="store.loadFaq({ simulateError: true })">FAQ 失敗</button>
        <button class="px-2.5 py-1 rounded bg-red-700 text-white hover:bg-red-600 transition ml-auto" @click="store.initAll()">全部重新整理</button>
      </div>

      <!-- 摘要卡片 -->
      <PointsSummaryCards
        :summary="store.summary"
        :loading="store.summaryLoading"
        :error="store.summaryError"
        @retry="store.loadSummary()"
      />

      <!-- 會員資格 + 回饋比較 -->
      <div class="grid grid-cols-1 lg:grid-cols-[280px_1fr] gap-4 items-stretch">
        <PointsMemberTierCard
          :tier="store.tierCurrent"
          :loading="store.tierLoading"
          :error="store.tierError"
          @retry="store.loadTier()"
        />
        <PointsTierComparisonTable
          :benefits="store.tierBenefits"
          :loading="store.tierLoading"
          :error="store.tierError"
          @retry="store.loadTier()"
        />
      </div>

      <!-- 即將到期點數 -->
      <PointsExpiringBatchPanel
        :batches="store.expiringBatches"
        :loading="store.expiringLoading"
        :error="store.expiringError"
        @retry="store.loadExpiring()"
      />

      <!-- 點數交易明細 -->
      <div class="space-y-3">
        <h2 class="text-xl font-bold text-gray-800">點數交易明細</h2>

        <PointsTransactionFilterBar
          :model-value="store.filter"
          :counts="store.transactionCounts"
          @update:category="store.setFilterCategory($event.type, $event.status)"
          @update:date-range="store.setDateRange"
          @update:keyword="store.setKeyword"
        />

        <PointsTransactionList
          :transactions="store.transactions"
          :loading="store.transactionsLoading"
          :error="store.transactionsError"
          :page="store.filter.page"
          :page-size="store.filter.pageSize"
          :total="store.transactionsTotal"
          :total-pages="store.totalPages"
          @retry="store.loadTransactions()"
          @select="store.openTransactionDetail"
          @page-change="store.setPage"
        />
      </div>

      <!-- FAQ -->
      <PointsFaqAccordion
        :faq-list="store.faqList"
        :loading="store.faqLoading"
        :error="store.faqError"
        @retry="store.loadFaq()"
      />

    <PointsTransactionDetailDialog
      :open="store.isDetailOpen"
      :transaction="store.selectedTransaction"
      :loading="store.detailLoading"
      :error="store.detailError"
      @close="store.closeDetail()"
      @retry="store.retryDetail()"
    />
  </div>
</template>

<script setup lang="ts">
definePageMeta({ middleware: 'auth', layout: 'member' })

const store = usePointsStore()
const isDev = import.meta.dev

store.initAll()
</script>
