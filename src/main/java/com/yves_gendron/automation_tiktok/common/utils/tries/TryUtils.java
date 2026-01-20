package com.yves_gendron.automation_tiktok.common.utils.tries;


import com.yves_gendron.automation_tiktok.common.utils.exceptions.ReTryResultException;
import org.springframework.lang.NonNull;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class TryUtils {
    private static final BiConsumer<Integer, Exception> DEFAULT_ON_RETRY = (i, e) -> {};
    private TryUtils() {
    }

    public static <T> TryResult<T> tryGet(@NonNull TrySupplier<T> supplier) {
        try {
            return TryResult.success(supplier.get());
        } catch (Exception e) {
            return TryResult.failure(e);
        }
    }

    public static <T> TryResult<T> tryGet(@NonNull TrySupplier<T> supplier, int retries) {
        return tryGet(supplier, retries, DEFAULT_ON_RETRY);
    }

    public static <T> TryResult<T> tryGet(@NonNull TrySupplier<T> supplier, int retries, @NonNull BiConsumer<Integer, Exception> onRetry) {
        if (retries < 1) {
            throw new ReTryResultException("Retries must be greater than 0");
        }
        TryResult<T> result = null;
        for (int i = 0; i < retries; i++) {
            result = tryGet(supplier);
            if (result.isSuccess()) {
                return result;
            }
            onRetry.accept(i, result.exception());
        }
        return result;
    }

    public static <T> TryResult<T> tryGet(@NonNull TrySupplier<T> supplier, int retries, @NonNull Runnable onRetry) {
        return tryGet(supplier, retries, (i, e) -> onRetry.run());
    }

    public static <T> TryResult<T> tryGet(@NonNull TrySupplier<T> supplier, int retries, @NonNull Consumer<Exception> onRetry) {
        return tryGet(supplier, retries, (i, e) -> onRetry.accept(e));
    }

    public static TryResult<Void> tryRun(@NonNull TryRunnable runnable) {
        try {
            runnable.run();
            return TryResult.success(null);
        } catch (Exception e) {
            return TryResult.failure(e);
        }
    }

    public static TryResult<Void> tryRun(@NonNull TryRunnable runnable, int retries) {
        return tryRun(runnable, retries, DEFAULT_ON_RETRY);
    }

    public static TryResult<Void> tryRun(@NonNull TryRunnable runnable, int retries, @NonNull BiConsumer<Integer, Exception> onRetry) {
        if (retries < 1) {
            throw new ReTryResultException("Retries must be greater than 0");
        }
        TryResult<Void> result = null;
        for (int i = 0; i < retries; i++) {
            result = tryRun(runnable);
            if (result.isSuccess()) {
                return result;
            }
            onRetry.accept(i, result.exception());
        }
        return result;
    }

    public static TryResult<Void> tryRun(@NonNull TryRunnable runnable, int retries, @NonNull Runnable onRetry) {
        return tryRun(runnable, retries, (i, e) -> onRetry.run());
    }

    public static TryResult<Void> tryRun(@NonNull TryRunnable runnable, int retries, @NonNull Consumer<Exception> onRetry) {
        return tryRun(runnable, retries, (i, e) -> onRetry.accept(e));
    }

    public static TryBuilder<Void> tryRunBuilder(@NonNull TryRunnable runnable) {
        return new TryBuilder<>(
                () -> {
                    runnable.run();
                    return null;
                }
        );
    }

    public static <T> TryBuilder<T> tryGetBuilder(@NonNull TrySupplier<T> supplier) {
        return new TryBuilder<>(supplier);
    }

}
