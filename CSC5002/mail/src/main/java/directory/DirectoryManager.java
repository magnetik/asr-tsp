package directory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;

import jaxb.JaxbList;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;


/**
 * Main Directory class
 * Need to be instanciated with Jetty
 * See pom.xml
 * @author Baptiste Lafontaine
 * @author Julie Garonne
 *
 */
@Path("/directory")
public class DirectoryManager { 
	// Setting up persistence manager
	static EntityManagerFactory factory;
	static EntityManager entityManager;
	static {
		factory = Persistence.createEntityManagerFactory("MessagePU");
		entityManager = factory.createEntityManager();
		
		// Set Auto Flush
		entityManager.setFlushMode(FlushModeType.AUTO);
	}
	/**
	 * Used to manual flush JPA cache
	 */
	static void flushCache() {
		entityManager.getTransaction().begin();
		entityManager.flush();
		entityManager.getTransaction().commit();
	}

	@GET
	@Path("/add/{username}")
	public String createUser(@PathParam("username") String username) {
		
		if (addUser(username) != UserStatus.UserExists) {
		
			// Cr�ation de la box de l'utilisateur
			Client client = Client.create();
			WebResource webResource = client.resource("http://localhost:8088/jersey-demo/mbm/addbox");
			MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
			formData.add("name", username);
			formData.add("type", "Mail");
		
			ClientResponse response = webResource.type("application/x-www-form-urlencoded").post(ClientResponse.class, formData);
			int status = response.getStatus();
	
			if (status == 201) {
				System.out.println("User+Box created: " + username );
				throw new WebApplicationException(Status.CREATED);
			}
			else {
				System.out.println("Probl�me!!" );
				throw new WebApplicationException(Status.BAD_REQUEST);
			}
		}
		else {
			System.out.println("Pb!!");
			throw new WebApplicationException(Status.BAD_REQUEST);
		}
	}
	
	/**
	 * 
	 * @param username
	 * @return
	 */
	
	public UserStatus addUser(String username) {
		try {
			getUser(username);
		} catch (UserException e) {
			User user = new User(username);

			entityManager.persist(user);
			flushCache();
			
			return UserStatus.UserCreated;

		}
		return UserStatus.UserExists;
	}

	@DELETE
	@Path("/delete/{username}")
	public void deleteUser(@PathParam("username") String username) {
		if (removeUser(username) == UserStatus.UserDeleted) {
			
			// Supression de la box de l'utilisateur
			Client client = Client.create();
			WebResource webResource = client.resource("http://localhost:8088/jersey-demo/mbm/box/");
		
			ClientResponse response = webResource.path(username).delete(ClientResponse.class);
			int status = response.getStatus();
	
			if (status == 410) {
				System.out.println("User+Box deleted: " + username );
				throw new WebApplicationException(Status.CREATED);
			}
			else {
				System.out.println("Probl�me!!" );
				throw new WebApplicationException(Status.BAD_REQUEST);
			}
		}
		else {
			throw new WebApplicationException(Status.BAD_REQUEST);
		}
	}
	
	/**
	 * Delete a user from the database
	 * @param username
	 * @return
	 */
	public UserStatus removeUser(String username){
		User user;
		try {
			user = getUser(username);
			
			entityManager.remove(user);
			flushCache();
			
			return UserStatus.UserDeleted;
			
		} catch (UserException e) {
			return UserStatus.UserUnknown;
		}
	}
	
	/**
	 * List all newsboxes a user can read
	 * @param username
	 * @return A list of newsbox name
	 * @throws UserException if the user does not exists
	 */
	@GET
	@Path("user/{username}/readable")
	public JaxbList<String> lookUpReadableNewsBox(@PathParam("username") String username) {
		List<String> result;
		try {
			result = lookUpUserNewsBox(username, UserRightEnum.Read);
		} catch (UserException e) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		
		return new JaxbList<String>(result);
	}
	
	/**
	 * List all newsboxes a user can write on
	 * @param username
	 * @return A list of newsbox name
	 * @throws UserException if the user does not exists
	 */
	@GET
	@Path("user/{username}/writable")
	public JaxbList<String> lookUpWriteNewsBox(@PathParam("username") String username) {
		List<String> result;
		try {
			result = lookUpUserNewsBox(username, UserRightEnum.Write);
		} catch (UserException e) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		
		return new JaxbList<String>(result);
	}
	
	
	/**
	 * Return all news box have AT LEAST the given right
	 * @param username
	 * @param minRight The minimum right the user must have
	 * @throws UserException if the user does not exists
	 * @return
	 */
	public List<String> lookUpUserNewsBox(String username, UserRightEnum minRight) throws UserException {
		User user = getUser(username);
		
		Query query = entityManager.createQuery("SELECT u.boxName FROM UserRight u WHERE u.user = :user AND u.right >= :right").setParameter("user", user).setParameter("right", minRight);
		@SuppressWarnings("unchecked")
		List<String> result = query.getResultList();
		
		return result;
	}
	
	@GET
	@Path("user/{username}/{newsBox}/rights")
	public String lookUpUserRight(		@PathParam("username") String username, 
										@PathParam("newsBox") String newsBox) {
		User user;
		try {
			user = getUser(username);
		} catch (UserException e) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		
		Query query = entityManager.createQuery("SELECT u.right FROM UserRight u WHERE u.user = :user AND u.boxName = :boxname").setParameter("user", user).setParameter("boxname", newsBox);
		@SuppressWarnings("unchecked")
		List<UserRightEnum> result = query.getResultList();
		
		return result.get(0).toString();
	}
	
	
	/**
	 * Return a user given by name
	 * @param username
	 * @return
	 * @throws UserException
	 */
	public User getUser(String username) throws UserException {
		User u = entityManager.find(User.class, username);
		if (u != null) {
			return u;
		}
		else{
			throw new UserException("Cet utilisateur n'existe pas");
		}
	}
	
	@PUT
	@Path("updateUser/{user}/{box}/{right}")
	public void updateUserRight(	@PathParam ("user") String username, 
									@PathParam ("box")  String newsBox,
									@PathParam ("right") String right) {
		UserRightEnum r = UserRightEnum.fromStringUserRightToUserRight(right);
		try {
			updateUserRight(username, newsBox, r);
			System.out.println("Update right for user : " + username + " for newsBox : " + newsBox + " new right : " + right);

			throw new WebApplicationException(Status.OK);
		} catch (UserException e) {
			throw new WebApplicationException(Status.BAD_REQUEST);
		}

	}
	
	/**
	 * Update the given user to the given right. 
	 * @param username
	 * @param newsBox
	 * @param right
	 * @return
	 * @throws UserException
	 */
	@SuppressWarnings("unchecked")
	public UserRightEnum updateUserRight(String username, String newsBox, UserRightEnum right) throws UserException {
		List<UserRight> result;
		User user;
		UserRight r;
		
		user = getUser(username);
		
		// Check if the user already have a right for this box 
		Query query = entityManager.createQuery("SELECT u FROM UserRight u WHERE u.user = :user AND u.boxName = :boxname").setParameter("user", user).setParameter("boxname", newsBox);
		result = query.getResultList();
		
		if (result.size() == 0) {
			
			r = new UserRight(user, newsBox, right);
		}
		else {
			r = result.get(0);
			r.right = right;
			
			entityManager.persist(r);
			flushCache();
		}

		System.out.print("Right from : " + username + " for : " + newsBox + " updated as : " + right);
		
		entityManager.persist(r);
		flushCache();
		
		return r.getRight();
	}
	
	/**
	 * List all users
	 * @return
	 */
	@GET
	@Path("listUsers")
	public JaxbList<String> listUser(){
		Query query = entityManager.createQuery("SELECT u.username FROM User u");
		@SuppressWarnings("unchecked")
		List<String> userList = query.getResultList();
		
		JaxbList<String> result = new JaxbList<String>(userList);
		return result;
		
	}


}