package commands;

import data.Edge;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class CommandSocketThread implements Runnable{
    private Command command;
    private int COMMAND_PORT;
    private BufferedReader input;
    private PrintWriter output;

    public CommandSocketThread(Command command, int COMMAND_PORT) {
        this.command = command;
        this.COMMAND_PORT = COMMAND_PORT;
    }

    @Override
    public void run() {
        try (Socket socket = new Socket("localhost", COMMAND_PORT)) {
            socket.setSoTimeout(30000);

            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);

            while (command.isRunning()) {
                String commandString = command.getCommand();
                if (commandString != null) {
                    output.println(commandString);

                    switch (commandString) {
                        case "login":
                            output.println(command.getUsername());
                            output.println(command.getPassword());

                            int loginResponse = Integer.parseInt(input.readLine());
                            command.setAuthenticateResponse(loginResponse);
                            break;

                        case "register":
                            output.println(command.getUsername());
                            output.println(command.getPassword());

                            int registerResponse = Integer.parseInt(input.readLine());
                            command.setAuthenticateResponse(registerResponse);
                            break;

                        case "add-station":
                            output.println(command.getStationName());
                            output.println(command.getX());
                            output.println(command.getY());
                            break;

                        case "update-station":
                            output.println(command.getStationName());
                            output.println(command.getX());
                            output.println(command.getY());
                            break;

                        case "add-route":
                            output.println(command.getRouteName());
                            output.println(command.getFrequency());

                            ArrayList<String> route = command.getRoute();
                            output.println(route.size());
                            for (String station : route)
                                output.println(station);
                            break;

                        case "delete-route":
                            output.println(command.getRouteName());
                            break;

                        case "find-route":
                            output.println(command.getFrom());
                            output.println(command.getTo());
                            output.println(command.getStartTime());

                            int edgeCount = Integer.parseInt(input.readLine());
                            ArrayList<Edge> edgesToDraw = new ArrayList<>();
                            for (int i = 0; i < edgeCount; ++i) {
                                String node1 = input.readLine();
                                String node2 = input.readLine();
                                edgesToDraw.add(new Edge(node1, node2));
                            }
                            command.setEdgesToDraw(edgesToDraw);

                            int commandCount = Integer.parseInt(input.readLine());
                            ArrayList<String> commands = new ArrayList<>();
                            for (int i = 0; i < commandCount; ++i)
                                commands.add(input.readLine());
                            command.setCommands(commands);

                            command.setFound(true);
                            break;

                        case "exit":
                            command.setRunning(false);
                            break;

                        default:
                            System.out.println("Invalid command");
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
