package syleelsw.anyonesolveit.interceptor;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class CustomHttpRequestWrapper extends HttpServletRequestWrapper {

    private final byte[] requestBody;

    public CustomHttpRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        requestBody = readBody(request);
    }

    private byte[] readBody(HttpServletRequest request) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream is = request.getInputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = is.read(buffer)) != -1) {
            baos.write(buffer, 0, bytesRead);
        }
        return baos.toByteArray();
    }

    @Override
    public ServletInputStream getInputStream() {
        ByteArrayInputStream bais = new ByteArrayInputStream(requestBody);
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {}

            @Override
            public int read() {
                return bais.read();
            }
        };
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
    }

    public String getRequestBody() {
        return new String(requestBody, StandardCharsets.UTF_8);
    }

    public String getParameter() {
        Map<String, String[]> parameterMap = super.getParameterMap();
        StringBuilder sb = new StringBuilder();
        for (String key : parameterMap.keySet()) {
            sb.append(key).append(":").append(parameterMap.get(key)[0]).append(" ");
        }
        return sb.toString();
    }
}