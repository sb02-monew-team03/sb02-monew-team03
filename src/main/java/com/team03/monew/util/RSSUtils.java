package com.team03.monew.util;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

@Component
public class RSSUtils {
    // localdatetime으로 변환
    public LocalDateTime parseRfc822(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
        ZonedDateTime zdt = ZonedDateTime.parse(dateStr, formatter);
        return zdt.withZoneSameInstant(ZoneId.of("Asia/Seoul")).toLocalDateTime();
    }

    // 여러 형태에 맞추어서 요약 추출
    public String extractSummary(Element item, String fallbackTitle) {
        String rawSummary = "";

        // content:encoded 우선
        Element encoded = item.selectFirst("content\\:encoded");
        if (encoded != null && !encoded.text().isBlank()) {
            rawSummary = Jsoup.parse(encoded.text()).text();
        }

        // description 백업
        if (rawSummary.isBlank()) {
            Element descriptionEl = item.selectFirst("description");
            if (descriptionEl != null && !descriptionEl.text().isBlank()) {
                rawSummary = Jsoup.parse(descriptionEl.text()).text();
            }
        }

        // fallback
        if (rawSummary.isBlank() || rawSummary.length() < 10 || containsBrokenChars(rawSummary)) {
            rawSummary = fallbackTitle;
        }

        return cleanSummary(rawSummary);
    }

    public boolean containsBrokenChars(String text) {
        return text.contains("�");
    }

    // 필터링
    public String cleanSummary(String input) {
        if (input == null || input.isBlank()) return "";

        String cleaned = Jsoup.parse(input).text().trim();
        cleaned = StringEscapeUtils.unescapeHtml4(cleaned);
        try {
            cleaned = URLDecoder.decode(cleaned, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException ignored) {}

        cleaned = cleaned.replaceAll("https?://\\S+", "");
        String[] banPhrases = {"더보기", "자세히 보기", "관련 기사", "사진=", "출처=", "영상="};
        for (String phrase : banPhrases) {
            cleaned = cleaned.replaceAll(".*" + phrase + ".*", "");
        }

        if (cleaned.matches(".*\\.(pdf|xls|doc)[^\\s]*.*")) return "";
        if (cleaned.replaceAll("[^a-zA-Z가-힣0-9]", "").isBlank() || cleaned.length() < 10) return "";

        return cleaned;
    }
}
