//package com.example.querydsl.config;
//
//import com.example.dailyapp.core.domain.member.Address;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//public class DataGenerator {
//
//    private List<String> names = new ArrayList<>();
//    private List<String> books = new ArrayList<>();
//    private List<Address> addresses = new ArrayList<>();
//
//    DataGenerator (){
//        initAddress();;
//        initName();
//    }
//
//    public String getName() {
//        Collections.shuffle(names);
//        return names.get(0);
//    }
//
//    public Address getAddress() {
//        Collections.shuffle(addresses);
//        return addresses.get(0);
//    }
//
//    void initAddress(){
//        addresses.add(new Address("부산", "남천", "051"));
//        addresses.add(new Address("대구", "달서", "414"));
//        addresses.add(new Address("서울", "신림", "02"));
//        addresses.add(new Address("울산", "공란", "034"));
//        addresses.add(new Address("서울", "한남동", "02"));
//        addresses.add(new Address("부산", "대연", "051"));
//        addresses.add(new Address("전북", "달서", "456"));
//        addresses.add(new Address("원주", "무실", "123"));
//        addresses.add(new Address("수원", "팔달구", "345"));
//    }
//
//    void initName () {
//        names.add("김지완");
//        names.add("김지영");
//        names.add("나경은");
//        names.add("도지수");
//        names.add("심민정");
//        names.add("정혜주");
//        names.add("박지혜");
//        names.add("정진우");
//        names.add("박진우");
//        names.add("한성욱");
//    }
//
//    void initBooks () {
//        names.add("김지완");
//        names.add("김지영");
//        names.add("나경은");
//        names.add("도지수");
//        names.add("심민정");
//        names.add("정혜주");
//        names.add("박지혜");
//        names.add("정진우");
//        names.add("박진우");
//        names.add("한성욱");
//    }
//
//}
