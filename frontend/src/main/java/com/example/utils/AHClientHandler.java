package com.example.utils;

import com.example.utils.exeptions.IncorrectCodeExeption;
import com.example.utils.exeptions.NoNetworkException;
import com.google.gson.Gson;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.ListenableFuture;
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
            AHClientHandler = new AHClientHandler("localhost:8080/api");
        }
        return AHClientHandler;
    }

    public AsyncHttpClient getAHClient() {
        return AHClient;
    }

    public <T> void postRequest(String url, T object) {
        String jsonObject = gson.toJson(object);
        ListenableFuture<Response> whenResponse = AHClient
                .preparePost(baseUrl + url)
                .setBody(jsonObject)
                .execute();
        NetworkThrowingFunction callback = () -> {
            try {
                Response response = whenResponse.get();
                if (response.getStatusCode() != 200) {
                    throw new NoNetworkException("Can't connect to the server",
                            new IncorrectCodeExeption("Code was" + String.valueOf(response.getStatusCode())));
                }
            } catch (InterruptedException | ExecutionException e) {
                throw new NoNetworkException("Can't connect to the server", e);
            }
        };
        whenResponse.addListener((Runnable) callback, Executors.newCachedThreadPool());
    }

    public <T> void putRequest(String url, T object) {
        String jsonObject = gson.toJson(object);
        ListenableFuture<Response> whenResponse = AHClient
                .preparePut(baseUrl + url)
                .setBody(jsonObject)
                .execute();
        NetworkThrowingFunction callback = () -> {
            try {
                Response response = whenResponse.get();
                if (response.getStatusCode() != 200) {
                    throw new NoNetworkException("Can't connect to the server",
                            new IncorrectCodeExeption("Code was" + String.valueOf(response.getStatusCode())));
                }
            } catch (InterruptedException | ExecutionException e) {
                throw new NoNetworkException("Can't connect to the server", e);
            }
        };
        whenResponse.addListener((Runnable) callback, Executors.newCachedThreadPool());
    }

    public <T> void deleteRequest(String url) {
        ListenableFuture<Response> whenResponse = AHClient
                .prepareDelete(baseUrl + url)
                .execute();
        NetworkThrowingFunction callback = () -> {
            try {
                Response response = whenResponse.get();
                if (response.getStatusCode() != 200) {
                    throw new NoNetworkException("Can't connect to the server",
                            new IncorrectCodeExeption("Code was" + String.valueOf(response.getStatusCode())));
                }
            } catch (InterruptedException | ExecutionException e) {
                throw new NoNetworkException("Can't connect to the server", e);
            }
        };
        whenResponse.addListener((Runnable) callback, Executors.newCachedThreadPool());
    }
}