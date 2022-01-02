package com.example.querydsl.config;//package com.example.dailyapp.config;
//
//import com.example.dailyapp.core.domain.category.Category;
//import com.example.dailyapp.core.domain.delivery.Delivery;
//import com.example.dailyapp.core.domain.item.Book;
//import com.example.dailyapp.core.domain.item.BookType;
//import com.example.dailyapp.core.domain.member.Address;
//import com.example.dailyapp.core.domain.member.Member;
//import com.example.dailyapp.core.domain.order.Order;
//import com.example.dailyapp.core.domain.order.OrderItem;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.annotation.PostConstruct;
//import javax.persistence.EntityManager;
//import java.math.BigDecimal;
//
//@Component
//@RequiredArgsConstructor
//public class InitDatabase {
//
//    private final InitService initService;
//
//    @PostConstruct
//    public void init(){
//        initService.memberInit();
//    }
//
//    @Component
//    @Transactional
//    @RequiredArgsConstructor
//    static class InitService {
//        private final EntityManager entityManager;
//
//        public void memberInit (){
//            Member member = getMember("Kim", new Address("서울", "가로수길", "3304"));
//            getPersist(member);
//
//            Book bookA = new Book(BookType.DEVELOPMENT,"Java#", "Jun", "32431242341234", BigDecimal.valueOf(25000), 30, new Category("도서"));
//            Book bookB = new Book(BookType.SELF_DEVELOPMENT,"위키북스", "Kim", "1243142314", BigDecimal.valueOf(30000), 5, new Category("커피"));
//            entityManager.persist(bookA);
//            entityManager.persist(bookB);
//
//            OrderItem orderItem = OrderItem.createOrderItem(bookA, new BigDecimal("22000"), 3);
//            OrderItem orderItemB = OrderItem.createOrderItem(bookB, new BigDecimal("32000"), 5);
//            Delivery deliveryAddress = new Delivery(new Address("서울시", "남부순환로", "334394034"));
//            deliveryAddress.changeStatus();
//            Order order =  Order.createOrder(member, deliveryAddress, orderItem, orderItemB);
//            entityManager.persist(order);
//        }
//
//        private void getPersist(Member member) {
//            entityManager.persist(member);
//        }
//
//        private Member getMember(String name, Address address) {
//            return new Member(name, address);
//        }
//    }
//}
