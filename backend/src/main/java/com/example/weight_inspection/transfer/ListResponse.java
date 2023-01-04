package com.example.weight_inspection.transfer;

import com.example.weight_inspection.models.Product;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

public class ListResponse<T> {
    private int page;
    private int totalPages;
    private long totalItems;
    private List<T> items;

    public ListResponse() {}

    public ListResponse(T item) {
        List<T> list = new ArrayList<T>();
        if (item != null) {
            list.add(item);
        }

        page = 0;
        items = list;
        totalItems = list.size();
        totalPages = 1;
    }

    public ListResponse(Page<T> pageRes) {
        page = pageRes.getNumber();
        totalPages = pageRes.getTotalPages();
        totalItems = pageRes.getTotalElements();
        items = pageRes.getContent();
    }

    @Override
    public String toString() {
        return "ListResponse [currentPage=" + page + ", totalPages=" + totalPages + ", totalItems=" + totalItems
                + ", items=" + items + "]";
    }

    public int getPage() {
        return page;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public long getTotalItems() {
        return totalItems;
    }

    public List<T> getItems() {
        return items;
    }
}
