package org.oauth.common.filter;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

/**
 * packageName    : org.oauth.common.filter
 * fileName       : LoggingFilter
 * author         : sinuk
 * date           : 2025-09-02
 * description    : 로깅 Filter
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025-09-02         sinuk       최초 생성
 **/

@Slf4j
@Order(1)
@Component
@RequiredArgsConstructor
public class LoggingFilter extends OncePerRequestFilter {

    private static final Set<String> SKIP_PATHS = Set.of("/health", "/actuator/health");
    private static final int MAX_BODY = 2000;

    private final Gson gson;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String p = request.getRequestURI();
        if (SKIP_PATHS.contains(p)) return true;
        return p.startsWith("/actuator");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        // 요청 바디를 진입 시점에 확보
        CachedBodyHttpServletRequest request = new CachedBodyHttpServletRequest(req);
        // 응답 바디 캐시
        ContentCachingResponseWrapper response = new ContentCachingResponseWrapper(res);

        long start = System.currentTimeMillis();

        // ===== REQ LOG =====
        String reqCt = safe(request.getContentType());
        String reqHeaders = formatRequestHeaders(request);
        String reqBody = resolveRequestBody(request, reqCt);

        log.info("""
                        
                        ===== REQ LOG =====
                        {} {}{}
                        CT: {}
                        HEADERS:
                        {}
                        BODY:
                        {}
                        ===== REQ END =====
                        """,
                request.getMethod(),
                request.getRequestURI(),
                optionalPrefixed("?", safe(request.getQueryString())),
                reqCt,
                reqHeaders,
                reqBody
        );

        try {
            chain.doFilter(request, response);
        } finally {
            long took = System.currentTimeMillis() - start;

            // ===== RES LOG =====
            String resCt = safe(response.getContentType());
            String resHeaders = formatResponseHeaders(response);
            String resBody = resolveResponseBody(response, resCt);

            log.info("""
                            
                            ===== RES LOG =====
                            {} {}  {}ms
                            status: {}  bytes: {}
                            CT: {}
                            HEADERS:
                            {}
                            BODY:
                            {}
                            ===== RES END =====
                            """,
                    request.getMethod(),
                    request.getRequestURI(),
                    took,
                    response.getStatus(),
                    response.getContentSize(),
                    resCt,
                    resHeaders,
                    resBody
            );

            // 꼭 마지막
            response.copyBodyToResponse();
        }
    }

    /* ---------------- body 처리 ---------------- */

    private String resolveRequestBody(CachedBodyHttpServletRequest request, String contentType) {
        if (isBinary(contentType)) return "(skipped: binary content)";
        String raw = new String(request.getCachedBody(), StandardCharsets.UTF_8);

        // body가 비어있으면 파라미터를 쿼리스트링 형태로
        String body = raw.isBlank()
                ? buildQueryString(request.getParameterMap())
                : raw;

        return prettyMaskClip(body, contentType);
    }

    private String resolveResponseBody(ContentCachingResponseWrapper response, String contentType) {
        if (isBinary(contentType)) return "(skipped: binary content)";
        String raw = new String(response.getContentAsByteArray(), StandardCharsets.UTF_8);
        return prettyMaskClip(raw, contentType);
    }

    private boolean isBinary(String contentType) {
        if (contentType == null) return false;
        String ct = contentType.toLowerCase();
        return ct.startsWith("multipart/")
                || ct.startsWith("application/octet-stream")
                || ct.startsWith("image/")
                || ct.startsWith("video/")
                || ct.startsWith("audio/");
    }

    /**
     * 마스킹 → 클립 → (JSON이면) pretty 순서로 처리
     * - pretty 이전에 마스킹해서 민감정보가 pretty에 노출되지 않게 함
     */
    private String prettyMaskClip(String body, String contentType) {
        String masked = mask(body);
        String clipped = clip(masked);

        if (looksLikeJson(contentType, clipped)) {
            try {
                JsonElement el = JsonParser.parseString(clipped);
                return gson.toJson(el);
            } catch (Exception ignore) {
                // 파싱 실패 시 원문 반환
            }
        }
        return clipped;
    }

    private boolean looksLikeJson(String contentType, String s) {
        if (contentType != null && contentType.toLowerCase().contains("application/json")) return true;
        String t = s == null ? "" : s.trim();
        return (t.startsWith("{") && t.endsWith("}")) || (t.startsWith("[") && t.endsWith("]"));
    }

    /* ---------------- 유틸 ---------------- */

    private String clip(String s) {
        if (s == null) return "";
        if (s.length() > MAX_BODY) return s.substring(0, MAX_BODY) + "...(truncated)";
        return s;
    }

    private String mask(String s) {
        if (s == null) return "";
        // 아주 단순한 마스킹 예시 (필요 시 키 추가)
        return s
                .replaceAll("(?i)(\"?password\"?\\s*[:=]\\s*\")(.*?)(\")", "$1***$3")
                .replaceAll("(?i)(\"?passwd\"?\\s*[:=]\\s*\")(.*?)(\")", "$1***$3")
                .replaceAll("(?i)(\"?secret\"?\\s*[:=]\\s*\")(.*?)(\")", "$1***$3")
                .replaceAll("(?i)(client_secret=)([^&\\s]+)", "$1***")
                .replaceAll("(?i)(authorization:\\s*bearer\\s+)([A-Za-z0-9\\-_.]+)", "$1***");
    }

    private String formatRequestHeaders(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        Enumeration<String> names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            String value = request.getHeader(name);
            // 민감 헤더 마스킹
            if (name.equalsIgnoreCase("authorization") || name.equalsIgnoreCase("cookie")) {
                value = "***";
            }
            sb.append(name).append(": ").append(value).append("\n");
        }
        return sb.toString().trim();
    }

    private String formatResponseHeaders(org.springframework.web.util.ContentCachingResponseWrapper response) {
        // headerName(소문자 정규화) -> 원본이름, 값 토큰 Set
        java.util.Map<String, HeaderBag> bag = new java.util.LinkedHashMap<>();

        for (String rawName : response.getHeaderNames()) {
            String normName = rawName.toLowerCase();
            bag.putIfAbsent(normName, new HeaderBag(rawName));
            for (String v : response.getHeaders(rawName)) {
                for (String token : v.split(",")) {
                    String t = token.trim();
                    if (!t.isEmpty()) bag.get(normName).values.add(t);
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        for (HeaderBag h : bag.values()) {
            String name = h.originalName;
            if (name.equalsIgnoreCase("set-cookie")) {
                sb.append(name).append(": ***\n");
            } else {
                sb.append(name).append(": ")
                        .append(String.join(", ", h.values))
                        .append("\n");
            }
        }
        return sb.toString().trim();
    }

    private static class HeaderBag {
        final String originalName;
        final java.util.LinkedHashSet<String> values = new java.util.LinkedHashSet<>();
        HeaderBag(String originalName) { this.originalName = originalName; }
    }

    private String buildQueryString(Map<String, String[]> params) {
        if (params == null || params.isEmpty()) return "";
        StringJoiner sj = new StringJoiner("&");
        params.forEach((k, arr) -> {
            if (arr == null || arr.length == 0) {
                sj.add(encode(k) + "=");
            } else {
                for (String v : arr) {
                    sj.add(encode(k) + "=" + encode(v));
                }
            }
        });
        return sj.toString();
    }

    private String encode(String s) {
        try {
            return URLEncoder.encode(s, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return s;
        }
    }

    private String safe(String s) { return s == null ? "" : s; }

    private String optionalPrefixed(String prefix, String s) {
        if (s == null || s.isBlank()) return "";
        return prefix + s;
    }
}