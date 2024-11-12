/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.taskflow.jcv.codegen;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-11-06
 */
@Data
public class Person {
    final Byte                  aByte  = 0;
    static Short                aShort = 1;
    AtomicLong                  num;
    private AtomicBoolean       atomicBoolean;
    private String              name;
    private int                 age;
    private String              email;
    private List<Person>        friends;      // Example of a nested collection
    private Map<String, String> attributes;   // Example of a map
    private Address             address;
    private Order               order;
    private User                user;

    @Data
    public static class Order {
        private String      orderId;
        private String      product;
        private int         quantity;
        private double      price;
        private Date        orderDate;
        private List<Goods> goods;
        private Timestamp   createTime;
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
        private int    quantity;
    }

    @RequiredArgsConstructor
    @Data
    public static class User {
        private final String username;
        private final String password;
        private final String email;
        private final Date   registrationDate;
    }
}
