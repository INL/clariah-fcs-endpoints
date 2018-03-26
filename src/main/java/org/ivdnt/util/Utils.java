package org.ivdnt.util;

public class Utils {

	/**
	 * print the stack trace to the ERROR console convenient when debugging
	 * 
	 * @param e
	 */
	public static void printStackTrace(Exception e) {

		System.err.println(e.getMessage());

		StackTraceElement[] elements = e.getStackTrace();
		for (int i = 0; i < elements.length; i++) {
			StackTraceElement s = elements[i];
			System.err.println("\tat " + s.getClassName() + "." + s.getMethodName() + "(" + s.getFileName() + ":"
					+ s.getLineNumber() + ")");
		}

	}

}
