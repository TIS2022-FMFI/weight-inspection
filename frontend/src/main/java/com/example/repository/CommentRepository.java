package com.example.repository;

import com.example.model.CommentModel;
import com.example.repository.CommentRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;

import static org.asynchttpclient.Dsl.*;

public class CommentRepository {
    private ObservableList<CommentModel> comments;

    public CommentRepository() {
        comments = FXCollections.observableArrayList();
    }

    public ObservableList<CommentModel> getComments() {
        return comments;
    }

    public void updateComments() {
        AsyncHttpClient asyncHttpClient = asyncHttpClient();
        ListenableFuture<Response> whenResponse = asyncHttpClient
                .prepareGet("https://jsonplaceholder.typicode.com/comments")
                .execute();
        Runnable callback = () -> {
            try {
                Response response = whenResponse.get();
                Type listType = new TypeToken<ArrayList<CommentModel>>() {
                }.getType();
                List<CommentModel> newComments = new Gson().fromJson(response.getResponseBody(), listType);
                comments.clear();
                comments.addAll(newComments);

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        };
        whenResponse.addListener(callback, Executors.newCachedThreadPool());
    }

}
