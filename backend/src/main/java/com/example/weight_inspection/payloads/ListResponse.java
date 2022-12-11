package com.example.weight_inspection.payloads;

import java.util.List;

public class ListResponse<T> {
    private int page;
    private int totalPages;
    private long totalItems;
    private List<T> items;

    public ListResponse() {}

    @Override
    public String toString() {
        return "ListResponse [currentPage=" + page + ", totalPages=" + totalPages + ", totalItems=" + totalItems
                + ", items=" + items + "]";
    }

    public int getPage() {
        return page;
    }

    public void setPage(int currentPage) {
        this.page = currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(long totalItems) {
        this.totalItems = totalItems;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }
}
