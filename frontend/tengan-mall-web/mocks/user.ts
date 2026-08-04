export interface UserInfo {
  userId: number
  username: string
}

// Mock 帳號（任意帳密都能登入，模擬後端回傳）
export const MOCK_USER: UserInfo = {
  userId: 1001,
  username: 'test@tengan.com',
}
