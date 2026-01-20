package com.yves_gendron.automation_tiktok.common.utils.tries;


import com.yves_gendron.automation_tiktok.common.utils.exceptions.ReTryResultException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class TryBuilder<T> {
    private static final BiConsumer<Integer, Exception> DEFAULT_ON_RETRY = (i, e) -> {
    };
    private static final Runnable DEFAULT_ON_NON_PARAMETRIC_RETRY = () -> {
    };
    private static final Consumer<Exception> DEFAULT_ON_EXCEPTION = e -> {
    };
    private static final Consumer<Integer> DEFAULT_ON_INDEX_RETRY_COUNT = i -> {
    };

    private final TrySupplier<T> trySupplier;
    private int retryCount = 1;
    private BiConsumer<Integer, Exception> onRetry = DEFAULT_ON_RETRY;
    private Runnable onNonParametricRetry = DEFAULT_ON_NON_PARAMETRIC_RETRY;
    private Consumer<Exception> onException = DEFAULT_ON_EXCEPTION;
    private Consumer<Integer> onIndexRetryCount = DEFAULT_ON_INDEX_RETRY_COUNT;
    private List<Class<? extends Exception>> skipExceptions = new ArrayList<>();
    private List<Predicate<Exception>> skipExceptionPredicates = new ArrayList<>();

    TryBuilder(TrySupplier<T> trySupplier) {
        this.trySupplier = trySupplier;
    }

    public TryBuilder<T> setRetryCount(int retryCount) {
        if (retryCount < 1) {
            throw new IllegalArgumentException("Retry count must be at least 1");
        }
        this.retryCount = retryCount;
        return this;
    }

    public TryBuilder<T> setOnRetry(BiConsumer<Integer, Exception> onRetry) {
        this.onRetry = Objects.requireNonNullElse(onRetry, DEFAULT_ON_RETRY);
        return this;
    }

    public TryBuilder<T> setOnNonParametricRetry(Runnable onNonParametricRetry) {
        this.onNonParametricRetry = Objects.requireNonNullElse(onNonParametricRetry, DEFAULT_ON_NON_PARAMETRIC_RETRY);
        return this;
    }

    public TryBuilder<T> setOnException(Consumer<Exception> onException) {
        this.onException = Objects.requireNonNullElse(onException, DEFAULT_ON_EXCEPTION);
        return this;
    }

    public TryBuilder<T> setOnIndexRetryCount(Consumer<Integer> onIndexRetryCount) {
        this.onIndexRetryCount = Objects.requireNonNullElse(onIndexRetryCount, DEFAULT_ON_INDEX_RETRY_COUNT);
        return this;
    }

    public TryBuilder<T> setSkipException(Class<? extends Exception> exception) {
        if (exception == null) {
            return this;

        }

        try {
            skipExceptions.add(exception);
        } catch (UnsupportedOperationException e) {
            skipExceptions = new ArrayList<>(skipExceptions);
            skipExceptions.add(exception);
        }


        return this;
    }

    public TryBuilder<T> setSkipExceptions(List<Class<Exception>> exceptions) {
        if (exceptions != null) {
            skipExceptions.addAll(exceptions);
        }
        return this;
    }

    public TryBuilder<T> setSkipExceptionPredicate(Predicate<Exception> predicate) {
        if (predicate == null) {
            return this;
        }

        try {
            skipExceptionPredicates.add(predicate);
        } catch (UnsupportedOperationException e) {
            skipExceptionPredicates = new ArrayList<>(skipExceptionPredicates);
            skipExceptionPredicates.add(predicate);
        }

        return this;
    }

    public TryBuilder<T> setSkipExceptionPredicates(List<Predicate<Exception>> predicates) {
        if (predicates != null) {
            skipExceptionPredicates.addAll(predicates);
        }
        return this;
    }

    public TryResult<T> build() {
        if (retryCount < 1) {
            throw new ReTryResultException("Retries must be greater than 0");
        }
        TryResult<T> result = null;
        for (int i = 0; i < retryCount; i++) {
            result = TryUtils.tryGet(trySupplier);
            if (result.isSuccess()) {
                return result;
            }
            if (isSkipException(result.exception())) {
                return result;
            }
            onRetry.accept(i, result.exception());
            onIndexRetryCount.accept(i);
            onException.accept(result.exception());
            onNonParametricRetry.run();
        }
        return result;
    }

    private boolean isSkipException(Exception e) {
        if (e == null) {
            return false;
        }
        if (skipExceptions.stream().anyMatch(it -> it.isInstance(e))) {
            return true;
        }
        if (skipExceptionPredicates.isEmpty()){
            return false;
        }
        return skipExceptionPredicates.stream().anyMatch(it -> it.test(e));

    }


}
