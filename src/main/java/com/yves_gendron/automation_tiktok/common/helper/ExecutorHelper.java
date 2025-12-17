package com.yves_gendron.automation_tiktok.common.helper;

import com.yves_gendron.automation_tiktok.config.AppProps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExecutorHelper {

    private final AppProps appProps;

    public ExecutorService getExecutorWithAvailableThreads() {
        int processingThreads = appProps.getProcessingThreads();

        if (processingThreads == 0) {
            processingThreads = Runtime.getRuntime().availableProcessors();
        }

        log.info("ExecutorHelper: using {} threads", processingThreads);
        return Executors.newFixedThreadPool(processingThreads);
    }

    public void waitForFutures(List<Future<?>> futures) {
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                log.warn("ExecutorHelper: exception while waiting for future task: {}", e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }

    public void waitToShutdown(ExecutorService executorService) {
        executorService.shutdown();
        try {
            boolean awaitTermination = executorService.awaitTermination(10, TimeUnit.MINUTES);
            if (!awaitTermination) {
                log.warn("Forcing shutdown...");
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            log.warn("ExecutorHelper: couldn't shut down executor {}", e.getMessage());
        }
    }
}
