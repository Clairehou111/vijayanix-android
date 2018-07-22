package com.vijayanix.iot.model.http;

import java.util.List;

public class HttpResponse extends HttpMessage {
    private int mStatus;
    private String mMessage;

    public void setStatus(int status) {
        mStatus = status;
    }

    public int getStatus() {
        return mStatus;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public String getMessage() {
        return mMessage;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        final String blank = " ";
        sb.append(getHttpVersion())
            .append(blank)
            .append(getStatus())
            .append(blank)
            .append(getMessage())
            .append(HEADER_SEPARATOR);
        List<HttpHeader> headers = getHeaders();
        for (HttpHeader header : headers) {
            sb.append(header.getName())
                .append(HEADER_CONTENT_SEPARATOR)
                .append(header.getValue())
                .append(HEADER_SEPARATOR);
        }
        sb.append(HEADER_SEPARATOR);

        if (getContent() != null) {
            sb.append(getContent());
        }

        return sb.toString();
    }
}
