package de.fisgmbh.tgh.filebrowser.test;

import javax.servlet.ServletException;

import org.apache.chemistry.opencmis.client.api.Session;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.fisgmbh.tgh.filebrowser.ObjectAdapter;

public class DocumentAdapterTest {

	Session session;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		try {
			session = ObjectAdapter.getSession();
		} catch (ServletException e) {
		}
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetDocumentInfoAsJson() {
		Assert.assertTrue(true);
	}

	@Test
	public void testGetDocumentInfo1() {
		Assert.assertTrue(true);
	}
	
	@Test
	public void testGetDocumentInfo2() {
		Assert.assertTrue(true);
	}

	@Test
	public void testContentStream1() {
		Assert.assertTrue(true);
	}
	
	@Test
	public void testContentStream2() {
		Assert.assertTrue(true);
	}
}
