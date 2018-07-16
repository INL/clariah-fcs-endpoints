package org.ivdnt.util;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {

	// logger
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	/**
	 * print the stack trace to the ERROR console convenient when debugging
	 * 
	 * @param e
	 */
	public static void printStackTrace(Exception e) {

		logger.error(e.getMessage());

		StackTraceElement[] elements = e.getStackTrace();
		for (int i = 0; i < elements.length; i++) {
			StackTraceElement s = elements[i];
			logger.error("\tat " + s.getClassName() + "." + s.getMethodName() + "(" + s.getFileName() + ":"
					+ s.getLineNumber() + ")");
		}

	}

}
