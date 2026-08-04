export interface ListItem {
  avatar: string;
  title: string;
  datetime: string;
  type: string;
  description: string;
  status?: "primary" | "success" | "warning" | "info" | "danger";
  extra?: string;
}

export interface TabItem {
  key: string;
  name: string;
  list: ListItem[];
  emptyText: string;
}

export const noticesData: TabItem[] = [
  {
    key: "1",
    name: "通知",
    list: [],
    emptyText: "暫無通知"
  },
  {
    key: "2",
    name: "訊息",
    list: [
      {
        avatar: "https://xiaoxian521.github.io/hyperlink/svg/smile1.svg",
        title: "小銘 評論了你",
        description: "誠在於心，信在於行，誠信在於心行合一。",
        datetime: "今天",
        type: "2"
      },
      {
        avatar: "https://xiaoxian521.github.io/hyperlink/svg/smile2.svg",
        title: "李白 回覆了你",
        description: "長風破浪會有時，直掛雲帆濟滄海。",
        datetime: "昨天",
        type: "2"
      },
      {
        avatar: "https://xiaoxian521.github.io/hyperlink/svg/smile5.svg",
        title: "標題",
        description:
          "請將滑鼠移動到此處，以便測試超長的訊息在此處將如何處理。本例中設定的描述最大行數為2，超過2行的描述內容將被省略並且可以通過tooltip檢視完整內容",
        datetime: "時間",
        type: "2"
      }
    ],
    emptyText: "暫無訊息"
  },
  {
    key: "3",
    name: "待辦",
    list: [
      {
        avatar: "",
        title: "第三方緊急程式碼變更",
        description:
          "小林提交於 2024-05-10，需在 2024-05-11 前完成程式碼變更任務",
        datetime: "",
        extra: "馬上到期",
        status: "danger",
        type: "3"
      },
      {
        avatar: "",
        title: "版本釋出",
        description: "指派小銘於 2024-06-18 前完成更新併發布",
        datetime: "",
        extra: "已耗時 8 天",
        status: "warning",
        type: "3"
      },
      {
        avatar: "",
        title: "新功能開發",
        description: "開發多租戶管理",
        datetime: "",
        extra: "進行中",
        type: "3"
      },
      {
        avatar: "",
        title: "任務名稱",
        description: "任務需要在 2030-10-30 10:00 前啟動",
        datetime: "",
        extra: "未開始",
        status: "info",
        type: "3"
      }
    ],
    emptyText: "暫無待辦"
  }
];
