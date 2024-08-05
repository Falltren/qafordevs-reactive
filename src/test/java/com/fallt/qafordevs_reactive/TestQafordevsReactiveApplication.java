package com.fallt.qafordevs_reactive;

import org.springframework.boot.SpringApplication;

public class TestQafordevsReactiveApplication {

	public static void main(String[] args) {
		SpringApplication.from(QafordevsReactiveApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
