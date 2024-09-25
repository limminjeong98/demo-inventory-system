package com.demo;

import org.jetbrains.annotations.NotNull;

public interface Calculator {
    @NotNull
    Integer add(@NotNull Integer a, @NotNull Integer b);
}