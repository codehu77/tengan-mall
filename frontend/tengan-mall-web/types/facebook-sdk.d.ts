/** Facebook JS SDK 的最小 ambient 型別宣告，只涵蓋這個專案實際用到的 API 表面。 */
export {}

declare global {
  interface Window {
    fbAsyncInit?: () => void
    FB?: {
      init(config: { appId: string; cookie?: boolean; xfbml?: boolean; version: string }): void
      login(
        callback: (response: { authResponse?: { accessToken: string; userID: string } }) => void,
        options?: { scope?: string },
      ): void
    }
  }
}
