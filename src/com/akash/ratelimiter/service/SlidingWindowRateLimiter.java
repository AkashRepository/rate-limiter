package com.akash.ratelimiter.service;

import com.akash.ratelimiter.model.RateLimiterType;
import com.akash.ratelimiter.model.Request;

import java.util.Map;
import java.util.concurrent.*;

public class SlidingWindowRateLimiter {

    private int timeOffPeriod;
    private int limit;
    private RateLimiterType rateLimiterType;
    private ArrayBlockingQueue<Request>[] requestBlocks;
//    private ExecutorService[] threads;
    private Map<Request, Integer> invertedMap;

    public SlidingWindowRateLimiter(final int timeOffPeriod, int limit) {
        this.timeOffPeriod = timeOffPeriod;
        this.limit = limit;
        this.requestBlocks = new ArrayBlockingQueue[timeOffPeriod];
//        this.threads = new ExecutorService[timeOffPeriod];
        this.invertedMap = new ConcurrentHashMap<>();
        Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(checkRequest(), 0, 1000, TimeUnit.MILLISECONDS);
    }

    private Runnable checkRequest() {
        return () -> {
            int currentBlock = getCurrentBlock();
            for(Request r : requestBlocks[currentBlock]){
                if(System.currentTimeMillis() - r.getStartTime() >= timeOffPeriod){
                    requestBlocks[currentBlock].remove(r);
                    invertedMap.remove(r);
                }
            }
        };
    }

    public void addRequest(final Request request){
        int currentBlock = getCurrentBlock();
        if(requestBlocks[currentBlock].size() > limit){
            throw new RuntimeException("Limit exceeded");
        }
        requestBlocks[currentBlock].add(request);
        invertedMap.put(request, currentBlock);
    }

    public void evict(Request request){
        int index = invertedMap.get(request);
        requestBlocks[index].remove(request);
        invertedMap.remove(request);
    }

    private int getCurrentBlock() {
        return (int) System.currentTimeMillis() % timeOffPeriod;
    }
}
