package com.team03.monew.external.rss;

import com.team03.monew.dto.newsArticle.request.NewsArticleRequestDto;
import com.team03.monew.entity.Interest;
import com.team03.monew.service.InterestService;
import com.team03.monew.service.NewsArticleService;
import com.team03.monew.service.NotificationService;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RssCollector {

    private final NewsArticleService newsArticleService;
    private final InterestService interestService;
    private final NotificationService notificationService;

    private static final Map<String, String> RSS_SOURCES = Map.of(
        "https://www.hankyung.com/feed/all-news", "한국경제",
        "https://www.chosun.com/arc/outboundfeeds/rss/?outputType=xml", "조선일보",
        "http://www.yonhapnewstv.co.kr/category/news/headline/feed/", "연합뉴스TV"
    );

    public void collectAll() {
        RSS_SOURCES.forEach(this::collectFromUrl);
    }


    private void collectFromUrl(String rssUrl, String source) {
        try {
            Document doc = Jsoup.connect(rssUrl).get();
            Elements items = doc.select("item");
            Map<Interest, Integer> interestArticleCount = new HashMap<>();
            Map<Interest, List<String>> interestKeywordMap = interestService.getInterestKeywordMap();

            System.out.println("[" + source + "] RSS 수집 시작");
            for (Element item : items) {
                String title = item.selectFirst("title").text();
                String link = item.selectFirst("link").text();
                String pubDate = item.selectFirst("pubDate").text();
//                String summary = item.selectFirst("description") != null ? item.selectFirst("description").text() : "";
                Element descriptionEl = item.selectFirst("description");
                String rawSummary = descriptionEl != null ? descriptionEl.text() : "";
                String summary = cleanSummary(rawSummary);

                System.out.println("제목: " + title);
                System.out.println("요약: " + summary);
//                System.out.println("▶ RSS 기사: " + title + " / " + link + " / " + pubDate);
                LocalDateTime date = parseRfc822(pubDate);

                NewsArticleRequestDto dto = new NewsArticleRequestDto(
                    title,
                    link,
                    summary,
                    date,
                    source
                );

                if (newsArticleService.containsKeyword(title, summary)) {
                    System.out.println("저장 직전 summary: " + dto.summary());
                    newsArticleService.saveIfNotExists(dto);
                    for (Map.Entry<Interest, List<String>> entry : interestKeywordMap.entrySet()) {
                        Interest interest = entry.getKey();
                        List<String> keywords = entry.getValue();

                        // 관심사 키워드 중 하나라도 포함되면 카운트 증가
                        if (keywords.stream().anyMatch(k -> title.contains(k) || summary.contains(k))) {
                            interestArticleCount.merge(interest, 1, Integer::sum);
                        }
                    }

                    for (Map.Entry<Interest, Integer> entry : interestArticleCount.entrySet()) {
                        Interest interest = entry.getKey();
                        int articleCount = entry.getValue();

                        notificationService.notifyInterestNews(interest, articleCount);
                    }

                }
            }
        } catch (IOException e) {
            System.err.println("RSS 수집 실패: " + rssUrl);
            e.printStackTrace();
        }
    }

    // localdatetime으로 변환
    private LocalDateTime parseRfc822(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
        ZonedDateTime zdt = ZonedDateTime.parse(dateStr, formatter);
        return zdt.withZoneSameInstant(ZoneId.of("Asia/Seoul")).toLocalDateTime();
    }

    private String cleanSummary(String input) {
        if (input == null || input.isBlank()) return "";

        // HTML 태그 제거
        String cleaned = Jsoup.parse(input).text().trim();

        // HTML 엔티티 디코딩 (&quot;, &amp; 등)
        cleaned = org.apache.commons.text.StringEscapeUtils.unescapeHtml4(cleaned);

        // URL 디코딩 (%EC%8A%A4 → 유니코드 문자)
        try {
            cleaned = java.net.URLDecoder.decode(cleaned, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            // 디코딩 실패한 경우 원본 사용
        }

        // 불필요한 링크 제거
        cleaned = cleaned.replaceAll("https?://\\S+", "");

        // 광고 문구 패턴 제거
        String[] banPhrases = {"더보기", "자세히 보기", "관련 기사", "관련 뉴스", "사진=|영상=", "출처="};
        for (String phrase : banPhrases) {
            if (cleaned.contains(phrase)) {
                cleaned = cleaned.replaceAll(".*" + phrase + ".*", "");
            }
        }
        // PDF/문서 링크 제거
        if (cleaned.matches(".*\\.(pdf|xls|doc)[^\\s]*.*")) return "";

        // 요약에 언론사 이름만 반복 → 의미 없음
        if (cleaned.matches(".*(SBS|연합뉴스|조선일보|채널A|MBC|KBS).*보기.*")) return "";

        // 특수문자, 공백만 있는 경우 제거
        if (cleaned.replaceAll("[^a-zA-Z가-힣0-9]", "").isBlank() || cleaned.length() < 10) {
            return "";
        }

        return cleaned;
    }
}
