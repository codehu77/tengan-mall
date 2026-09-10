package com.tengan.mall.devtools.scrape;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.microsoft.playwright.options.WaitUntilState;
import com.tengan.mall.devtools.scrape.dto.ScrapeResult;
import com.tengan.mall.devtools.scrape.dto.ScrapedSku;
import com.tengan.mall.devtools.scrape.dto.SpecHint;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

/**
 * MOMO 商品頁是 Next.js JS 動態渲染，價格/SKU 資料要等渲染完才會出現，且沒有伺服器端就能拿到的
 * JSON。實測發現渲染完成後頁面會注入一段 schema.org {@code ProductGroup} 的 JSON-LD
 * （{@code script[type="application/ld+json"]}），裡面已經有標題/圖庫/品牌/規格提示/每個顏色 SKU
 * 的名稱+圖片+價格——不需要真的去點每個顏色 swatch。「商品特色」區塊則是賣家自己上傳的一段 HTML，
 * 包在一個用 {@code srcdoc} 內嵌的 iframe 裡，而且要把它捲進視窗範圍才會被前端框架掛載——這段是
 * 唯一需要真的滾動頁面才能拿到的部分。
 */
@Service
public class MomoScraperService {

    private static final String FEATURE_IFRAME_SELECTOR = "#goods-feature-tabs-panel-features iframe";

    /** JSON-LD image 清單裡的商品照命名規則：_R/_R1~_R5 是固定 640x640 的正式商品照，
     * _O 原本以為是外包裝/標示貼紙才排除，但實測（15028716 這隻商品）發現 _O 其實也是正常的
     * 商品陳列圖（例如同色系全機型排列圖），只是解析度是 1000x1000，一樣是正方形，混進列表
     * 不會有比例跑掉的問題，所以跟 _R 系列一起留著。 */
    private static final Pattern MAIN_PHOTO_PATTERN = Pattern.compile("_(?:R\\d*|O)_m\\.[a-zA-Z0-9]+$");

    /** JSON-LD 裡每個顏色 SKU 給的圖，是 /spec/ 路徑下 320x320 的小圖（檔名 _L.jpg）——但實測
     * 頁面上顏色 swatch 按鈕自己用的圖，同一路徑換成 _R.jpg，剛好是 640x640，跟 SPU 主圖同一套
     * 尺寸規格（使用者自己在頁面右鍵複製圖片網址驗證過)。不是每個商品都保證有這個版本，所以只當
     * 候選網址試載一次，失敗就退回原本的 _L 小圖。 */
    private static final Pattern SMALL_SPEC_IMAGE_PATTERN = Pattern.compile("^(.*/spec/.*)_L\\.jpg(\\?.*)?$");

    private final ObjectMapper objectMapper;

    public MomoScraperService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public ScrapeResult scrape(String url) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            try {
                Page page = browser.newPage(new Browser.NewPageOptions().setViewportSize(1280, 1000));
                page.navigate(url, new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED)
                        .setTimeout(60000));

                JsonNode productGroup = readProductGroup(page);
                if (productGroup == null) {
                    throw new IllegalStateException(
                            "頁面裡找不到商品的結構化資料（JSON-LD ProductGroup）。可能不是 MOMO 商品頁，或 MOMO 改版了頁面結構。");
                }

                String name = textOrNull(productGroup, "name");
                List<String> spuImages = filterMainPhotos(arrayOfStrings(productGroup.get("image")));
                JsonNode brandNode = productGroup.get("brand");
                String brandHint = brandNode == null ? null : textOrNull(brandNode, "name");
                List<SpecHint> specHints = readSpecHints(productGroup.get("additionalProperty"));
                List<ScrapedSku> skus = readSkus(page, productGroup.get("hasVariant"));

                FeatureSection feature = readFeatureSection(page);

                return new ScrapeResult(url, name, brandHint, spuImages, specHints, skus, feature.images(),
                        feature.text());
            } finally {
                browser.close();
            }
        }
    }

    private JsonNode readProductGroup(Page page) {
        page.waitForSelector("script[type=\"application/ld+json\"]",
                new Page.WaitForSelectorOptions().setState(WaitForSelectorState.ATTACHED).setTimeout(15000));
        List<String> texts = page.locator("script[type=\"application/ld+json\"]").allTextContents();
        for (String text : texts) {
            JsonNode node = findProductGroup(text);
            if (node != null) {
                return node;
            }
        }
        return null;
    }

    private JsonNode findProductGroup(String jsonLdText) {
        try {
            JsonNode root = objectMapper.readTree(jsonLdText);
            JsonNode graph = root.has("@graph") ? root.get("@graph") : root;
            if (graph.isArray()) {
                for (JsonNode candidate : graph) {
                    if (isProductGroup(candidate)) {
                        return candidate;
                    }
                }
                return null;
            }
            return isProductGroup(graph) ? graph : null;
        } catch (Exception e) {
            // 頁面上其他跟商品無關的 JSON-LD 區塊（麵包屑、評論…），或格式不是合法 JSON，略過
            return null;
        }
    }

    private boolean isProductGroup(JsonNode node) {
        return node.has("@type") && "ProductGroup".equals(node.get("@type").asText());
    }

    private String textOrNull(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return value == null || value.isNull() ? null : value.asText();
    }

    private List<String> arrayOfStrings(JsonNode arrayNode) {
        List<String> result = new ArrayList<>();
        if (arrayNode == null) {
            return result;
        }
        if (arrayNode.isArray()) {
            arrayNode.forEach(n -> result.add(n.asText()));
        } else if (!arrayNode.isNull()) {
            result.add(arrayNode.asText());
        }
        return result;
    }

    /** 只留下檔名符合固定尺寸商品照命名規則的圖；如果一張都不符合（MOMO 改了命名規則、
     * 或這個商品根本沒有 _R 系列圖），就不濾，寧可尺寸不整齊也不要匯入時開天窗。 */
    private List<String> filterMainPhotos(List<String> urls) {
        List<String> filtered = urls.stream().filter(u -> MAIN_PHOTO_PATTERN.matcher(u).find()).toList();
        return filtered.isEmpty() ? urls : filtered;
    }

    private List<SpecHint> readSpecHints(JsonNode arrayNode) {
        List<SpecHint> hints = new ArrayList<>();
        if (arrayNode == null) {
            return hints;
        }
        Iterator<JsonNode> iterator = arrayNode.isArray() ? arrayNode.iterator() : List.of(arrayNode).iterator();
        iterator.forEachRemaining(node -> hints.add(new SpecHint(textOrNull(node, "name"), textOrNull(node, "value"))));
        return hints;
    }

    private List<ScrapedSku> readSkus(Page page, JsonNode hasVariant) {
        List<ScrapedSku> skus = new ArrayList<>();
        if (hasVariant == null || !hasVariant.isArray()) {
            return skus;
        }
        Map<String, String> imageUpgradeCache = new HashMap<>();
        for (JsonNode variant : hasVariant) {
            String name = textOrNull(variant, "name");
            String skuCode = textOrNull(variant, "sku");
            List<String> images = filterMainPhotos(arrayOfStrings(variant.get("image")));
            String mainImage = images.isEmpty() ? null
                    : upgradeSpecImage(page, imageUpgradeCache, images.get(0));
            JsonNode offers = variant.get("offers");
            BigDecimal price = null;
            if (offers != null) {
                String priceText = textOrNull(offers, "price");
                if (priceText != null) {
                    price = new BigDecimal(priceText);
                }
            }
            skus.add(new ScrapedSku(name, skuCode, mainImage, price));
        }
        return skus;
    }

    private String upgradeSpecImage(Page page, Map<String, String> cache, String url) {
        if (cache.containsKey(url)) {
            return cache.get(url);
        }
        Matcher matcher = SMALL_SPEC_IMAGE_PATTERN.matcher(url);
        if (!matcher.matches()) {
            cache.put(url, url);
            return url;
        }
        String candidate = matcher.group(1) + "_R.jpg" + (matcher.group(2) == null ? "" : matcher.group(2));
        Object loaded = page.evaluate("src => new Promise(resolve => {\n"
                + "  const img = new Image();\n"
                + "  img.onload = () => resolve(true);\n"
                + "  img.onerror = () => resolve(false);\n"
                + "  img.src = src;\n"
                + "})", candidate);
        String result = Boolean.TRUE.equals(loaded) ? candidate : url;
        cache.put(url, result);
        return result;
    }

    /** 商品特色 tab 的 iframe 用 IntersectionObserver 懶掛載，實測發現一次跳到定位的
     * scrollIntoView 沒用，要真的模擬使用者一格一格往下滑，掛載才會觸發。 */
    private FeatureSection readFeatureSection(Page page) {
        for (int i = 0; i < 40; i++) {
            page.mouse().wheel(0, 1500);
            page.waitForTimeout(300);
        }

        Locator iframe = page.locator(FEATURE_IFRAME_SELECTOR).first();
        try {
            iframe.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.ATTACHED).setTimeout(15000));
        } catch (PlaywrightException e) {
            return new FeatureSection(List.of(), null);
        }

        String srcdoc = iframe.getAttribute("srcdoc");
        if (srcdoc == null || srcdoc.isBlank()) {
            return new FeatureSection(List.of(), null);
        }

        Document doc = Jsoup.parse(srcdoc);
        List<String> images = doc.select("img[src]").eachAttr("src");
        String text = doc.body().text();
        return new FeatureSection(images, text.isBlank() ? null : text);
    }

    private record FeatureSection(List<String> images, String text) {
    }
}
