package com.skellybuilds.SCMC.utils;
import com.skellybuilds.SCMC.StringAr;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;


public class Network {
    int port;
    ServerThread server;

    public Network(int port) {
        this.port = port;
    }

    public void init(ServerCommands cmds){
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            LoggerFactory.getLogger("SCMC [Server Client Mods Checker]").info("Server is listening on port {}", port);
           // System.out.println();
            while (true) {
                Socket socket = serverSocket.accept();
                this.server = new ServerThread(socket, cmds);
                server.start();
            }
        } catch (Exception e) {
            LoggerFactory.getLogger("SCMC [Server Client Mods Checker]").error("Unable to start the server? Is the port free & available?");
            throw new RuntimeException("Unable to start SCMC communications server!");
        }
    }

   public static class ServerCommands {
        private final List<Command> Commands = new ArrayList<>();

        public void addCommand(String name, BiConsumer<String[], Socket> callback){
            Commands.add(new Command(name, callback));
        }

        public void removeCommand(String name){
            //else continue;
            Commands.removeIf(cmd -> Objects.equals(cmd.name, name));
        }

        public void executeCommand(String name, String[] args, Socket out) {
            for (Command cmd : Commands) {
                if (Objects.equals(cmd.name, name)) {
                    cmd.execute(args, out);
                }
            }
        }


        public boolean doesExist(String name){
            for (Command cmd : Commands) {
                if(Objects.equals(cmd.name, name)){
                    return true;
                } //else continue;
            }
            return false;
        }

        private static class Command {
            public String name;
            private final BiConsumer<String[], Socket> callback;


            public Command(String name, BiConsumer<String[], Socket> callback){
                this.name = name;
                this.callback = callback;
            }

            public void execute(String[] args, Socket out) {
                callback.accept(args, out);

            }


        }
    }

    static class ServerThread extends Thread {
        private final Socket socket;
        private final ServerCommands cmds;



        public ServerThread(Socket socket, ServerCommands cmds) {
            this.socket = socket;
            this.cmds = cmds;
            //cmds.addCommand("test", ServerThread::test);
        }


        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                String data = in.readLine();

                if(data == null){
                    out.println(0);
                    return;
                }

                String[] parts = data.split("\\|", 999);

                if(parts.length < 1){
                    out.println(520);
                }

                String[] arguments = {};
                if(parts.length > 1) {
                    //String argumentsString = parts[0].;
                    arguments = StringAr.removeElement(parts, parts[0]);
                }

                parts[0] = parts[0].replace(" ", "");

                if(cmds.doesExist(parts[0])){
                    cmds.executeCommand(parts[0], arguments, socket);
                } else {
                out.println(428);
                }

            } catch (Exception e) {
                LoggerFactory.getLogger("SCMC [Server Client Mods Checker]").error("Couldn't read the buffers from socket!");
                LoggerFactory.getLogger("SCMC [Server Client Mods Checker]").error(e.toString());
                //throw new RuntimeException("Unable to read socket buffers!");
            }
        }

    }
}