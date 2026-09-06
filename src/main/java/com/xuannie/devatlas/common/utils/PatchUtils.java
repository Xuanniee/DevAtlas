package com.xuannie.devatlas.common.utils;

import java.util.function.Consumer;

public final class PatchUtils {
    private PatchUtils() {}

    // Set the value of the object if value is not NULL
    public static <T> void ifPresent(T value, Consumer<T> setter) {
        if (value != null) {
            setter.accept(value);
        }
    }
}
