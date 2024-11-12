package cn.taskflow.jcv.codegen;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-11-06
 */
@Data
public class Person {
    private String name;
    private int age;
    private String email;
    private List<Person> friends; // Example of a nested collection
    private Map<String, String> attributes; // Example of a map
    private Address address;
    private Order order;
    private User user;

    @Data
    public static class Order {
        private String orderId;
        private String product;
        private int quantity;
        private double price;
        private Date orderDate;
        private List<Goods> goods;
        private Timestamp createTime;
    }

    @Data
    public static class Address {
        private String province;
        private String city;
        private String district;
        private String street;
        private String postalCode;
        private String recipientName;
        private String phoneNumber;
    }

    @Data
    public static class Goods {
        private String goodsId;
        private String name;
        private String category;
        private double price;
        private int quantity;
    }
    @RequiredArgsConstructor
    @Data
    public static class User {
        private final String username;
        private final String password;
        private final String email;
        private final Date registrationDate;
    }
}
