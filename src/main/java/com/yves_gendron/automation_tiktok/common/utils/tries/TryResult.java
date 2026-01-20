package com.yves_gendron.automation_tiktok.common.utils.tries;

import com.yves_gendron.automation_tiktok.common.utils.exceptions.TryResultException;
import org.springframework.lang.Nullable;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public record TryResult<T>(
        boolean isSuccess,
        @Nullable
        T result,
        @Nullable
        Exception exception
) {

    public boolean isFailure() {
        return !isSuccess;
    }

    public Optional<T> getOptionalResult() {
        return Optional.ofNullable(result);
    }

    public Optional<Exception> getOptionalException() {
        return Optional.ofNullable(exception);
    }

    public T orElseThrow() throws TryResultException {
        if (isFailure()) {
            throw new TryResultException(exception);
        }
        return result;
    }

    public T orElseThrow(Supplier<? extends RuntimeException> supplier) throws TryResultException {
        if (isFailure()) {
            throw supplier.get();
        }
        return result;
    }

    public T orElse(T other) {
        return isSuccess ? result : other;
    }

    public TryResult<T> onFail(Consumer<Exception> consumer) {
        if (isFailure()) {
            consumer.accept(exception);
        }
        return this;
    }

    public TryResult<T> onSuccess(Consumer<T> consumer) {
        if (isSuccess) {
            consumer.accept(result);
        }
        return this;
    }

    public TryResult<T> peek(BiConsumer<T, Exception> consumer) {
        consumer.accept(result, exception);
        return this;
    }

    public TryResult<T> filter(TryFunction<T,Boolean> predicate) {
        if (isFailure()) {
            return this;
        }
        try {
            if (!predicate.apply(result)) {
                return TryResult.failure(new TryResultException("Predicate failed"));
            }
        } catch (Exception e) {
            return TryResult.failure(e);
        }
        return this;
    }




    public <R> TryResult<R> map(TryFunction<T, R> mapper) {
        if (isFailure()) {
            return TryResult.failure(exception);
        }
        try {
            return TryResult.success(mapper.apply(result));
        } catch (Exception e) {
            return TryResult.failure(e);
        }
    }


    public <R> TryResult<R> flatMap(Function<T, TryResult<R>> mapper) {
        if (isFailure()) {
            return TryResult.failure(exception);
        }
        return mapper.apply(result);
    }

    public static <T> TryResult<T> success(T result) {
        return new TryResult<>(true, result, null);
    }

    public static <T> TryResult<T> failure(Exception exception) {
        return new TryResult<>(false, null, exception);
    }
}
