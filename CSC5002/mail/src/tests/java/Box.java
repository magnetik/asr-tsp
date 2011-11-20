import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import box.BoxException;
import box.BoxStatus;
import box.BoxType;
import box.MailBox;
import box.MailBoxManager;
import box.message.Message;

public class Box {
	MailBoxManager mbm;
	BoxStatus s;
	
	@Before 
	public void setUp() {
		mbm = new MailBoxManager(); 
	}
	

	@After
	public void tearDown(){
		mbm = null;
	}
	
	@Test
	public void testRemoveBox() throws BoxException {	
		s = mbm.addBox("lafontai", BoxType.Mail);
		
		s = mbm.removeBox("lafontai");
		
		assertSame(s, BoxStatus.Deleted);
	}
	
	@Test
	public void testAddBox() throws BoxException {	
		s = mbm.addBox("lafontai", BoxType.Mail);
		
		assertSame(s, BoxStatus.Created);
		
		s = mbm.addBox("public", BoxType.News);
		
		assertSame(s, BoxStatus.Created);
	}
	
	@Test(expected=box.BoxException.class)
	public void testAddBox2() throws BoxException {	
		s = mbm.addBox("lafontai", BoxType.Mail);
		s = mbm.addBox("lafontai", BoxType.News);
	}
	
	@Test
	public void testMessage() {
		Message m = new Message("lafontai", "garrone", "Coucou", "Au revoir");
	}
	
	@Test 
	public void testSendMsg() throws BoxException {
		mbm.addBox("lafontai", BoxType.Mail);
		Message m = new Message("lafontai", "garrone", "Coucou", "Au revoir");
	}
	
	@Test
	public void testGetBox() throws BoxException {
		BoxStatus bs = mbm.addBox("lafontai", BoxType.Mail);
		assertEquals(bs,BoxStatus.Created);
		
		MailBox b = new MailBox("lafontai");
		
		assertEquals(mbm.getBox("lafontai"),b);
	}


}
