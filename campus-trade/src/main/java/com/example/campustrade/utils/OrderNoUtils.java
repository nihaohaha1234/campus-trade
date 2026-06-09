package com.example.campustrade.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;


//生成订单号
public class OrderNoUtils {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMddHHmmss");

    public static String generateOrderNo(){
        String timePart = LocalDateTime.now().format(formatter);
        int randomPart = ThreadLocalRandom.current().nextInt(1000,10000);
        return timePart+randomPart;
    }
}
