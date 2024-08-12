package syleelsw.anyonesolveit.interceptor;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

@Component
public class LoggingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        CustomHttpRequestWrapper requestWrapper = new CustomHttpRequestWrapper((HttpServletRequest) request);

        CustomHttpResponseWrapper responseWrapper = new CustomHttpResponseWrapper((HttpServletResponse) response);
        chain.doFilter(requestWrapper, responseWrapper);


        int statusCode = responseWrapper.getStatus();

        // 400번대와 500번대 상태 코드에 대해서만 로그 작성
        if (statusCode >= 400) {
            // 로그 양식에 맞게 JSON 형태로 로그 생성
            String logMessage = String.format(
                    "{ \"timestamp\": \"%s\", \"request\": \"%s %s\" , \"status_code\": %d, \"error_message\": \"%s\" , \"request_body\": \"%s\" , \"request_params\": \"%s\"}",
                    Instant.now().toString(),
                    requestWrapper.getMethod(),
                    requestWrapper.getRequestURI(),
                    statusCode,
                    responseWrapper.getResponseBody(),
                    requestWrapper.getRequestBody(),
                    requestWrapper.getParameter()
            );

            logger.error(logMessage);
        }

        // 응답 데이터를 원래의 HttpServletResponse에 복사
        byte[] responseData = responseWrapper.getResponseBody().getBytes(StandardCharsets.UTF_8);
        response.getOutputStream().write(responseData);
        response.getOutputStream().flush();

    }

    @Override
    public void destroy() {}
}