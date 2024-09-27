package jpabook.jpashop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;

@SpringBootApplication
public class JpashopApplication {

	public static void main(String[] args) {

		Hello hello = new Hello();
		hello.setData("hello");
		String data = hello.getData();

		System.out.println("data = " + data);
		SpringApplication.run(JpashopApplication.class, args);
	}

	@Bean
	Hibernate6Module hibernate6Module() {

		Hibernate6Module hibernate6Module = new Hibernate6Module();
		// hibernate6Module.configure(Hibernate6Module.Feature.FORCE_LAZY_LOADING,
		// true);
		return hibernate6Module;
	}

}
