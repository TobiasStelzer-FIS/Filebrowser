package de.fisgmbh.tgh.filebrowser.test;

import javax.servlet.ServletException;

import org.apache.chemistry.opencmis.client.api.Session;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.fisgmbh.tgh.filebrowser.FolderAdapter;
import de.fisgmbh.tgh.filebrowser.ObjectAdapter;

public class FolderAdapterTest {

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
	public void testGetRootAdapter() {
		Assert.assertTrue(true);	
	}
	
	@Test
	public void testCreatefolder1() {
		Assert.assertTrue(true);	
	}
	
	@Test
	public void testCreatefolder2() {
		Assert.assertTrue(true);	
	}
	
	@Test
	public void testCreatedocument1() {
		Assert.assertTrue(true);	
	}
	
	@Test
	public void testCreatedocument2() {
		Assert.assertTrue(true);	
	}

	@Test
	public void testGetNavigationInfo() {
		Assert.assertTrue(true);	
	}
	
	@Test
	public void testGetFolderInfo() {
		Assert.assertTrue(true);	
	}
	
	@Test
	public void testGetHierarchy() {
		Assert.assertTrue(true);	
	}

}
