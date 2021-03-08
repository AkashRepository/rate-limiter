package com.akash.ratelimiter.model;

import java.util.Objects;

public class Request {
    private String requestId;
    private long startTime;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Request request = (Request) o;
        return getStartTime() == request.getStartTime() && Objects.equals(getRequestId(), request.getRequestId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRequestId(), getStartTime());
    }
}
