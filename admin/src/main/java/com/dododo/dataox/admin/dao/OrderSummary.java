package com.dododo.dataox.admin.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderSummary {

    private Item totalSuppliedItems;

    private Item totalConsumedItems;

    private double totalProfit;

    @Data
    @AllArgsConstructor
    public static class Item {

        private double count;

        private double sum;

    }
}
