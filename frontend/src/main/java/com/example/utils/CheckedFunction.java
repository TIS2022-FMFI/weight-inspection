package com.example.utils;

@FunctionalInterface
public interface CheckedFunction {
    void apply() throws NoNetworkException;
}