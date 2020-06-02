package com.bfwg.rest;

import com.google.gson.Gson;

public class RESTPagination {

    private int page;
    private int limit;
    private int totalPages;
    private long totalItems;

    public RESTPagination(int page, int limit, long totalItems) {
        this.page = page;
        this.limit = limit;
        if (limit != 0) {
            this.totalPages = (totalItems % limit == 0) ? (int) (totalItems / limit) : ((int) (totalItems / limit) + 1);
        } else this.totalPages = 0;
        this.totalItems = totalItems;
    }

    public RESTPagination(int page, int limit, int totalPages, long totalItems) {
        this.page = page;
        this.limit = limit;
        if (limit != 0) {
            this.totalPages = (totalItems % limit == 0) ? (int) (totalItems / limit) : ((int) (totalItems / limit) + 1);
        } else this.totalPages = 0;
        this.totalItems = totalItems;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
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

    public static void main(String[] args) {
        System.out.println(new Gson().toJson(new RESTPagination(1, 10, 31)));
    }
}
