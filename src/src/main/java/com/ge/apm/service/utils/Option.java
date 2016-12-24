package com.ge.apm.service.utils;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

import java.util.NoSuchElementException;
import java.util.Objects;


public abstract class Option<T> {
    public static <T> Option<T> of(T value) {
        return value == null ? new None<T>() : new Some<>(value);
    }

    public Option<T> none() {
        return new None<T>();
    }

    public Option<T> some(T value) {
        return new Some<>(value);
    }

    public boolean isDefined() {
        return !this.isEmpty();
    }

    public Option<T> orElse(T that) {
        return isEmpty() ? of(that) : this;
    }

    public Option<T> orElse(Option<T> that) {
        return isEmpty() ? that : this;
    }

    public T getOrElse(T that) {
        return isEmpty() ? that : this.get();
    }

    public <U> Option<U> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper, "mapper is null");
        return isEmpty() ? new None<U>() : new Some<U>(mapper.apply(get()));
    }

    public Option<T> filter(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate, "predicate is null");
        return isEmpty() || predicate.apply(get()) ? this : new None<T>();
    }

    public <U> Option<U> flatMap(Function<? super T, ? extends Option<? extends U>> mapper) {
        Objects.requireNonNull(mapper, "mapper is null");
        return isEmpty() ? new None<U>() : (Option<U>) mapper.apply(get());
    }

    public abstract boolean isEmpty();

    public abstract T get();
}

final class None<T> extends Option<T> {
    None() {
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public T get() {
        throw new NoSuchElementException("Non.get");
    }
}

final class Some<T> extends Option<T> {
    private T value;

    Some(T value) {
        this.value = value;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public T get() {
        return value;
    }
}