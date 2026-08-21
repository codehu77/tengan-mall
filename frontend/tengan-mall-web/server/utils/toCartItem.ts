import type { CartItem } from '../../types/cart'
import type { PublicSeckillSku } from './seckill'

/** tengan-cart 回應欄位（name/mainImage）跟前端既有 CartItem 型別（skuName/image）欄位名稱不同，集中在這裡轉換一次。 */
export interface BackendCartLine {
  itemId: number
  skuId: number
  spuId: number | null
  name: string | null
  price: number | null
  mainImage: string | null
  count: number
  checked: boolean
  specText: string | null
  available: boolean
}

export function toCartItem(line: BackendCartLine, seckillMap: Map<number, PublicSeckillSku>): CartItem {
  const seckill = seckillMap.get(line.skuId)
  return {
    itemId: line.itemId,
    skuId: line.skuId,
    spuId: line.spuId,
    skuName: line.name ?? '（商品已下架）',
    price: line.price ?? 0,
    image: line.mainImage ?? '',
    count: line.count,
    checked: line.checked,
    available: line.available,
    seckillPrice: seckill?.seckillPrice,
    seckillRemaining: seckill?.remaining,
  }
}
