package org.jstepanovic.server.service;

import org.jstepanovic.server.repository.Repository;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.jstepanovic.commons.Constants.SERVER_ADDRESS;
import static org.jstepanovic.commons.Constants.SERVER_PORT;


public enum Server {
    INSTANCE;

    private final int numberOfProcessors = Runtime.getRuntime().availableProcessors();
    private final ExecutorService executorService = Executors.newFixedThreadPool(numberOfProcessors - 1);

    private final Repository repository = new Repository();

    private final AtomicBoolean isServerRunning = new AtomicBoolean(false);

    private ServerSocket serverSocket;

    public void run() {
        try {
            serverSocket = new ServerSocket(SERVER_PORT, 50, InetAddress.getByName(SERVER_ADDRESS));

            isServerRunning.set(true);
            System.out.println("Server is running");

            executorService.submit(new Session(serverSocket.accept(), repository));

            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("started communication with the new client");
                    executorService.submit(new Session(clientSocket, repository));
                } catch (SocketException e) {
                    stop();
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void stop() {

        if (!isServerRunning.get()) {
            System.out.println("Server is not running");
            return;
        }

        try {
            executorService.shutdown();
            isServerRunning.set(false);
            executorService.awaitTermination(200, TimeUnit.MILLISECONDS);
            System.out.println("Server is stopping....");
            serverSocket.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean isRunning() {
        return isServerRunning.get();
    }

}
