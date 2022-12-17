package com.example.utils;

import com.example.utils.exeptions.IncorrectCodeExeption;
import com.google.gson.Gson;

import java.util.concurrent.CompletableFuture;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Response;

import static org.asynchttpclient.Dsl.*;

public class AHClientHandler {

    private static AHClientHandler AHClientHandler;
    private AsyncHttpClient AHClient;
    private Gson gson;
    private String baseUrl;

    private AHClientHandler(String baseUrl) {
        this.baseUrl = baseUrl;
        this.AHClient = asyncHttpClient();
        this.gson = new Gson();
    }

    public static AHClientHandler getAHClientHandler() {
        if (AHClientHandler == null) {
            AHClientHandler = new AHClientHandler("http://localhost:8080/api");
        }
        return AHClientHandler;
    }

    public AsyncHttpClient getAHClient() {
        return AHClient;
    }

    public <T> void postRequest(String url, T object) {
        String jsonObject = gson.toJson(object);
        CompletableFuture<Response> whenResponse = AHClient
                .preparePost(baseUrl + url)
                .setBody(jsonObject)
                .execute()
                .toCompletableFuture()
                .exceptionally(t -> {
                    System.out.println(new RuntimeException("Can't connect to the server"));
                    return null;
                })
                .thenApply(response -> {
                    if (response.getStatusCode() != 200) {
                        System.out.println(
                                new IncorrectCodeExeption("Code was " + String.valueOf(response.getStatusCode())));
                    }
                    return null;
                });
    }

    public <T> void putRequest(String url, T object) {
        String jsonObject = gson.toJson(object);
        CompletableFuture<Response> whenResponse = AHClient
                .preparePut(baseUrl + url)
                .setBody(jsonObject)
                .execute()
                .toCompletableFuture()
                .exceptionally(t -> {
                    System.out.println(new RuntimeException("Can't connect to the server"));
                    return null;
                })
                .thenApply(response -> {
                    if (response.getStatusCode() != 200) {
                        System.out.println(
                                new IncorrectCodeExeption("Code was " + String.valueOf(response.getStatusCode())));
                    }
                    return null;
                });
    }

    public <T> void deleteRequest(String url) {
        CompletableFuture<Response> whenResponse = AHClient
                .prepareDelete(baseUrl + url)
                .execute()
                .toCompletableFuture()
                .exceptionally(t -> {
                    System.out.println(new RuntimeException("Can't connect to the server"));
                    return null;
                })
                .thenApply(response -> {
                    if (response.getStatusCode() != 200) {
                        System.out.println(
                                new IncorrectCodeExeption("Code was " + String.valueOf(response.getStatusCode())));
                    }
                    return null;
                });
    }
}