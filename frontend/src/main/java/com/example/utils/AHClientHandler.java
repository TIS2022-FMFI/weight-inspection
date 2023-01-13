package com.example.utils;

import com.example.controller.TableController;
import com.example.model.Page;
import com.example.utils.exeptions.IncorrectCodeExeption;
import com.google.gson.Gson;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.util.concurrent.CompletableFuture;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Response;
import com.google.gson.reflect.TypeToken;

import io.netty.channel.unix.Socket;

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
                    Platform.runLater(() -> {
                        Alert errorAlert = new Alert(AlertType.ERROR);
                        errorAlert.setHeaderText("Can't connect to the server");
                        errorAlert.setContentText("Check your internet connection");
                        errorAlert.showAndWait();
                    });
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
                                            System.out.println(page);
                                            System.out.println(pageSize);
                                            System.out.println(newPage.getTotalPages());
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
        CompletableFuture<Response> whenResponse = AHClient
                .preparePost(baseUrl + url)
                .setHeader("Content-Type", "application/json")
                .setBody(jsonObject)
                .execute()
                .toCompletableFuture()
                .exceptionally(t -> {
                    Platform.runLater(() -> {
                        Alert errorAlert = new Alert(AlertType.ERROR);
                        errorAlert.setHeaderText("Can't connect to the server");
                        errorAlert.setContentText("Check your internet connection");
                        errorAlert.showAndWait();
                    });
                    return null;
                })
                .thenApply(response -> {
                    if (response.getStatusCode() == 404) {
                        Platform.runLater(() -> {
                            Alert errorAlert = new Alert(AlertType.ERROR);
                            errorAlert.setHeaderText("Not found error while comunicating with server");
                            errorAlert.setContentText(
                                    "Possibly you've tried to change connection of two items and used id of an item that does not exist");
                            errorAlert.showAndWait();
                        });
                    } else if (response.getStatusCode() < 200 || response.getStatusCode() > 299) {
                        Platform.runLater(() -> {
                            Alert errorAlert = new Alert(AlertType.ERROR);
                            errorAlert.setHeaderText("Error while comunicating with server");
                            errorAlert.setContentText("Error code " + String.valueOf(response.getStatusCode()));
                            errorAlert.showAndWait();
                        });
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
        CompletableFuture<Response> whenResponse = AHClient
                .preparePut(baseUrl + url)
                .setHeader("Content-Type", "application/json")
                .setBody(jsonObject)
                .execute()
                .toCompletableFuture()
                .exceptionally(t -> {
                    Platform.runLater(() -> {
                        Alert errorAlert = new Alert(AlertType.ERROR);
                        errorAlert.setHeaderText("Can't connect to the server");
                        errorAlert.setContentText("Check your internet connection");
                        errorAlert.showAndWait();
                    });
                    return null;
                })
                .thenApply(response -> {
                    if (response.getStatusCode() == 404) {
                        Platform.runLater(() -> {
                            Alert errorAlert = new Alert(AlertType.ERROR);
                            errorAlert.setHeaderText("Not found error while comunicating with server");
                            errorAlert.setContentText(
                                    "Possibly you've tried to change connection of two items and used id of an item that does not exist");
                            errorAlert.showAndWait();
                        });
                    } else if (response.getStatusCode() < 200 || response.getStatusCode() > 299) {
                        Platform.runLater(() -> {
                            Alert errorAlert = new Alert(AlertType.ERROR);
                            errorAlert.setHeaderText("Error while comunicating with server");
                            errorAlert.setContentText("Error code " + String.valueOf(response.getStatusCode()));
                            errorAlert.showAndWait();
                        });
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
                    Platform.runLater(() -> {
                        Alert errorAlert = new Alert(AlertType.ERROR);
                        errorAlert.setHeaderText("Can't connect to the server");
                        errorAlert.setContentText("Check your internet connection");
                        errorAlert.showAndWait();
                    });
                    return null;
                })
                .thenApply(response -> {
                    if (response.getStatusCode() < 200 || response.getStatusCode() > 299) {
                        Platform.runLater(() -> {
                            Alert errorAlert = new Alert(AlertType.ERROR);
                            errorAlert.setHeaderText("Error while comunicating with server");
                            errorAlert.setContentText("Error code " + String.valueOf(response.getStatusCode()));
                            errorAlert.showAndWait();
                        });
                    }
                    Platform.runLater(
                            () -> {
                                controller.updateTable();
                            });
                    return null;
                });
    }
}