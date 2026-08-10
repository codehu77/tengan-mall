<script setup lang="ts">
import type { OrderDetail } from "@/api/order";

defineProps<{
  detail: OrderDetail;
}>();

const paymentMethodLabels: Record<string, string> = {
  linepay: "LINE Pay",
  credit_card: "信用卡",
  cod: "貨到付款"
};

function formatTime(iso?: string) {
  return iso ? new Date(iso).toLocaleString("zh-TW", { hour12: false }) : "-";
}
</script>

<template>
  <div>
    <el-descriptions title="基本資訊" :column="2" border>
      <el-descriptions-item label="訂單編號">{{ detail.orderSn }}</el-descriptions-item>
      <el-descriptions-item label="會員 ID">{{ detail.memberId }}</el-descriptions-item>
      <el-descriptions-item label="付款方式">
        {{ paymentMethodLabels[detail.paymentMethod] ?? detail.paymentMethod }}
      </el-descriptions-item>
      <el-descriptions-item label="建立時間">{{ formatTime(detail.createdAt) }}</el-descriptions-item>
      <el-descriptions-item label="取消原因" v-if="detail.cancelReason">
        {{ detail.cancelReason }}
      </el-descriptions-item>
      <el-descriptions-item label="確認收貨時間" v-if="detail.receiptTime">
        {{ formatTime(detail.receiptTime) }}
      </el-descriptions-item>
    </el-descriptions>

    <el-descriptions title="收件資訊" :column="2" border class="mt-4">
      <el-descriptions-item label="收件人">{{ detail.receiverName }}</el-descriptions-item>
      <el-descriptions-item label="電話">{{ detail.receiverPhone }}</el-descriptions-item>
      <el-descriptions-item label="地址" :span="2">
        {{ detail.city }}{{ detail.district }}{{ detail.street }}
        <span v-if="detail.postalCode">（{{ detail.postalCode }}）</span>
      </el-descriptions-item>
      <el-descriptions-item label="備註" :span="2" v-if="detail.remark">
        {{ detail.remark }}
      </el-descriptions-item>
    </el-descriptions>

    <el-descriptions title="金額" :column="3" border class="mt-4">
      <el-descriptions-item label="小計">NT$ {{ detail.totalAmount }}</el-descriptions-item>
      <el-descriptions-item label="折扣">NT$ {{ detail.discountAmount }}</el-descriptions-item>
      <el-descriptions-item label="應付">NT$ {{ detail.payAmount }}</el-descriptions-item>
    </el-descriptions>

    <div class="mt-4 mb-2" style="font-weight: bold">商品明細</div>
    <el-table :data="detail.items" border size="small">
      <el-table-column label="圖片" width="70">
        <template #default="{ row }">
          <el-image v-if="row.skuImage" :src="row.skuImage" style="width: 40px; height: 40px" fit="cover" />
        </template>
      </el-table-column>
      <el-table-column label="規格名稱" prop="skuName" min-width="200" />
      <el-table-column label="單價" width="100">
        <template #default="{ row }">NT$ {{ row.price }}</template>
      </el-table-column>
      <el-table-column label="數量" prop="count" width="80" />
      <el-table-column label="小計" width="100">
        <template #default="{ row }">NT$ {{ row.subtotal }}</template>
      </el-table-column>
    </el-table>
  </div>
</template>
