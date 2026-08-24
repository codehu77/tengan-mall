<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from "vue";
import { useRouter, type RouteLocationRaw } from "vue-router";
import echarts from "@/plugins/echarts";
import { getDashboardSummary, type DashboardSummary } from "@/api/dashboard";

defineOptions({
  name: "Welcome"
});

const router = useRouter();

const loading = ref(true);
const summary = ref<DashboardSummary>({
  todayNewOrderCount: 0,
  todayRevenue: 0,
  monthRevenue: 0,
  yearRevenue: 0,
  pendingShipmentCount: 0,
  lowStockSkuCount: 0,
  revenueTrend: []
});

/** 今天 00:00 ~ 現在，給「今日新訂單」卡片點擊導覽帶日期區間用。 */
function todayRange(): [string, string] {
  const now = new Date();
  const startOfToday = new Date(now.getFullYear(), now.getMonth(), now.getDate());
  return [startOfToday.toISOString(), now.toISOString()];
}

const cards = [
  {
    key: "todayNewOrderCount" as const,
    label: "今日新訂單",
    icon: "ep:shopping-cart",
    color: "#409eff",
    format: (v: number) => v.toString(),
    to: (): RouteLocationRaw => {
      const [from, to] = todayRange();
      return { path: "/orders/list", query: { from, to } };
    }
  },
  {
    key: "todayRevenue" as const,
    label: "今日營收",
    icon: "ep:money",
    color: "#e6a23c",
    format: (v: number) => `NT$ ${v.toLocaleString()}`,
    to: null
  },
  {
    key: "monthRevenue" as const,
    label: "本月營收",
    icon: "ep:money",
    color: "#e6a23c",
    format: (v: number) => `NT$ ${v.toLocaleString()}`,
    to: null
  },
  {
    key: "yearRevenue" as const,
    label: "本年營收",
    icon: "ep:money",
    color: "#e6a23c",
    format: (v: number) => `NT$ ${v.toLocaleString()}`,
    to: null
  },
  {
    key: "pendingShipmentCount" as const,
    label: "待出貨訂單",
    icon: "ep:box",
    color: "#909399",
    format: (v: number) => v.toString(),
    to: (): RouteLocationRaw => ({ path: "/orders/list", query: { status: 2 } })
  },
  {
    key: "lowStockSkuCount" as const,
    label: "低庫存商品",
    icon: "ep:warning",
    color: "#f56c6c",
    format: (v: number) => v.toString(),
    to: (): RouteLocationRaw => ({ path: "/inventory/stock", query: { onlyLowStock: "true" } })
  }
];

function onCardClick(card: (typeof cards)[number]) {
  if (card.to) {
    router.push(card.to());
  }
}

const chartRef = ref<HTMLDivElement>();
let chartInstance: echarts.ECharts | null = null;

function renderChart() {
  if (!chartRef.value) {
    return;
  }
  if (!chartInstance) {
    chartInstance = echarts.init(chartRef.value);
  }
  const dates = summary.value.revenueTrend.map(p =>
    new Date(p.date).toLocaleDateString("zh-TW", { month: "2-digit", day: "2-digit" })
  );
  const revenues = summary.value.revenueTrend.map(p => p.revenue);
  chartInstance.setOption({
    tooltip: {
      trigger: "axis",
      valueFormatter: (v: number) => `NT$ ${v.toLocaleString()}`
    },
    grid: { left: 50, right: 20, top: 20, bottom: 30 },
    xAxis: { type: "category", data: dates },
    yAxis: { type: "value" },
    series: [
      {
        name: "營收",
        type: "line",
        smooth: true,
        areaStyle: {},
        data: revenues,
        itemStyle: { color: "#e6a23c" }
      }
    ]
  });
}

function onResize() {
  chartInstance?.resize();
}

onMounted(async () => {
  try {
    summary.value = await getDashboardSummary();
    renderChart();
  } finally {
    loading.value = false;
  }
  window.addEventListener("resize", onResize);
});

onBeforeUnmount(() => {
  window.removeEventListener("resize", onResize);
  chartInstance?.dispose();
  chartInstance = null;
});
</script>

<template>
  <div v-loading="loading" class="dashboard-summary">
    <el-row :gutter="16">
      <el-col v-for="card in cards" :key="card.key" :xs="24" :sm="12" :md="8" :lg="4" :xl="4">
        <el-card
          class="summary-card"
          :class="{ 'summary-card-clickable': card.to }"
          shadow="hover"
          @click="onCardClick(card)"
        >
          <div class="summary-card-body">
            <IconifyIconOnline :icon="card.icon" :style="{ color: card.color }" class="summary-card-icon" />
            <div>
              <div class="summary-card-value">{{ card.format(summary[card.key]) }}</div>
              <div class="summary-card-label">{{ card.label }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="mt-2">
      <template #header>近 7 天營收趨勢</template>
      <div ref="chartRef" class="revenue-chart" />
    </el-card>
  </div>
</template>

<style scoped>
.dashboard-summary {
  min-height: 120px;
}

.summary-card {
  margin-bottom: 16px;
}

.summary-card-clickable {
  cursor: pointer;
  transition: transform 0.15s ease;
}

.summary-card-clickable:hover {
  transform: translateY(-2px);
}

.summary-card-body {
  display: flex;
  align-items: center;
  gap: 12px;
}

.summary-card-icon {
  font-size: 32px;
}

.summary-card-value {
  font-size: 22px;
  font-weight: 600;
}

.summary-card-label {
  font-size: 13px;
  color: var(--el-text-color-secondary);
}

.revenue-chart {
  width: 100%;
  height: 300px;
}
</style>
