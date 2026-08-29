import type { Config } from 'tailwindcss'

/**
 * 網站級 Design Tokens。色彩/陰影數值來自 assets/css/main.css 的 CSS variables，
 * 這裡只是把它們也註冊成 Tailwind utility class（bg-primary、text-danger…），
 * 方便元件直接用 class 寫，不用到處寫 style="color: var(--color-x)"。
 *
 * 純新增（extend），不覆寫 Tailwind 內建的 red/gray 色盤，既有頁面用到的
 * color="red" / text-gray-500 等寫法完全不受影響。
 *
 * 這些顏色值都是 var(--color-x) 字串，不是 Tailwind 認得出的 hex/rgb，所以套用
 * 「顏色/透明度」語法（例如 bg-danger/10）在編譯期會被直接忽略、不產生任何 CSS
 * 規則——不是報錯，是背景整個消失，很難察覺。需要淡色底時改用下面已經配好的
 * -soft token（bg-danger-soft），不要在這些 token 後面加 /數字。
 */
export default <Partial<Config>>{
  theme: {
    extend: {
      colors: {
        // 特意不叫 "primary"：@nuxt/ui v2 把 "primary" 當保留字，會用它自己的
        // rgb(var(...) / alpha) 動態換色機制接管，導致我們自訂的 hex 字串失效
        // （bg-primary 編譯出 rgb(var(--color-primary-DEFAULT) / 1) 這種指向不存在
        // 變數的壞規則）。改叫 "brand" 完全繞開這個保留字衝突。
        brand: {
          DEFAULT: 'var(--color-primary)',
          hover: 'var(--color-primary-hover)',
          light: 'var(--color-primary-light)',
          lighter: 'var(--color-primary-lighter)',
        },
        danger: 'var(--color-danger)',
        'danger-soft': 'var(--color-danger-soft)',
        success: 'var(--color-success)',
        'success-soft': 'var(--color-success-soft)',
        warning: 'var(--color-warning)',
        launch: 'var(--color-launch)',
        'launch-soft': 'var(--color-launch-soft)',
        heading: 'var(--color-heading)',
        body: 'var(--color-text)',
        subtle: 'var(--color-text-secondary)',
        muted: 'var(--color-text-muted)',
        border: 'var(--color-border)',
        background: 'var(--color-background)',
        card: 'var(--color-card)',
      },
      boxShadow: {
        card: 'var(--shadow-card)',
        'card-hover': 'var(--shadow-card-hover)',
      },
      fontFamily: {
        sans: ['"Noto Sans TC"', '"Noto Sans"', 'sans-serif'],
      },
    },
  },
}
