package org.palladiosimulator.addon.slingshot.debuggereventsystems.socket;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import org.palladiosimulator.addon.slingshot.debuggereventsystems.EventDebugSystem;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.socket.messages.CloseSystem;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.socket.messages.Message;

/**
 * Manages the client-side socket communication for the Event Debug System.
 * <p>
 * This class is responsible for handling socket-based communication with the
 * Event Debug Server. It maintains a connection to the server, sends messages
 * to the server, and processes incoming messages from the server. Listeners can
 * be registered to handle specific types of messages received from the server.
 * The class also provides methods to start and stop the message sending and
 * receiving threads, ensuring proper cleanup of resources.
 * </p>
 */
public class EventDebugClient {

	private static EventDebugClient instance;
	private static final List<Consumer<Message>> messageReceivedListeners = new LinkedList<>();
	private final ConcurrentLinkedQueue<Message> messageQueue = new ConcurrentLinkedQueue<>();
	private final ExecutorService listenerNotificationService = Executors.newCachedThreadPool();

	private Socket socket;

	private Thread senderThread;
	private Thread readerThread;

	private boolean gotClosed = false;

	public EventDebugClient() {
		try {
			socket = new Socket("localhost", EventDebugSystem.getSettings().getPort());
//			output = new ObjectOutputStream(socket.getOutputStream());
//			output.flush();
			startMessageSender();
			startMessageReceiver();
		} catch (final IOException e) {
			throw new RuntimeException(
					"Could not connect to server. Ensure that the debugger server has been started before you send/receive messages.",
					e);
		}
	}

	/**
	 * Gets the singleton instance of the EventDebugClient.
	 * <p>
	 * Initializes the client connection to the server using the port specified in
	 * the EventDebugSystem settings.
	 * </p>
	 *
	 * @return The singleton instance of the EventDebugClient.
	 */
	public static EventDebugClient getInstance() {
		if (instance == null) {
			instance = new EventDebugClient();
		}
		return instance;
	}

	/**
	 * Adds a listener to handle messages received from the server.
	 * <p>
	 * Each listener is invoked asynchronously when a message is received from the
	 * server.
	 * </p>
	 *
	 * @param messageListener The listener to add.
	 */
	public static void addListener(final Consumer<Message> messageListener) {
		messageReceivedListeners.add(messageListener);
	}

	private void notifyListeners(final Message message) {
		messageReceivedListeners
				.forEach(consumer -> listenerNotificationService.submit(() -> consumer.accept(message)));
	}

	/**
	 * Sends a message to the server.
	 * <p>
	 * Queues a message to be sent to the server. Messages are sent asynchronously
	 * by the message sender thread.
	 * </p>
	 *
	 * @param message The message to send.
	 */
	public static void sendMessage(final Message message) {
		getInstance().messageQueue.add(message);
	}

	/**
	 * Starts the background thread responsible for sending messages to the server.
	 * <p>
	 * Initializes and starts a thread that continuously checks for queued messages
	 * and sends them to the server over the established socket connection.
	 * </p>
	 */
	public void startMessageSender() {
		senderThread = new Thread(new SenderRunnable());
		senderThread.start();
	}

	/**
	 * Starts the background thread responsible for receiving messages from the
	 * server.
	 * <p>
	 * Initializes and starts a thread that continuously listens for incoming
	 * messages from the server and processes them by notifying registered
	 * listeners.
	 * </p>
	 */
	public void startMessageReceiver() {
		readerThread = new Thread(new ReceiverRunnable());
		readerThread.start();
	}

	/**
	 * Stops the client, closing the socket connection and cleaning up resources.
	 * <p>
	 * Ensures that all queued messages are discarded, the socket connection is
	 * closed, and the singleton instance is reset.
	 * </p>
	 */
	public void stop() {
		try {
			while (!messageQueue.isEmpty()) {
				messageQueue.clear();
			}
			if (socket != null && !socket.isClosed()) {
				socket.close();
				socket = null;
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
		gotClosed = true;
		instance = null;

	}

	/**
	 * Handles the task of sending messages to the server in a separate thread.
	 * <p>
	 * This {@code Runnable} is responsible for serializing and sending messages
	 * from the client to the server. It continuously checks for messages in a queue
	 * and sends them as they become available. If the socket is closed or an error
	 * occurs, it stops sending messages and closes the client.
	 * </p>
	 */
	private class SenderRunnable implements Runnable {

		@Override
		public void run() {
			try {
				sendMessages();
			} catch (final SocketException e) {
				System.out.println("Client closed the socket before.");
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}

		/**
		 * Sends messages from the queue to the server.
		 * <p>
		 * Dequeues messages and sends them to the server using an
		 * {@code ObjectOutputStream}. Ensures messages are properly flushed to the
		 * output stream. If the socket is closed, it stops sending messages.
		 * </p>
		 *
		 * @throws IOException If an I/O error occurs while sending the message.
		 */
		private void sendMessages() throws IOException {
			try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
				while (!socket.isClosed()) {
					if (!messageQueue.isEmpty()) {
						final Object message = messageQueue.poll();
						out.writeObject(message);
						out.flush();
						System.out.println("Message sent: " + message);
					}
				}
			} finally {
				stop();
			}
		}

	}

	/**
	 * Handles receiving messages from the server in a separate thread.
	 * <p>
	 * This {@code Runnable} is responsible for deserializing and processing
	 * messages received from the server. It listens for incoming messages and
	 * notifies registered listeners of new messages. If a {@code EOFException} is
	 * caught, it indicates that the client closed the connection. The runnable
	 * stops receiving messages if the client is closed or an error occurs.
	 * </p>
	 */
	private class ReceiverRunnable implements Runnable {

		@Override
		public void run() {
			try {
				receiveMessages();
			} catch (final EOFException e) {
				System.out.println("Client closed the connection.");
			} catch (final IOException e) {
				System.out.println("Could not receive messages anymore.");
				e.printStackTrace();
			}
		}

		/**
		 * Receives and processes messages from the server.
		 * <p>
		 * Listens for messages sent by the server and deserializes them using an
		 * {@code ObjectInputStream}. Notifies registered listeners of the received
		 * messages. Handles special messages such as {@code CloseSystem} by stopping
		 * the client.
		 * </p>
		 *
		 * @throws IOException If an I/O error occurs while receiving messages.
		 */
		private void receiveMessages() throws IOException {
			try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
				while (!gotClosed && !socket.isClosed()) {
					final Object message = readMessage(in);
					if (message != null && message instanceof final Message am) {
						System.out.println("Message read: " + am);

						if (am instanceof CloseSystem) {
							System.out.println("Close client");
							break;
						} else {
							notifyListeners(am);
						}
					}
				}
			} finally {
				stop();
			}
		}

		/**
		 * Reads a single message from the input stream.
		 * <p>
		 * Attempts to read and deserialize an object from the input stream. Catches
		 * {@code ClassNotFoundException} if the class of a serialized object cannot be
		 * found.
		 * </p>
		 *
		 * @param in The input stream from which to read the message.
		 * @return The deserialized message object, or {@code null} if an error occurs
		 *         during deserialization.
		 * @throws IOException If an I/O error occurs while reading the message.
		 */
		private Object readMessage(final ObjectInputStream in) throws IOException {
			try {
				return in.readObject();
			} catch (final ClassNotFoundException e) {
				System.out.println(
						"The message that has been tried to read does not have a class on this system to reconstruct. ");
				e.printStackTrace();
				return null;
			}
		}
	}
}
