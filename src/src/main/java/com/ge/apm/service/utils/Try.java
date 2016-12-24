package com.ge.apm.service.utils;


import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Objects;


public abstract class Try<T> {

    public static <T> Try<T> success(T value) {
        return new Success<>(value);
    }

    public static <T> Try<T> failure(Throwable exception) {
        return new Failure<>(exception);
    }

    public Try<Throwable> failed() {
        if (isFailure()) {
            return new Success<>(getCause());
        } else {
            return new Failure<>(new NoSuchElementException("Success.failed()"));
        }
    }

    public abstract boolean isSuccess();

    public abstract boolean isFailure();

    public abstract Throwable getCause();


}

final class Success<T> extends Try<T> {
    private final T value;

    Success(T value) {
        this.value = value;
    }

    @Override
    public boolean isSuccess() {
        return true;
    }

    @Override
    public boolean isFailure() {
        return false;
    }

    @Override
    public Throwable getCause() {
        throw new UnsupportedOperationException("getCause on Success");
    }
}

final class Failure<T> extends Try<T> {
    private final NonFatalException cause;

    Failure(Throwable exception) {
        Objects.requireNonNull(exception, "exception is null");
        cause = NonFatalException.of(exception);
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    @Override
    public boolean isFailure() {
        return true;
    }

    @Override
    public Throwable getCause() {
        return cause.getCause();
    }
}

final class FatalException extends RuntimeException {
    FatalException(Throwable exception) {
        super(exception);
    }

    @Override
    public boolean equals(Object o) {
        return (o == this) || (o instanceof FatalException
                && Arrays.deepEquals(getCause().getStackTrace(), ((FatalException) o).getCause().getStackTrace()));
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getCause());
    }

    @Override
    public String toString() {
        return "Fatal(" + getCause() + ")";
    }
}

final class NonFatalException extends RuntimeException {
    NonFatalException(Throwable exception) {
        super(exception);
    }

    static NonFatalException of(Throwable exception) {
        Objects.requireNonNull(exception, "exception is null");
        if (exception instanceof NonFatalException) {
            return (NonFatalException) exception;
        } else if (exception instanceof FatalException) {
            throw (FatalException) exception;
        } else {
            final boolean isFatal = exception instanceof InterruptedException
                    || exception instanceof LinkageError
                    || exception instanceof ThreadDeath
                    || exception instanceof VirtualMachineError;
            if (isFatal) {
                throw new FatalException(exception);
            } else {
                return new NonFatalException(exception);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        return (o == this) || (o instanceof NonFatalException
                && Arrays.deepEquals(getCause().getStackTrace(), ((NonFatalException) o).getCause().getStackTrace()));
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getCause());
    }

    @Override
    public String toString() {
        return "NonFatal(" + getCause() + ")";
    }
}