package org.palladiosimulator.addon.slingshot.debuggereventsystems.socket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.palladiosimulator.addon.slingshot.debuggereventsystems.EventDebugSystem;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.socket.messages.CloseSystem;

public class EventDebugServer implements Runnable {

	private static EventDebugServer instance;

	private ServerSocket serverSocket;
	private int port;

	private ExecutorService clientExecutor;
	private final Map<Socket, ClientHandler> clientSockets = new ConcurrentHashMap<>();

	@Override
	public void run() {
		port = EventDebugSystem.getSettings().getPort();
		try {
			serverSocket = new ServerSocket(port);
			clientExecutor = Executors.newCachedThreadPool();
			System.out.println("EventDebugServer Started on Port " + port);
			while (!serverSocket.isClosed()) {
				try {
					final Socket socket = serverSocket.accept();
					final ClientHandler c = new ClientHandler(socket);
					clientSockets.put(socket, c);
					clientExecutor.submit(c);
				} catch (final SocketException e) {
					if (serverSocket == null || serverSocket.isClosed()) {
						// Gracefully shut-down the socket.
						break;
					} else {
						throw e;
					}
				}
			}
			
		} catch (final BindException e) {
			System.out.println("The socket " + port
					+ " is already in use, indicating that there might be an EventDebugger already running.");
			System.out.println(e.getMessage());
		} catch (final IOException e) {
			throw new RuntimeException("Error happened while oppening the server", e);
		} finally {
			closeServer();
		}
	}

	public static EventDebugServer getInstance() {
		if (instance == null) {
			instance = new EventDebugServer();
		}

		return instance;
	}

	public static void start() {
		new Thread(getInstance()).start();
	}

	public static void closeServer() {
		if (getInstance().serverSocket != null && !getInstance().serverSocket.isClosed()) {
			EventDebugClient.sendMessage(new CloseSystem());
			try {
				Thread.sleep(500);
			} catch (final InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			for (final Socket socket : getInstance().clientSockets.keySet()) {
				if (!socket.isClosed()) {
					socket.close();
				}
			}
			getInstance().clientSockets.clear();
			
			if (getInstance().serverSocket != null && !getInstance().serverSocket.isClosed()) {
				getInstance().serverSocket.close();
			}
			getInstance().serverSocket = null;
			
			getInstance().shutdownClients();
		} catch (final IOException e) {
			e.printStackTrace();
		} finally {
			instance = null;
		}
	}

	private void shutdownClients() {
		if (clientExecutor == null || clientExecutor.isShutdown()) {
			return;
		}

		clientExecutor.shutdown();
		try {
			if (!clientExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
				clientExecutor.shutdownNow();
			}
		} catch (final InterruptedException e) {
			clientExecutor.shutdownNow();
		}

		clientExecutor = null;
	}

	private class ClientHandler implements Runnable {

		private final Socket socket;
		private ObjectOutputStream out;

		public ClientHandler(final Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
					ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
				// out = new ObjectOutputStream(socket.getOutputStream());
				this.out = out;
				out.flush();

				Object message;
				while (!socket.isClosed() && (message = in.readObject()) != null) {
					// if (message != null) {
					broadcastMessage(message);
				}
			} catch (final IOException | ClassNotFoundException e) {
				throw new RuntimeException("Error while reading message.", e);
			}
		}

		private void broadcastMessage(final Object message) {
			clientSockets.entrySet().stream().filter(s -> !s.getKey().equals(socket)).forEach(entry -> {
				try {
					entry.getValue().out.writeObject(message);
					entry.getValue().out.flush();
				} catch (final IOException e) {
					throw new RuntimeException("Error sending message: ", e);
				}
			});
		}
	}

}
