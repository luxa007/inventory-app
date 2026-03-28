package com.Luxa.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Bootstrap class for the Inventory Spring Boot application.
 *
 * <p>Starts an embedded web server and triggers component scanning for the
 * {@code com.Luxa.inventory} package so that controllers, configuration,
 * and other Spring-managed beans are discovered automatically.</p>
 */
@SpringBootApplication
public class InventoryApplication {

	/**
	 * Application entry point invoked by the JVM.
	 *
	 * @param args optional command-line arguments, currently unused
	 */
	public static void main(String[] args) {
		SpringApplication.run(InventoryApplication.class, args);
	}

}
