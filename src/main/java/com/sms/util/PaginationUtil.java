package com.sms.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Small helper used to render the numbered pagination bar.
 * Shows up to 5 page numbers around the current page.
 */
public final class PaginationUtil {

	private PaginationUtil() {
	}

	public static List<Integer> getPageNumbers(int currentPage, int totalPages) {
		List<Integer> pages = new ArrayList<>();
		if (totalPages <= 0) {
			return pages;
		}
		int start = Math.max(0, currentPage - 2);
		int end = Math.min(totalPages - 1, start + 4);
		start = Math.max(0, end - 4);
		for (int i = start; i <= end; i++) {
			pages.add(i);
		}
		return pages;
	}
}
