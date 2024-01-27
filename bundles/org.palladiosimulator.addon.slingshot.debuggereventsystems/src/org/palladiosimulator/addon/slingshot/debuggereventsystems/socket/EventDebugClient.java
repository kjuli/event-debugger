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


	public static EventDebugClient getInstance() {
		if (instance == null) {
			instance = new EventDebugClient();
		}
		return instance;
	}

	public static void addListener(final Consumer<Message> messageListener) {
		messageReceivedListeners.add(messageListener);
	}

	private void notifyListeners(final Message message) {
		messageReceivedListeners
				.forEach(consumer -> listenerNotificationService.submit(() -> consumer.accept(message)));
	}

	public void startServer() {
		EventDebugServer.start();
	}

	public static void sendMessage(final Message message) {
		getInstance().messageQueue.add(message);
	}

	public void startMessageSender() {
		senderThread = new Thread(new SenderRunnable());
		senderThread.start();
	}

	public void startMessageReceiver() {
		readerThread = new Thread(new ReceiverRunnable());
		readerThread.start();
	}

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
