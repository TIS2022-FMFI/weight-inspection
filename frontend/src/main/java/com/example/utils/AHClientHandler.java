package com.example.utils;

import com.example.controller.TableController;
import com.example.model.Page;
import com.example.utils.exeptions.IncorrectCodeExeption;
import com.google.gson.Gson;

import javafx.application.Platform;
import javafx.collections.ObservableList;

import java.util.concurrent.CompletableFuture;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Response;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

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

    public <T> void getPage(String url, int page, int pageSize, ObservableList<T> returnList, Class<T> type,
            TableController controller) {
        CompletableFuture<Response> whenResponse = AHClient
                .prepareGet(baseUrl + url)
                .addQueryParam("page", String.valueOf(page))
                .addQueryParam("page_size", String.valueOf(pageSize))
                .execute()
                .toCompletableFuture()
                .exceptionally(t -> {
                    t.printStackTrace();
                    return null;
                })
                .thenApply(
                        response -> {
                            try {
                                Type pageType = TypeToken.getParameterized(Page.class, type).getType();
                                Page<T> newPage = new Gson().fromJson(response.getResponseBody(),
                                        pageType);
                                Platform.runLater(
                                        () -> {
                                            returnList.clear();
                                            returnList.addAll(newPage.getItems());
                                            controller.setPaging(newPage.getTotalPages(), page);
                                            controller.updateButtons();
                                        });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return null;
                        });
    }

    public <T> void postRequest(String url, T object, TableController controller) {
        String jsonObject = gson.toJson(object);
        System.out.println(url);
        System.out.println(jsonObject);
        CompletableFuture<Response> whenResponse = AHClient
                .preparePost(baseUrl + url)
                .setHeader("Content-Type", "application/json")
                .setBody(jsonObject)
                .execute()
                .toCompletableFuture()
                .exceptionally(t -> {
                    System.out.println(new RuntimeException("Can't connect to the server"));
                    return null;
                })
                .thenApply(response -> {
                    if (response.getStatusCode() < 200 || response.getStatusCode() > 299) {
                        System.out.println(
                                new IncorrectCodeExeption("Code was " + String.valueOf(response.getStatusCode())));
                    }
                    Platform.runLater(
                            () -> {
                                controller.updateTable();
                            });
                    return null;
                });
    }

    public <T> void putRequest(String url, T object, TableController controller) {
        String jsonObject = gson.toJson(object);
        System.out.println(jsonObject);
        CompletableFuture<Response> whenResponse = AHClient
                .preparePut(baseUrl + url)
                .setHeader("Content-Type", "application/json")
                .setBody(jsonObject)
                .execute()
                .toCompletableFuture()
                .exceptionally(t -> {
                    System.out.println(new RuntimeException("Can't connect to the server"));
                    return null;
                })
                .thenApply(response -> {
                    if (response.getStatusCode() < 200 || response.getStatusCode() > 299) {
                        System.out.println(
                                new IncorrectCodeExeption("Code was " + String.valueOf(response.getStatusCode())));
                    }
                    Platform.runLater(
                            () -> {
                                controller.updateTable();
                            });
                    return null;
                });
    }

    public <T> void deleteRequest(String url, TableController controller) {
        CompletableFuture<Response> whenResponse = AHClient
                .prepareDelete(baseUrl + url)
                .execute()
                .toCompletableFuture()
                .exceptionally(t -> {
                    System.out.println(new RuntimeException("Can't connect to the server"));
                    return null;
                })
                .thenApply(response -> {
                    if (response.getStatusCode() < 200 || response.getStatusCode() > 299) {
                        System.out.println(
                                new IncorrectCodeExeption("Code was " + String.valueOf(response.getStatusCode())));
                    }
                    Platform.runLater(
                            () -> {
                                controller.updateTable();
                            });
                    return null;
                });
    }
}