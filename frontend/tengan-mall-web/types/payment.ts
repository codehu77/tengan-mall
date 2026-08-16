import type { PaymentMethod } from '~/types/order'

export interface PaymentMethodsResult {
  methods: PaymentMethod[]
}

export interface EcpayFormData {
  actionUrl: string
  fields: Record<string, string>
}

export interface InitiatePaymentResult {
  method: PaymentMethod
  ecpayForm: EcpayFormData | null
  linePayPaymentUrl: string | null
}

/** 1=PENDING 2=PAID（對齊 tengan-payment PaymentStatus）。 */
export type PaymentStatusCode = 1 | 2

export interface PaymentStatusResult {
  orderSn: string
  method: PaymentMethod
  status: PaymentStatusCode
  amount: number
}
