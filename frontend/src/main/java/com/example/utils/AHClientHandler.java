package com.example.utils;

import com.example.model.Page;
import com.example.utils.exeptions.IncorrectCodeExeption;
import com.google.gson.Gson;

import javafx.collections.ObservableList;

import java.util.concurrent.CompletableFuture;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Response;
import com.example.model.CommentModel;
import com.example.repository.CommentRepository;
import com.google.gson.reflect.TypeToken;

import javafx.collections.FXCollections;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import org.asynchttpclient.ListenableFuture;

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

    public <T> void getPage(String url, int page, int pageSize, ObservableList<T> returnList) {
        CompletableFuture<Response> whenResponse = AHClient
                .prepareGet(baseUrl + url)
                .addQueryParam("page_size", String.valueOf(pageSize))
                .execute()
                .toCompletableFuture()
                .exceptionally(t -> {
                    t.printStackTrace();
                    return null;
                })
                .thenApply(
                        response -> {
                            System.out.println(returnList);
                            System.out.println("Parsing");
                            Type listType = new TypeToken<ArrayList<T>>() {
                            }.getType();
                            List<T> newItems = new Gson().fromJson(response.getResponseBody(),
                                    listType);
                            System.out.println(newItems);
                            returnList.clear();
                            returnList.addAll(newItems);
                            return null;
                        });
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