/** Google Identity Services (GIS) 的最小 ambient 型別宣告，只涵蓋這個專案實際用到的 API 表面。 */
export {}

declare global {
  interface Window {
    google?: {
      accounts: {
        id: {
          initialize(config: {
            client_id: string
            callback: (response: { credential: string }) => void
          }): void
          renderButton(
            parent: HTMLElement,
            options?: { theme?: string; size?: string; width?: number | string; logo_alignment?: string },
          ): void
        }
      }
    }
  }
}
