package co.kr.allpick.domain.order.util;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class OrderNumberGenerator {

    private static final SecureRandom secureRandom = new SecureRandom();

    private OrderNumberGenerator() {}

    public static String generate() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String random = String.format("%06d", secureRandom.nextInt(1000000));
        return "ORD-" + date + "-" + random;
    }
}