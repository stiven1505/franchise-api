package com.stiven.franchise_api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class FranchiseApiApplicationTests {

	@Test
	void mainMethodRuns() {
		assertDoesNotThrow(() -> FranchiseApiApplication.class.getDeclaredConstructor().newInstance());
	}
}
