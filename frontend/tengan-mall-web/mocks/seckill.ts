export interface SeckillSkuInfo {
  skuId: number
  skuName: string
  skuTitle: string
  skuDefaultImg: string
  price: number
  saleCount: number
}

export interface SeckillItem {
  id: number
  skuId: number
  seckillPrice: number
  seckillCount: number
  seckillLimit: number
  startTime: number
  endTime: number
  randomCode: string
  skuInfoVo: SeckillSkuInfo
}

export interface SeckillSession {
  sessionId: number
  time: string
  status: 'ongoing' | 'upcoming' | 'tomorrow'
  label: string
  active: boolean
  startTime: number
  endTime: number
}

function atHour(offsetDays: number, h: number) {
  const d = new Date()
  d.setDate(d.getDate() + offsetDays)
  d.setHours(h, 0, 0, 0)
  return d.getTime()
}

// 固定場次時刻表：每天 4 個場次
const SCHEDULE = [
  { time: '00:00', h: 0,  endH: 6  },
  { time: '06:00', h: 6,  endH: 12 },
  { time: '12:00', h: 12, endH: 18 },
  { time: '18:00', h: 18, endH: 24 },
]

function buildSessions(): SeckillSession[] {
  const now = Date.now()
  const result: SeckillSession[] = []
  let foundActive = false

  // 今天 + 明天，共 8 個場次候選，取前 4 個未過期的
  for (let day = 0; day <= 1 && result.length < 4; day++) {
    for (const s of SCHEDULE) {
      const start = atHour(day, s.h)
      const end   = s.endH === 24 ? atHour(day + 1, 0) : atHour(day, s.endH)
      if (end <= now) continue   // 已結束，跳過

      const isOngoing = start <= now && now < end
      const isTomorrow = day === 1

      let status: SeckillSession['status']
      let label: string
      if (isOngoing)       { status = 'ongoing';  label = '現正搶購' }
      else if (!isTomorrow){ status = 'upcoming'; label = '即將開搶' }
      else                  { status = 'tomorrow'; label = '明天開搶' }

      const active = !foundActive
      if (active) foundActive = true

      result.push({ sessionId: result.length + 1, time: s.time, status, label, active, startTime: start, endTime: end })
      if (result.length === 4) break
    }
  }
  return result
}

export const MOCK_SECKILL_SESSIONS: SeckillSession[] = buildSessions()

function items(sessionIdx: number, list: Omit<SeckillItem, 'startTime' | 'endTime'>[]): SeckillItem[] {
  const s = MOCK_SECKILL_SESSIONS[sessionIdx]
  return list.map(i => ({ ...i, startTime: s?.startTime ?? 0, endTime: s?.endTime ?? 0 }))
}

export const MOCK_SECKILL_ITEMS_BY_SESSION: Record<number, SeckillItem[]> = {
  // 場次 1：手機 & 穿戴裝置
  1: items(0, [
    { id: 101, skuId: 1,  seckillPrice: 29900, seckillCount: 50,  seckillLimit: 1, randomCode: 's1a01', skuInfoVo: { skuId: 1,  skuName: 'Apple iPhone 15 Pro 256GB 原色鈦金屬',       skuTitle: 'iPhone 15 Pro',    skuDefaultImg: 'https://placehold.co/200x200?text=iPhone+15+Pro', price: 39900, saleCount: 1823 } },
    { id: 102, skuId: 2,  seckillPrice: 21900, seckillCount: 80,  seckillLimit: 1, randomCode: 's1a02', skuInfoVo: { skuId: 2,  skuName: 'Samsung Galaxy S24 Ultra 512GB 鈦黑',        skuTitle: 'S24 Ultra',        skuDefaultImg: 'https://placehold.co/200x200?text=S24+Ultra',    price: 35900, saleCount: 1241 } },
    { id: 103, skuId: 3,  seckillPrice: 9900,  seckillCount: 60,  seckillLimit: 1, randomCode: 's1a03', skuInfoVo: { skuId: 3,  skuName: 'Apple Watch Series 9 GPS 45mm 午夜色',       skuTitle: 'Apple Watch S9',   skuDefaultImg: 'https://placehold.co/200x200?text=Watch+S9',     price: 14900, saleCount: 1456 } },
    { id: 104, skuId: 4,  seckillPrice: 7999,  seckillCount: 100, seckillLimit: 1, randomCode: 's1a04', skuInfoVo: { skuId: 4,  skuName: 'Sony WH-1000XM5 無線降噪耳機 黑色限量版',    skuTitle: 'Sony WH-1000XM5', skuDefaultImg: 'https://placehold.co/200x200?text=Sony+XM5',     price: 11900, saleCount: 2341 } },
    { id: 105, skuId: 5,  seckillPrice: 5490,  seckillCount: 120, seckillLimit: 1, randomCode: 's1a05', skuInfoVo: { skuId: 5,  skuName: 'AirPods Pro 2 (USB-C) 主動降噪',              skuTitle: 'AirPods Pro 2',    skuDefaultImg: 'https://placehold.co/200x200?text=AirPods+Pro2', price: 8490,  saleCount: 3102 } },
    { id: 106, skuId: 6,  seckillPrice: 3490,  seckillCount: 200, seckillLimit: 2, randomCode: 's1a06', skuInfoVo: { skuId: 6,  skuName: 'Xiaomi 14 8GB/256GB 白色',                   skuTitle: 'Xiaomi 14',        skuDefaultImg: 'https://placehold.co/200x200?text=Xiaomi+14',    price: 7990,  saleCount: 890  } },
    { id: 107, skuId: 7,  seckillPrice: 12900, seckillCount: 40,  seckillLimit: 1, randomCode: 's1a07', skuInfoVo: { skuId: 7,  skuName: 'ASUS ROG Phone 8 Pro 16GB/512GB 幻影黑',     skuTitle: 'ROG Phone 8 Pro',  skuDefaultImg: 'https://placehold.co/200x200?text=ROG+Phone8',   price: 22900, saleCount: 421  } },
    { id: 108, skuId: 8,  seckillPrice: 4290,  seckillCount: 150, seckillLimit: 2, randomCode: 's1a08', skuInfoVo: { skuId: 8,  skuName: 'Google Pixel 8a 128GB 翠綠色',               skuTitle: 'Pixel 8a',         skuDefaultImg: 'https://placehold.co/200x200?text=Pixel+8a',     price: 9990,  saleCount: 673  } },
  ]),

  // 場次 2：電腦 & 周邊
  2: items(1, [
    { id: 201, skuId: 11, seckillPrice: 27900, seckillCount: 30,  seckillLimit: 1, randomCode: 's2a01', skuInfoVo: { skuId: 11, skuName: 'MacBook Air 13吋 M3 晶片 8GB/256GB 午夜色', skuTitle: 'MacBook Air M3',   skuDefaultImg: 'https://placehold.co/200x200?text=MacBook+M3',   price: 37900, saleCount: 782  } },
    { id: 202, skuId: 12, seckillPrice: 18900, seckillCount: 50,  seckillLimit: 1, randomCode: 's2a02', skuInfoVo: { skuId: 12, skuName: 'ASUS Zenbook 14 OLED i7/16G/512G',           skuTitle: 'Zenbook 14 OLED',  skuDefaultImg: 'https://placehold.co/200x200?text=Zenbook+OLED', price: 29900, saleCount: 543  } },
    { id: 203, skuId: 13, seckillPrice: 3290,  seckillCount: 200, seckillLimit: 2, randomCode: 's2a03', skuInfoVo: { skuId: 13, skuName: 'Logitech MX Master 3S 無線滑鼠 石墨黑',       skuTitle: 'MX Master 3S',     skuDefaultImg: 'https://placehold.co/200x200?text=MX+Master3S',  price: 4990,  saleCount: 2187 } },
    { id: 204, skuId: 14, seckillPrice: 12900, seckillCount: 40,  seckillLimit: 1, randomCode: 's2a04', skuInfoVo: { skuId: 14, skuName: 'Samsung 27吋 4K IPS 顯示器 U27B850',          skuTitle: '4K 顯示器',        skuDefaultImg: 'https://placehold.co/200x200?text=4K+Monitor',   price: 19900, saleCount: 312  } },
    { id: 205, skuId: 15, seckillPrice: 1890,  seckillCount: 300, seckillLimit: 2, randomCode: 's2a05', skuInfoVo: { skuId: 15, skuName: 'Anker 140W GaN 氮化鎵充電器 三孔',             skuTitle: 'Anker 140W GaN',   skuDefaultImg: 'https://placehold.co/200x200?text=Anker+GaN',    price: 3490,  saleCount: 4521 } },
    { id: 206, skuId: 16, seckillPrice: 5490,  seckillCount: 80,  seckillLimit: 1, randomCode: 's2a06', skuInfoVo: { skuId: 16, skuName: 'Keychron Q5 Max 鋁合金機械鍵盤 茶軸',          skuTitle: 'Keychron Q5 Max',  skuDefaultImg: 'https://placehold.co/200x200?text=Keychron+Q5',  price: 8490,  saleCount: 876  } },
    { id: 207, skuId: 17, seckillPrice: 3990,  seckillCount: 60,  seckillLimit: 1, randomCode: 's2a07', skuInfoVo: { skuId: 17, skuName: 'WD_BLACK SN850X 2TB NVMe SSD',               skuTitle: 'SN850X 2TB',       skuDefaultImg: 'https://placehold.co/200x200?text=WD+SN850X',    price: 6990,  saleCount: 1043 } },
    { id: 208, skuId: 18, seckillPrice: 890,   seckillCount: 500, seckillLimit: 3, randomCode: 's2a08', skuInfoVo: { skuId: 18, skuName: 'Baseus 10000mAh 行動電源 22.5W 快充 白色',     skuTitle: 'Baseus 行動電源',  skuDefaultImg: 'https://placehold.co/200x200?text=Baseus+PB',    price: 1890,  saleCount: 7832 } },
  ]),

  // 場次 3：家電
  3: items(2, [
    { id: 301, skuId: 21, seckillPrice: 14900, seckillCount: 80,  seckillLimit: 1, randomCode: 's3a01', skuInfoVo: { skuId: 21, skuName: 'Dyson V15 Detect 無線吸塵器 黃色',            skuTitle: 'Dyson V15',        skuDefaultImg: 'https://placehold.co/200x200?text=Dyson+V15',    price: 21900, saleCount: 678  } },
    { id: 302, skuId: 22, seckillPrice: 8900,  seckillCount: 100, seckillLimit: 1, randomCode: 's3a02', skuInfoVo: { skuId: 22, skuName: 'Philips 3000 Series 空氣清淨機 AC3033',        skuTitle: 'Philips AC3033',   skuDefaultImg: 'https://placehold.co/200x200?text=Philips+AP',   price: 13900, saleCount: 1234 } },
    { id: 303, skuId: 23, seckillPrice: 4990,  seckillCount: 150, seckillLimit: 1, randomCode: 's3a03', skuInfoVo: { skuId: 23, skuName: 'Panasonic 變頻無風扇電風扇 F-CR7MLD',           skuTitle: 'Panasonic 電風扇', skuDefaultImg: 'https://placehold.co/200x200?text=Panasonic+Fan', price: 9900,  saleCount: 456  } },
    { id: 304, skuId: 24, seckillPrice: 3290,  seckillCount: 200, seckillLimit: 2, randomCode: 's3a04', skuInfoVo: { skuId: 24, skuName: 'Nespresso Vertuo Next 膠囊咖啡機 黑色',        skuTitle: 'Nespresso Vertuo', skuDefaultImg: 'https://placehold.co/200x200?text=Nespresso',     price: 6490,  saleCount: 2109 } },
    { id: 305, skuId: 25, seckillPrice: 19900, seckillCount: 20,  seckillLimit: 1, randomCode: 's3a05', skuInfoVo: { skuId: 25, skuName: 'LG 樂金 蒸氣洗脫烘 滾筒洗衣機 15kg',           skuTitle: 'LG 洗脫烘 15kg',  skuDefaultImg: 'https://placehold.co/200x200?text=LG+WD15',      price: 32900, saleCount: 87   } },
    { id: 306, skuId: 26, seckillPrice: 2490,  seckillCount: 300, seckillLimit: 2, randomCode: 's3a06', skuInfoVo: { skuId: 26, skuName: 'Instant Pot Duo 7合1電子壓力鍋 6L',             skuTitle: 'Instant Pot 6L',  skuDefaultImg: 'https://placehold.co/200x200?text=InstantPot',   price: 4990,  saleCount: 3421 } },
    { id: 307, skuId: 27, seckillPrice: 6900,  seckillCount: 60,  seckillLimit: 1, randomCode: 's3a07', skuInfoVo: { skuId: 27, skuName: 'Roomba j7+ 自動集塵掃地機器人',                 skuTitle: 'Roomba j7+',       skuDefaultImg: 'https://placehold.co/200x200?text=Roomba+j7',    price: 14900, saleCount: 521  } },
    { id: 308, skuId: 28, seckillPrice: 1290,  seckillCount: 400, seckillLimit: 2, randomCode: 's3a08', skuInfoVo: { skuId: 28, skuName: 'Philips 舒眠檯燈 HF3520 日光型 LED',            skuTitle: 'Philips 舒眠燈',   skuDefaultImg: 'https://placehold.co/200x200?text=Philips+LED',  price: 2990,  saleCount: 6780 } },
  ]),

  // 場次 4：運動 & 戶外
  4: items(3, [
    { id: 401, skuId: 31, seckillPrice: 7980,  seckillCount: 200, seckillLimit: 1, randomCode: 's4a01', skuInfoVo: { skuId: 31, skuName: 'Nintendo Switch OLED 白色主機 + 健身環大冒險',  skuTitle: 'Switch OLED 健身組', skuDefaultImg: 'https://placehold.co/200x200?text=Switch+OLED',  price: 12980, saleCount: 3201 } },
    { id: 402, skuId: 32, seckillPrice: 4990,  seckillCount: 100, seckillLimit: 1, randomCode: 's4a02', skuInfoVo: { skuId: 32, skuName: 'Garmin Forerunner 265 GPS 運動錶 黑色',          skuTitle: 'Garmin FR265',     skuDefaultImg: 'https://placehold.co/200x200?text=Garmin+FR265', price: 12900, saleCount: 892  } },
    { id: 403, skuId: 33, seckillPrice: 3490,  seckillCount: 150, seckillLimit: 2, randomCode: 's4a03', skuInfoVo: { skuId: 33, skuName: 'HOKA Clifton 9 慢跑鞋 男款 冰川藍/黑',          skuTitle: 'HOKA Clifton 9',   skuDefaultImg: 'https://placehold.co/200x200?text=HOKA+C9',      price: 5490,  saleCount: 1456 } },
    { id: 404, skuId: 34, seckillPrice: 1290,  seckillCount: 300, seckillLimit: 3, randomCode: 's4a04', skuInfoVo: { skuId: 34, skuName: 'Nike Dri-FIT 快乾運動短袖 黑色 L',               skuTitle: 'Nike Dri-FIT 短袖', skuDefaultImg: 'https://placehold.co/200x200?text=Nike+DriFIT',  price: 2490,  saleCount: 5678 } },
    { id: 405, skuId: 35, seckillPrice: 8900,  seckillCount: 40,  seckillLimit: 1, randomCode: 's4a05', skuInfoVo: { skuId: 35, skuName: 'Specialized Rockhopper Comp 27.5 登山車',        skuTitle: 'Rockhopper Comp',  skuDefaultImg: 'https://placehold.co/200x200?text=Specialized',  price: 18900, saleCount: 234  } },
    { id: 406, skuId: 36, seckillPrice: 2190,  seckillCount: 200, seckillLimit: 2, randomCode: 's4a06', skuInfoVo: { skuId: 36, skuName: 'Osprey Talon 22 登山背包 黑色',                  skuTitle: 'Osprey Talon 22',  skuDefaultImg: 'https://placehold.co/200x200?text=Osprey+Talon', price: 4490,  saleCount: 876  } },
    { id: 407, skuId: 37, seckillPrice: 4590,  seckillCount: 80,  seckillLimit: 1, randomCode: 's4a07', skuInfoVo: { skuId: 37, skuName: 'Whoop 4.0 健康手環 12個月訂閱組',                skuTitle: 'Whoop 4.0',        skuDefaultImg: 'https://placehold.co/200x200?text=Whoop+4',      price: 8900,  saleCount: 321  } },
    { id: 408, skuId: 38, seckillPrice: 990,   seckillCount: 500, seckillLimit: 3, randomCode: 's4a08', skuInfoVo: { skuId: 38, skuName: 'Nalgene 1L 廣口水瓶 BPA Free 消光黑',            skuTitle: 'Nalgene 1L',       skuDefaultImg: 'https://placehold.co/200x200?text=Nalgene+1L',   price: 1890,  saleCount: 4321 } },
  ]),
}

export interface SeckillOrder {
  orderSn: string
  createTime: string
  skuId: number
  skuName: string
  skuImage: string
  originalPrice: number
  seckillPrice: number
  count: number
  sessionName: string
}

export const MOCK_SECKILL_ORDER: SeckillOrder = {
  orderSn: 'TG20240628SK0001',
  createTime: new Date().toISOString(),
  skuId: 5,
  skuName: 'Sony WH-1000XM5 無線降噪耳機 黑色限量版',
  skuImage: 'https://placehold.co/100x100?text=Sony',
  originalPrice: 11900,
  seckillPrice: 7999,
  count: 1,
  sessionName: '午夜場特賣',
}
