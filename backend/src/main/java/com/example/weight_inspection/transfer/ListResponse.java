package com.example.weight_inspection.transfer;

import com.example.weight_inspection.models.Product;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
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

    public ListResponse(Set<T> set) {
        page = 0;
        totalPages = 1;
        totalItems = set.size();
        items = new ArrayList<>(set);
    }

    @Override
    public String toString() {
        return "ListResponse [currentPage=" + page + ", totalPages=" + totalPages + ", totalItems=" + totalItems
                + ", items=" + items + "]";
    }
}
