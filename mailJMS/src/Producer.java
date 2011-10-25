/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import java.util.Arrays;
import java.util.Date;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.util.IndentPrinter;

/**
 * A simple tool for publishing messages
 * 
 * @version $Revision: 1.2 $
 */
public class Producer {

    private Destination destination;
    private long sleepTime;
    private boolean verbose = true;
    private int messageSize = 255;
    private long timeToLive;
    private String user = ActiveMQConnection.DEFAULT_USER;
    private String password = ActiveMQConnection.DEFAULT_PASSWORD;
    private String url = ActiveMQConnection.DEFAULT_BROKER_URL;
    private String subject = "lafontai";
    private boolean topic;
    private boolean transacted;
    private boolean persistent;

    public static void main(String[] args) {
        Producer producerTool = new Producer();
        String[] unknown = CommandLineSupport.setOptions(producerTool, args);
        if (unknown.length > 0) {
            System.out.println("Unknown options: " + Arrays.toString(unknown));
            System.exit(-1);
        }
        producerTool.run();
    }

    public void run() {
        Connection connection = null;
        try {
            System.out.println("Connecting to URL: " + url);
            System.out.println("Publishing a Message with size " + messageSize + " to " + (topic ? "topic" : "queue") + ": " + subject);
            System.out.println("Using " + (persistent ? "persistent" : "non-persistent") + " messages");
            System.out.println("Sleeping between publish " + sleepTime + " ms");
            if (timeToLive != 0) {
                System.out.println("Messages time to live " + timeToLive + " ms");
            }

            // Create the connection.
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(user, password, url);
            connection = connectionFactory.createConnection();
            connection.start();

            // Create the session
            Session session = connection.createSession(transacted, Session.AUTO_ACKNOWLEDGE);
            
            destination = session.createTopic(subject);

            // Create the producer.
            MessageProducer producer = session.createProducer(destination);
            if (persistent) {
                producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            } else {
                producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            }
            if (timeToLive != 0) {
                producer.setTimeToLive(timeToLive);
            }

            // Start sending messages
            sendMsg(session, producer);

            System.out.println("Done.");

            // Use the ActiveMQConnection interface to dump the connection
            // stats.
            ActiveMQConnection c = (ActiveMQConnection)connection;
            c.getConnectionStats().dump(new IndentPrinter());

        } catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (Throwable ignore) {
            }
        }
    }

    protected void sendMsg(Session session, MessageProducer producer) throws Exception {

    	TextMessage message = session.createTextMessage(createMessageText());

    	if (verbose) {
    		String msg = message.getText();
    		if (msg.length() > 50) {
    			msg = msg.substring(0, 50) + "...";
    		}
    		System.out.println("Sending message: " + msg + " to " + subject);
    	}

    	producer.send(message);
    	if (transacted) {
    		session.commit();
    	}

    	Thread.sleep(sleepTime);

        

    }

    private String createMessageText() {
        StringBuffer buffer = new StringBuffer(messageSize);
        buffer.append("Message: sent at: " + new Date());
        if (buffer.length() > messageSize) {
            return buffer.substring(0, messageSize);
        }
        for (int i = buffer.length(); i < messageSize; i++) {
            buffer.append(' ');
        }
        return buffer.toString();
    }

    public void setPersistent(boolean durable) {
        this.persistent = durable;
    }

    public void setMessageSize(int messageSize) {
        this.messageSize = messageSize;
    }

    public void setPassword(String pwd) {
        this.password = pwd;
    }

    public void setSleepTime(long sleepTime) {
        this.sleepTime = sleepTime;
    }

    
    public void setTimeToLive(long timeToLive) {
        this.timeToLive = timeToLive;
    }

    public void setTopic(boolean topic) {
        this.topic = topic;
    }

    public void setQueue(boolean queue) {
        this.topic = !queue;
    }

    public void setTransacted(boolean transacted) {
        this.transacted = transacted;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
    
    public void setSubject(String arg) {
        this.subject = arg;
    }
}
