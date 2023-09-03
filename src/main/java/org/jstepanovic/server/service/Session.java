package org.jstepanovic.server.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jstepanovic.server.model.command.*;
import org.jstepanovic.server.repository.Repository;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;


public class Session implements Runnable {

    private final Socket socket;

    private final Repository repository;

    private Command command;

    public Session(Socket socketForClient, Repository repository) {
        this.socket = socketForClient;
        this.repository = repository;
    }


    @Override
    public void run() {
        try (
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream()))
        {
            while (true) {
                try {
                    String jsonRequest = input.readUTF(); // reading the message
                    JsonObject request = JsonParser.parseString(jsonRequest).getAsJsonObject();

                    String jsonResponse = executeCommand(request);

                    output.writeUTF(jsonResponse); // send response to the client

                    if (command instanceof ExitCommand) {
                        socket.close();
                        Server.INSTANCE.stop();
                        break;
                    }
                } catch (EOFException e) {
                    socket.close();
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String executeCommand(JsonObject request) {
        command = setCommand(request.get("type").getAsString());
        return command.execute(request);
    }

    private Command setCommand(String requestType) {
        return switch (requestType) {
            case Command.GET -> new GetCommand(repository);
            case Command.SET -> new SetCommand(repository);
            case Command.DELETE -> new DeleteCommand(repository);
            default -> new ExitCommand();
        };
    }
}

