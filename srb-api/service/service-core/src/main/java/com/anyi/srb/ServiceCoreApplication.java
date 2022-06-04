package com.anyi.srb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;


/**
 * @author 安逸i
 * @version 1.0
 */

@SpringBootApplication
@ComponentScan(basePackages = {"com.anyi"})
@EnableTransactionManagement
public class ServiceCoreApplication {
    public static void main(String[] args) {
        try {
            SpringApplication.run(ServiceCoreApplication.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
