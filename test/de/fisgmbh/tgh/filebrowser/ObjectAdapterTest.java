package de.fisgmbh.tgh.filebrowser;

import static org.junit.Assert.fail;

import javax.servlet.ServletException;

import org.apache.chemistry.opencmis.client.api.Session;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.junit.Assert;

public class ObjectAdapterTest {

	/*
	 * Can't use Unit-Tests because the App has to be running on 
	 * the SCP to work with the Documentservice.
	 */
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testDelete() {
		fail("Not yet implemented");
	}

	@Test
	public void testRename() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetId() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetId() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetSession() {
		fail("Not yet implemented");		
	}

}
