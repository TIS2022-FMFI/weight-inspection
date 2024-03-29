package com.example.utils;

import com.example.controller.TableController;
import com.example.model.Page;
import com.example.utils.exeptions.IncorrectCodeExeption;
import com.google.gson.Gson;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.BoundRequestBuilder;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Param;
import org.asynchttpclient.Response;
import com.google.gson.reflect.TypeToken;

import io.netty.buffer.search.AhoCorasicSearchProcessorFactory;
import io.netty.channel.unix.Socket;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

import static org.asynchttpclient.Dsl.*;

public class AHClientHandler {

    private static AHClientHandler AHClientHandler;
    private AsyncHttpClient AHClient;
    private Gson gson;
    private UUID lastUuid;
    private String baseUrl;

    private AHClientHandler(String baseUrl) {
        this.baseUrl = baseUrl;
        this.AHClient = asyncHttpClient();
        this.gson = new Gson();
    }

    public static void remake() {
        AHClientHandler = new AHClientHandler(AdminState.getServer());
    }

    public static AHClientHandler getAHClientHandler() {
        if (AHClientHandler == null) {
            AHClientHandler = new AHClientHandler(AdminState.getServer());
        }
        return AHClientHandler;
    }

    public AsyncHttpClient getAHClient() {
        return AHClient;
    }

    public <T> List<T> getPageSync(String url, List<Param> params, int page, int pageSize, Class<T> type) {
        BoundRequestBuilder request = AHClient
                .prepareGet(baseUrl + url)
                .addQueryParam("page", String.valueOf(page))
                .addQueryParam("page_size", String.valueOf(pageSize))
                .setRealm(org.asynchttpclient.Dsl.basicAuthRealm(AdminState.getUserName(), AdminState.getPassword()))
                .addQueryParams(params);
        ListenableFuture<Response> whenResponse = request.execute();
        try {
            Response response = whenResponse.get();
            if (response.getStatusCode() == 404 || response.getStatusCode() == 409) {
                Alert errorAlert = new Alert(AlertType.ERROR);
                errorAlert.setHeaderText("Nastala chyba");
                errorAlert.setContentText("Administrator bol notifikovany. Tato notifikacia zmyzne cez 3 sekundy.");
                PauseTransition delay = new PauseTransition(Duration.seconds(3));
                delay.setOnFinished(e -> {
                    errorAlert.hide();
                });
                delay.play();
                errorAlert.showAndWait();
                return null;
            }
            Type pageType = TypeToken.getParameterized(Page.class, type).getType();
            Page<T> newPage = new Gson().fromJson(response.getResponseBody(),
                    pageType);
            return newPage.getItems();

        } catch (Exception e) {
            Alert errorAlert = new Alert(AlertType.ERROR);
            errorAlert.setHeaderText("Can't connect to server");
            errorAlert.setContentText("Check if you have internet connection");
            PauseTransition delay = new PauseTransition(Duration.seconds(3));
            delay.setOnFinished(event -> {
                errorAlert.hide();
            });
            delay.play();
            errorAlert.showAndWait();
            return null;
        }
    }

    public <T, T2> T2 postRequestSync(String url, T object, Class<T2> type) {
        String jsonObject = gson.toJson(object);
        BoundRequestBuilder request = AHClient
                .preparePost(baseUrl + url)
                .setHeader("Content-Type", "application/json")
                .setRealm(org.asynchttpclient.Dsl.basicAuthRealm(AdminState.getUserName(), AdminState.getPassword()))
                .setBody(jsonObject);
        ListenableFuture<Response> whenResponse = request.execute();
        System.out.println(jsonObject);
        try {
            Response response = whenResponse.get();
            System.out.println(response.getStatusCode());
            System.out.println(response.getResponseBody());
            if (response.getStatusCode() == 404 || response.getStatusCode() == 409) {
                Alert errorAlert = new Alert(AlertType.ERROR);
                errorAlert.setHeaderText("Nastala chyba");
                errorAlert.setContentText("Administrator bol notifikovany. Tato notifikacia zmyzne cez 3 sekundy.");
                PauseTransition delay = new PauseTransition(Duration.seconds(3));
                delay.setOnFinished(e -> {
                    errorAlert.hide();
                });
                delay.play();
                errorAlert.showAndWait();
                return null;
            }
            if (response.getStatusCode() > 299 || response.getStatusCode() < 200) {
                Alert errorAlert = new Alert(AlertType.ERROR);
                errorAlert.setHeaderText("Nastala chyba");
                errorAlert.setContentText("Nieco sa stalo na serveri");
                PauseTransition delay = new PauseTransition(Duration.seconds(3));
                delay.setOnFinished(e -> {
                    errorAlert.hide();
                });
                delay.play();
                errorAlert.showAndWait();
                return null;
            }
            T2 item = new Gson().fromJson(response.getResponseBody(), type);
            return item;

        } catch (Exception e) {
            Alert errorAlert = new Alert(AlertType.ERROR);
            errorAlert.setHeaderText("Can't connect to server");
            errorAlert.setContentText("Check if you have internet connection");
            PauseTransition delay = new PauseTransition(Duration.seconds(3));
            delay.setOnFinished(event -> {
                errorAlert.hide();
            });
            delay.play();
            errorAlert.showAndWait();
            return null;
        }
    }

    public <T> void getPage(String url, int page, int pageSize, ObservableList<T> returnList, Class<T> type,
            TableController controller) {
        UUID myUuid = UUID.randomUUID();
        this.lastUuid = myUuid;
        CompletableFuture<Response> whenResponse = AHClient
                .prepareGet(baseUrl + url)
                .addQueryParam("page", String.valueOf(page))
                .addQueryParam("page_size", String.valueOf(pageSize))
                .setRealm(org.asynchttpclient.Dsl.basicAuthRealm(AdminState.getUserName(), AdminState.getPassword()))
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
                            if (myUuid != this.lastUuid) {
                                return null;
                            }
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
        UUID myUuid = UUID.randomUUID();
        this.lastUuid = myUuid;
        String jsonObject = gson.toJson(object);
        CompletableFuture<Response> whenResponse = AHClient
                .preparePost(baseUrl + url)
                .setHeader("Content-Type", "application/json")
                .setRealm(org.asynchttpclient.Dsl.basicAuthRealm(AdminState.getUserName(), AdminState.getPassword()))
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
                    if (myUuid != this.lastUuid) {
                        return null;
                    }
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
        UUID myUuid = UUID.randomUUID();
        this.lastUuid = myUuid;
        String jsonObject = gson.toJson(object);
        CompletableFuture<Response> whenResponse = AHClient
                .preparePut(baseUrl + url)
                .setHeader("Content-Type", "application/json")
                .setRealm(org.asynchttpclient.Dsl.basicAuthRealm(AdminState.getUserName(), AdminState.getPassword()))
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
                    if (myUuid != this.lastUuid) {
                        return null;
                    }
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
        UUID myUuid = UUID.randomUUID();
        this.lastUuid = myUuid;
        CompletableFuture<Response> whenResponse = AHClient
                .prepareDelete(baseUrl + url)
                .setRealm(org.asynchttpclient.Dsl.basicAuthRealm(AdminState.getUserName(), AdminState.getPassword()))
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
                    if (myUuid != this.lastUuid) {
                        return null;
                    }
                    if (response.getStatusCode() == 500 || response.getStatusCode() == 409) {
                        Platform.runLater(() -> {
                            Alert errorAlert = new Alert(AlertType.ERROR);
                            errorAlert.setHeaderText("Error while comunicating with server");
                            errorAlert.setContentText(
                                    "Pravdepodobne sa snazite vymazat nieco, co ma pripojenie.");
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

    public <T> T getRequestSync(String url, List<Param> params, Class<T> type) {
        ListenableFuture<Response> whenResponse = AHClient
                .prepareGet(baseUrl + url)
                .setRealm(org.asynchttpclient.Dsl.basicAuthRealm(AdminState.getUserName(), AdminState.getPassword()))
                .addQueryParams(params)
                .execute();
        try {
            Response response = whenResponse.get();
            if (response.getStatusCode() == 401) {
                Alert errorAlert = new Alert(AlertType.ERROR);
                errorAlert.setHeaderText("Nastala chyba");
                errorAlert.setContentText("Asi ste vyuzily nespravne meno alebo heslo");
                PauseTransition delay = new PauseTransition(Duration.seconds(3));
                delay.setOnFinished(e -> {
                    errorAlert.hide();
                });
                delay.play();
                errorAlert.showAndWait();
                AdminState.setUserName("");
                AdminState.setPassword("");
                return null;
            }
            if (response.getStatusCode() == 404 || response.getStatusCode() == 409) {
                Alert errorAlert = new Alert(AlertType.ERROR);
                errorAlert.setHeaderText("Nastala chyba");
                errorAlert.setContentText("Tato notifikacia zmizne cez 3 sekundy.");
                PauseTransition delay = new PauseTransition(Duration.seconds(3));
                delay.setOnFinished(e -> {
                    errorAlert.hide();
                });
                delay.play();
                errorAlert.showAndWait();
                return null;
            }
            T newObject = new Gson().fromJson(response.getResponseBody(), type);
            return newObject;

        } catch (Exception e) {
            Alert errorAlert = new Alert(AlertType.ERROR);
            errorAlert.setHeaderText("Nastala chyba");
            errorAlert.setContentText("Tato notifikacia zmizne cez 3 sekundy.");
            PauseTransition delay = new PauseTransition(Duration.seconds(3));
            delay.setOnFinished(e2 -> {
                errorAlert.hide();
            });
            delay.play();
            errorAlert.showAndWait();
            return null;
        }
    }

    public void postImage(String url, File image) {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            String auth = AdminState.getUserName() + ":" + AdminState.getPassword();
            byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.ISO_8859_1));
            String authHeader = "Basic " + new String(encodedAuth);
            HttpEntity data = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                    .addBinaryBody("image", image, ContentType.IMAGE_PNG, image.getName())
                    .build();
            HttpUriRequest request = RequestBuilder.post(baseUrl + url)
                    .setHeader(HttpHeaders.AUTHORIZATION, authHeader)
                    .setEntity(data)
                    .build();
            ResponseHandler<String> responseHandler = response -> {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    image.delete();
                    return null;
                } else {
                    Alert errorAlert = new Alert(AlertType.ERROR);
                    errorAlert.setHeaderText("Can't connect to server");
                    errorAlert.setContentText("Check if you have internet connection");
                    PauseTransition delay = new PauseTransition(Duration.seconds(3));
                    delay.setOnFinished(event -> {
                        errorAlert.hide();
                    });
                    delay.play();
                    errorAlert.showAndWait();
                    image.delete();
                    return null;
                }
            };
            httpclient.execute(request, responseHandler);
        } catch (IOException e) {
            Alert errorAlert = new Alert(AlertType.ERROR);
            errorAlert.setHeaderText("Can't connect to server");
            errorAlert.setContentText("Check if you have internet connection");
            PauseTransition delay = new PauseTransition(Duration.seconds(3));
            delay.setOnFinished(event -> {
                errorAlert.hide();
            });
            delay.play();
            errorAlert.showAndWait();
            image.delete();
        }
    }

    public void postExport() {
        BoundRequestBuilder request = AHClient
                .preparePost(baseUrl + "/email/export")
                .setRealm(org.asynchttpclient.Dsl.basicAuthRealm(AdminState.getUserName(), AdminState.getPassword()));
        ListenableFuture<Response> whenResponse = request.execute();
        try {
            Response response = whenResponse.get();
            if (response.getStatusCode() == 200) {
                Alert errorAlert = new Alert(AlertType.CONFIRMATION);
                errorAlert.setHeaderText("OK");
                errorAlert.setContentText("Export bol poslany na vas email.");
                errorAlert.showAndWait();
                return;
            }
            if (response.getStatusCode() > 299 || response.getStatusCode() < 200) {
                Alert errorAlert = new Alert(AlertType.ERROR);
                errorAlert.setHeaderText("Nastala chyba");
                errorAlert.setContentText("Mozno nemate priradenu emailovu adresu.");
                errorAlert.showAndWait();
                return;
            }
        } catch (Exception e) {
            Alert errorAlert = new Alert(AlertType.ERROR);
            errorAlert.setHeaderText("Can't connect to server");
            errorAlert.setContentText("Check if you have internet connection");
            errorAlert.showAndWait();
        }
    }
}