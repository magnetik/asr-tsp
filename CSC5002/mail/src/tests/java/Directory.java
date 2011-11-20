import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import box.NewsBox;


import directory.DirectoryManager;
import directory.user.UserRight;
import directory.user.UserStatus;

public class Directory {
	DirectoryManager dm;
	UserStatus u;
	NewsBox newsBox;

	@Before 
	public void init() {
		dm = new DirectoryManager(); 
		u = dm.addUser("lafontai");
		
		newsBox = new NewsBox("public");
	}
	
	@After
	public void tearDown(){
		dm = null;
	}
	
	@Test
	public void testAddUser() {		 
		assertSame(u, UserStatus.UserCreated);
	}
	
	@Test
	public void testRemoveUser() {
		u = dm.removeUser("lafontai");
		assertSame(u, UserStatus.UserDeleted);
	}

	@Test(expected=directory.user.UserException.class)
	public void testLookUpUser() throws directory.user.UserException {
		u = dm.lookUpUser("lafontai");
		u = dm.lookUpUser("garrone");
	}

	@Test
	public void testLookUpUserRight() {
		UserRightEnum r;
		try {
			r = dm.lookUpUserRight("lafontai","public");
			assertSame(r, UserRightEnum.None);
		} catch (directory.user.UserException e) {
			fail();
		}
		
	}

	@Test
	public void testUpdateUserRight() {
		UserRightEnum r;
		try {
			r = dm.lookUpUserRight("lafontai", "public");
			
			assertSame(r, UserRightEnum.None);
			
			r = dm.updateUserRight("lafontai", "public", UserRightEnum.Write);
			assertSame(r, UserRightEnum.Write);
		} catch (directory.user.UserException e) {
			fail();
		}
	}

}
