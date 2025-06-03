package com.team03.monew.collector;

import com.team03.monew.dto.newsArticle.request.NewsArticleRequestDto;
import com.team03.monew.service.NewsArticleService;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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

    private static final String RSS_URL = "https://www.hankyung.com/feed/all-news";

    private static final Map<String, String> RSS_SOURCES = Map.of(
        "https://www.hankyung.com/feed/all-news", "한국경제",
        "https://www.chosun.com/arc/outboundfeeds/rss/?outputType=xml", "조선일보",
        "https://www.yonhapnewstv.co.kr/browse/feed/", "연합뉴스TV"
    );

    public void collectAll() {
        RSS_SOURCES.forEach(this::collectFromUrl);
    }


    private void collectFromUrl(String rssUrl, String source) {
        try {
            Document doc = Jsoup.connect(RSS_URL).get();
            Elements items = doc.select("item");

            for (Element item : items) {
                String title = item.selectFirst("title").text();
                String link = item.selectFirst("link").text();
                String pubDate = item.selectFirst("pubDate").text();
                String summary = item.selectFirst("description") != null ? item.selectFirst("description").text() : "";

                System.out.println("▶ RSS 기사: " + title + " / " + link + " / " + pubDate);

                LocalDateTime date = parseRfc822(pubDate);

                NewsArticleRequestDto dto = new NewsArticleRequestDto(
                    title,
                    link,
                    summary,
                    date,
                    source
                );

                if (newsArticleService.containsKeyword(title, summary)) {
                    newsArticleService.saveIfNotExists(dto);
                }
            }
        } catch (IOException e) {
            System.err.println("RSS 수집 실패: " + RSS_URL);
            e.printStackTrace();
        }
    }

    private LocalDateTime parseRfc822(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
        ZonedDateTime zdt = ZonedDateTime.parse(dateStr, formatter);
        return zdt.withZoneSameInstant(ZoneId.of("Asia/Seoul")).toLocalDateTime();
    }
}
