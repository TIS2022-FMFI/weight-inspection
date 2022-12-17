package com.example.utils;

import com.example.utils.exeptions.NoNetworkException;

@FunctionalInterface
public interface NetworkThrowingFunction {
    void apply() throws NoNetworkException;
}