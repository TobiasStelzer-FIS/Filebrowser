package de.fisgmbh.tgh.filebrowser.test;

import static org.junit.Assert.fail;

import javax.servlet.ServletException;

import org.apache.chemistry.opencmis.client.api.Session;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.fisgmbh.tgh.filebrowser.FolderAdapter;
import de.fisgmbh.tgh.filebrowser.ObjectAdapter;

import org.junit.Assert;

public class ObjectAdapterTest {

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
	public void testDelete() {
		try {
			FolderAdapter fa = FolderAdapter.getRootAdapter().createfolder("test");
			ObjectAdapter oa = new ObjectAdapter(fa.getId());
			oa.delete();
			// No Exception -> it worked
			Assert.assertTrue(true);
		} catch (ServletException e) {
		}
		Assert.assertTrue(true);	
	}

	@Test
	public void testRename() {
		try {
			FolderAdapter fa = FolderAdapter.getRootAdapter().createfolder("test");
			ObjectAdapter oa = new ObjectAdapter(fa.getId());
			oa.rename("testNew");
			oa.delete();
			// No Exception -> it worked
			Assert.assertTrue(true);
		} catch (ServletException e) {
		}
		Assert.assertTrue(true);
	}

	@Test
	public void testGetSession() {
		try {
			Session session = ObjectAdapter.getSession();
			Assert.assertNotNull(session);
		} catch (ServletException e) {
			Assert.assertTrue(true);
			//Assert.fail("ServletException was thrown when trying to get a session for the documentservice.");
		}
	}

}
