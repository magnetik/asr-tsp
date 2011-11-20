package box;

import java.util.ArrayList;

import java.util.Collection;
import java.util.List;

import box.message.Message;
import box.message.MessageException;
import box.message.MessageOperations;
import box.message.MessageStatus;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;

import jaxb.JaxbList;


import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import directory.UserException;
import directory.UserRightEnum;

/**
 * Main MailBox class
 * Need to be instanciated with Jetty
 * See pom.xml
 * @author Baptiste Lafontaine
 * @author Julie Garonne
 *
 */
@Path("/mbm")
public class MailBoxManager {
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
	 * Used to manua flush JPA cache
	 */
	static void flushCache() {
		entityManager.getTransaction().begin();
		entityManager.flush();
		entityManager.getTransaction().commit();
	}

	public List<Message> readAUserNewMessages(String username) throws UserException {
		return null;
	}
	
	@GET
	@Path("box/{boxname}")
	public Collection<Message> readABoxAllMessages(@PathParam("boxname") String boxName) {
		Box b;
		try {
			b = getBox(boxName);
		} catch (BoxException e) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		Query query = entityManager.createQuery("SELECT m FROM Message m WHERE m.receiverName = :name").setParameter("name", b);
		@SuppressWarnings("unchecked")
		List<Message> list = query.getResultList();
		
		System.out.println(list.size());
		
		//System.out.println("Get messages for box : " + boxName + " (" + userBox.getMsgList().size() + " messages)");
		return list;
	}
	
	/**
	 * Mark a message a red
	 * @param num identifier of the message
	 * @return
	 */
	@PUT
	@Path("read/{idToRead}")
	public String markAMessageAsRead(@PathParam("idToRead") int num) {
		Message m;
		try {
			m = getMessage(num);
		} catch (MessageException e) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		m.setRead();
		
		entityManager.persist(m);
		flushCache();
		
		throw new WebApplicationException(Status.OK);
	}
	
	public List<Message> readAUserAllMessages(String username) throws UserException, BoxException {
		// TODO Auto-generated method stub
		return null;
	}

	@DELETE
	@Path("{identifier}")
	public void deleteAMessage(@PathParam("identifier") int num)  {
		Message m;
		try {
			m = getMessage(num);
		} catch (MessageException e) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		removeMessage(m);
		
		throw new WebApplicationException(Status.GONE);
	}
	
	/**
	 * Return a Message given by identifier
	 * @param num
	 * @return
	 * @throws MessageException
	 */
	public Message getMessage(int num) throws MessageException {
		System.out.println("Lookinf for message :" + num);
		Message m = entityManager.find(Message.class, num);
		if (m != null) {
			return m;
		}
		else{
			throw new MessageException("Ce message n'existe pas");
		}
	}
	
	/**
	 * Physicaly remove a message from the database
	 * @param m
	 * @return
	 */
	public MessageStatus removeMessage(Message m) {
		entityManager.remove(m);
		flushCache();
		return MessageStatus.DoNotExists;
	}

	public Integer deleteAUserReadMessages(String username) throws UserException, MessageException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Send a message to a specifix  box
	 * Check permissions first
	 * @param receiverName
	 * @param senderName
	 * @param subject
	 * @param body
	 */
	@POST
	@Consumes("application/x-www-form-urlencoded")
	@Path("box")
	public void sendAMessageToABox(	@FormParam("receiverName") String receiverName, // destination 
									@FormParam("senderName") String senderName,    // source
									@FormParam("subject") String subject,     
									@FormParam("body") String body) {
		
		Box receiverBox;
		try {
			receiverBox = getBox(receiverName);
		} catch (BoxException e1) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		
		if (receiverBox.getType() == BoxType.News) {
			// check rights for NewsBox
			Client client = Client.create();
			WebResource webResource = client.resource("http://localhost:8088/jersey-demo/directory/user/" + senderName + "/" + receiverName + "/rights");
			MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
			formData.add("username", senderName);
			formData.add("newsBox", receiverName);
			
			String r = webResource.get(String.class);
			UserRightEnum right = UserRightEnum.fromStringUserRightToUserRight(r);
			System.out.println("User rights : " + right);
			
			if (right == UserRightEnum.Write) {
				try {
					sendAMessageTo(receiverName, senderName, subject, body);
				} catch (Exception e) {
					throw new WebApplicationException(Status.NOT_FOUND); // Throw 404 error
				} 
				throw new WebApplicationException(Status.CREATED);
			}	
			else {
				System.out.println("L'utilisateur : " + senderName + " n'a pas les droits sur : " + receiverName);
				throw new WebApplicationException(Status.UNAUTHORIZED);
			}
		}
		else {
			try {
				sendAMessageTo(receiverName, senderName, subject, body);
			} catch (Exception e) {
				throw new WebApplicationException(Status.NOT_FOUND); // Throw 404 error
			} 
			throw new WebApplicationException(Status.CREATED);
		}
		
		
	}
	
	/**
	 * Send a message to the given receiverName
	 * @param receiverName Destination of the message
	 * @param senderName origin of the message
	 * @param subject Subject of the message
	 * @param body Body of the message
	 * @return 
	 * @throws MessageException
	 * @throws BoxException
	 */
	public MessageOperations sendAMessageTo(	String receiverName, 
												String senderName, 
												String subject, 
												String body) throws MessageException, BoxException {
		Box b = getBox(receiverName);
		
		Message m = new Message(senderName, b, subject, body);
		
		entityManager.persist(m);
		flushCache();
		
		return MessageOperations.Sent;

	}
	
	/**
	 * Get a Box by name
	 * @param name of the box
	 * @return
	 * @throws BoxException if the box does not exist
	 */
	public Box getBox(String name) throws BoxException {
		Box b = entityManager.find(Box.class, name);
		if (b != null) {
			return b;
		}
		else{
			throw new BoxException("Cette boite n'existe pas");
		}
	}

	/**
	 * Create a box
	 * @param name Name of the box
	 * @param type Type (as String) of the box
	 */
	@POST
	//@Consumes("application/x-www-form-urlencoded")
	@Path("addbox")
	public void addBox(	@FormParam("name") String name, 
						@FormParam("type") String type) {
		BoxType bt;
		if (type.equals("Mail")) {
			bt = BoxType.Mail;
		}
		else if (type.equals("News")) {
			bt = BoxType.News;
		}
		else {
			throw new WebApplicationException(Status.BAD_REQUEST);
		}
		try {
			addBox(name, bt);
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebApplicationException(Status.BAD_REQUEST);
		}
		throw new WebApplicationException(Status.CREATED);
	}
	
	/**
	 * Add a box to the database
	 * @param name
	 * @param type
	 * @return
	 * @throws BoxException
	 */
	public BoxStatus addBox(String name, BoxType type) throws BoxException {
		Box b = entityManager.find(Box.class, name);
		if (b != null) {
			throw new BoxException("Cette boite existe déjà");
		}
		else {
			b = new Box(name,type);
			
			entityManager.persist(b);
			flushCache();				// Auto flush does not work...
			
			System.out.println("Box created: " + name + " (of type:" + type + ")");
			return BoxStatus.Created;
		}
	}

	/**
	 * Delete a box
	 * @param name name of the box to delete
	 */
	@DELETE
	@Path("box/{boxname}")
	public void rmBox(@PathParam("boxname") String name) {
		try {
			removeBox(name);
		} catch (BoxException e) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		throw new WebApplicationException(Status.GONE);
	}
	
	/**
	 * Remove a box from the database
	 * @param name name of the box to remove
	 * @return BoxStatus.Deleted on success
	 * @throws BoxException if the box does not exists
	 */
	public BoxStatus removeBox(String name) throws BoxException {
		Box b = entityManager.find(Box.class, name);
		if (b != null) {
			
			entityManager.remove(b); // remove from the database
			flushCache();	
			
			System.out.println("Box deleted: " + name);
			return BoxStatus.Deleted;
		}
		else {
			throw new BoxException("Cette boite n'existe pas");
		}
	}
	
	/**
	 * Get all news boxes from the database
	 * @return List of news boxes
	 */
	@SuppressWarnings("unchecked")
	public List<Box> getNewsBoxes() {
		Query query = entityManager.createQuery("SELECT b FROM Box b WHERE b.type = :type").setParameter("type", BoxType.News);
		return query.getResultList();
	}
	
	/**
	 * Return the complete list of news box
	 * @return
	 */
	@GET
	@Path("listNewsBox")
	public JaxbList<String> listNewsBox(){
		List<String> list = new ArrayList<String>();
		
		for (Box b: getNewsBoxes()) {
			list.add(b.getName());
		}
		JaxbList<String> result = new JaxbList<String>(list);
		return result;
		
	}
}