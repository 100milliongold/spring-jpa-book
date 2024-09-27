package jpabook.jpashop.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;

@SpringBootTest
@Transactional
public class OrderServiceTest {

    @Autowired EntityManager em;
    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepository;

    
    @Test
    public void 상품주문() throws Exception {
        Member member = createMember();
        Book book = createBook("시골책" , 10000 , 10);
        
        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);
        
        Order getOrder = orderRepository.findOne(orderId);
        Assertions.assertThat(OrderStatus.ORDER).withFailMessage("상품주문시 상태는 %s", OrderStatus.ORDER).isEqualTo(getOrder.getStatus());
        Assertions.assertThat(getOrder.getOrderItems().size()).withFailMessage("주문한 상품 종류 수가 정확해야 한다.").isEqualTo(1);
        Assertions.assertThat(getOrder.getTotalPrice()).withFailMessage("주문 가격은 가격 * 수량 = %s 이다", 10000 * orderCount).isEqualTo(10000 * orderCount);
        Assertions.assertThat(book.getStockQuantity()).withFailMessage("주문 수량만큼 재고가 줄어야 한다").isEqualTo(8);
    }
    
    @Test
    public void 상품주문_재고수량초과() throws Exception {
        Member member = createMember();
        Book book = createBook("시골책" , 10000 , 10);
        
        int orderCount = 11;
        
        
        Throwable thrown = Assertions.catchThrowable(() -> {
            orderService.order(member.getId(), book.getId(), orderCount);
        });
        
        // then
        Assertions.assertThat(thrown).withFailMessage("재고 수량 부족 예외가 발생해야 한다.").isInstanceOf(NotEnoughStockException.class).hasMessageContaining("need more stock");
        
    }
    
    
    @Test
    public void 주문취소() throws Exception {
        Member member = createMember();
        Book book = createBook("시골책" , 10000 , 10);
        
        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);
        
        orderService.cancelOrder(orderId);
        Order getOrder = orderRepository.findOne(orderId);

        Assertions.assertThat(OrderStatus.CANCEL).withFailMessage("상품주문시 상태는 %s", OrderStatus.CANCEL).isEqualTo(getOrder.getStatus());
        Assertions.assertThat(book.getStockQuantity()).withFailMessage("주문 취소된 상품은 그만큼 재고가 증가해야 한다.").isEqualTo(10);
    }
    
    public Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울" , "강남" , "123-123"));
        em.persist(member);
        return member;
    }
    
    public Book createBook(String name , int price , int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }
}
