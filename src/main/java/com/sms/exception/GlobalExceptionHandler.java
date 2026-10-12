package com.sms.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

/**
 * Global exception handling for the whole application.
 * Instead of the default whitelabel error page the user always
 * lands on a friendly 404 / 500 screen.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	/**
	 * Unknown id passed to student / course / enrollment screens.
	 */
	@ExceptionHandler(ResourceNotFoundException.class)
	public ModelAndView handleResourceNotFound(ResourceNotFoundException ex) {
		log.warn("Resource not found: {}", ex.getMessage());

		ModelAndView mav = new ModelAndView("error/404");
		mav.setStatus(HttpStatus.NOT_FOUND);
		mav.addObject("message", ex.getMessage());
		return mav;
	}

	/**
	 * Fallback - anything unexpected goes to the 500 screen.
	 */
	@ExceptionHandler(Exception.class)
	public ModelAndView handleGenericException(Exception ex) {
		log.error("Unexpected error occurred", ex);

		ModelAndView mav = new ModelAndView("error/500");
		mav.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
		mav.addObject("message", "Something went wrong on our end. Please try again later.");
		return mav;
	}
}
