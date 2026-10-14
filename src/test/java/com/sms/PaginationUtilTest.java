package com.sms;

import com.sms.util.PaginationUtil;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaginationUtilTest {

	@Test
	void returnsAllPagesWhenTotalIsSmall() {
		List<Integer> pages = PaginationUtil.getPageNumbers(0, 3);
		assertEquals(List.of(0, 1, 2), pages);
	}

	@Test
	void windowsFivePagesAroundCurrent() {
		List<Integer> pages = PaginationUtil.getPageNumbers(5, 10);
		assertEquals(List.of(3, 4, 5, 6, 7), pages);
	}

	@Test
	void handlesEmptyAndLastPages() {
		assertTrue(PaginationUtil.getPageNumbers(0, 0).isEmpty());
		assertEquals(List.of(5, 6, 7, 8, 9), PaginationUtil.getPageNumbers(9, 10));
	}
}
