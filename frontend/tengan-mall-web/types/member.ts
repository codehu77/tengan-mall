export interface MemberProfile {
  id: number
  username: string
  phone: string | null
  nickname: string
  avatarUrl: string | null
}

export interface MemberAddress {
  id: number
  receiverName: string
  receiverPhone: string
  city: string
  district: string
  postalCode: string
  street: string
  isDefault: boolean
}

export interface AddressFormPayload {
  receiverName: string
  receiverPhone: string
  city: string
  district: string
  postalCode: string
  street: string
  isDefault: boolean
}
