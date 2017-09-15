package de.fisgmbh.tgh.filebrowser.test;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

public class CustomRunListener extends RunListener {

	private PrintWriter writer;
	private HashMap<String, List<String>> classMethodsMap;
	private HashMap<String, List<String>> failedClassMethodsMap;

	public CustomRunListener(PrintWriter writer) {
		this.writer = writer;

		classMethodsMap = new HashMap<String, List<String>>();
		failedClassMethodsMap = new HashMap<String, List<String>>();
	}

	private void insertItem(HashMap<String, List<String>> map, String clazz, String method) {
		List<String> methods = null;
		if (!map.containsKey(clazz)) {
			map.put(clazz, new ArrayList<String>());
		}
		methods = map.get(clazz);
		methods.add(method);
	}

	@Override
	public void testRunStarted(Description description) throws Exception {
	}

	@Override
	public void testRunFinished(Result result) throws Exception {

		try {
			writer.println("JUnit Test Cases");
			writer.println();
			for (String clazz : classMethodsMap.keySet()) {
				List<String> methods = classMethodsMap.get(clazz);
				if (methods == null) {
					continue;
				}

				writer.println();
				writer.println("Class: " + clazz);
				writer.println("Methods:");
				for (String method : methods) {
					String temp = method + ":";
					String status = wasMethodSuccesful(clazz, method) ? "Success!" : "FAILURE!";
					writer.println(String.format("   %-23s %s", temp, status));
				}
			}

			writer.println();
			writer.println("Final stats:");
			writer.println("  Status: " + (result.wasSuccessful() ? "SUCCESS!" : "FAILURE!"));
			writer.println("  Tests run: " + result.getRunCount());
			writer.println("  Failures: " + result.getFailureCount());
			writer.println("  Ignored: " + result.getIgnoreCount());
			writer.println();
		} catch (Exception e) {
			writer.println(e.getMessage());
		}
	}

	private boolean wasMethodSuccesful(String clazz, String method) {
		List<String> methods = failedClassMethodsMap.get(clazz);
		if (methods == null) {
			return true;
		}
		if (methods.contains(method)) {
			return false;
		}
		return true;
	}

	@Override
	public void testStarted(Description description) throws Exception {
		String clazz = description.getClassName();
		String method = description.getMethodName();
		try {
			clazz = clazz.substring(clazz.lastIndexOf(".") + 1, clazz.length());
			insertItem(classMethodsMap, clazz, method);
		} catch (Exception e) {

		}
	}

	@Override
	public void testFinished(Description description) throws Exception {
	}

	@Override
	public void testFailure(Failure failure) throws Exception {
		String testHeader = failure.getTestHeader();
		int i1, i2, i3, i4;
		i1 = 0;
		i2 = testHeader.indexOf("(");
		i3 = testHeader.lastIndexOf(".") + 1;
		i4 = testHeader.length() - 1;

		try {
			String method = testHeader.substring(i1, i2);
			String clazz = testHeader.substring(i3, i4);

			insertItem(failedClassMethodsMap, clazz, method);
		} catch (Exception e) {
			writer.println("   Exception: " + e.getMessage());
		}
	}

	@Override
	public void testAssumptionFailure(Failure failure) {
		String testHeader = failure.getTestHeader();
		int i1, i2, i3, i4;
		i1 = 0;
		i2 = testHeader.indexOf("(");
		i3 = testHeader.lastIndexOf(".") + 1;
		i4 = testHeader.length() - 1;

		try {
			String method = testHeader.substring(i1, i2);
			String clazz = testHeader.substring(i3, i4);

			insertItem(failedClassMethodsMap, clazz, method);
		} catch (Exception e) {
			writer.println("   Exception: " + e.getMessage());
		}
	}

	@Override
	public void testIgnored(Description description) throws Exception {
	}

}
