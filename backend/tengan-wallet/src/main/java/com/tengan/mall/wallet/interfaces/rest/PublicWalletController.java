package com.tengan.mall.wallet.interfaces.rest;

import com.tengan.mall.wallet.application.tier.GetTierBenefitsUseCase;
import com.tengan.mall.wallet.interfaces.rest.dto.FaqResponse;
import com.tengan.mall.wallet.interfaces.rest.dto.FaqResponse.PointFaqItemResponse;
import com.tengan.mall.wallet.interfaces.rest.dto.TierBenefitResponse;
import com.tengan.mall.wallet.interfaces.rest.dto.TierBenefitsResponse;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/wallet/points")
public class PublicWalletController {

    private final GetTierBenefitsUseCase getTierBenefitsUseCase;

    public PublicWalletController(GetTierBenefitsUseCase getTierBenefitsUseCase) {
        this.getTierBenefitsUseCase = getTierBenefitsUseCase;
    }

    @GetMapping("/tier/benefits")
    public TierBenefitsResponse tierBenefits() {
        var items = getTierBenefitsUseCase.get().stream()
                .map(b -> new TierBenefitResponse(b.tier(), b.label(), b.cashbackRateLabel(), b.monthlyCapLabel(),
                        b.perks()))
                .toList();
        return new TierBenefitsResponse(items);
    }

    /** 靜態內容，不落地資料表——跟前端 mocks/points.ts 的 FAQ 文案是同一組（見 Phase 8 規劃）。 */
    @GetMapping("/faq")
    public FaqResponse faq() {
        return new FaqResponse(List.of(
                new PointFaqItemResponse("1", "點數什麼時候會入帳？",
                        "訂單確認收貨後，會先進入 7 天鑑賞期（待入帳狀態），期滿後系統會自動將點數轉為可用。"),
                new PointFaqItemResponse("2", "點數有使用期限嗎？",
                        "每筆點數自入帳日起 365 天內有效，逾期未使用會自動失效，請留意「即將到期」提醒。"),
                new PointFaqItemResponse("3", "PRO / PRO+ 會員的回饋比例是多少？",
                        "PRO 會員享有消費金額 2% 回饋（單月上限 500 點），PRO+ 會員享有 4% 回饋且無單月上限。"),
                new PointFaqItemResponse("4", "點數可以在結帳時折抵現金嗎？",
                        "可以，結帳頁可選擇使用可用點數折抵訂單金額，1 點折抵 NT$1。"),
                new PointFaqItemResponse("5", "訂單取消後，使用掉的點數會退回嗎？",
                        "會，訂單取消後系統會自動將該筆訂單使用的點數退回帳戶。")));
    }
}
