import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;

public class Client {
    public static void main(String[] args) throws IOException {
        // check input arguments
         if (args.length != 2) {
            System.err.println("Usage: java Client <serverAddress> <portNumber>");
            System.exit(1);
        }
        String serverAddress = args[0];
        int port = Integer.parseInt(args[1]);
        // server address and port
  
        try {
            // create a socket to connect to the server
            Socket client = new Socket(serverAddress, port);

            // set up input and output streams for the socket
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);

            // set up an input stream to read user input
            BufferedReader readUserInput = new BufferedReader(new InputStreamReader(System.in));

            // client ID
            String clientId = "";

            boolean clientConnected = false;
            // display connection status
            System.out.println("Connected to server");

            // main loop to handle user input and server responses
            while (true) {
                // prompt user for input
                System.out.print("Enter message: ");
                String input = readUserInput.readLine();

                // handle user input
                if (input.trim().equals("DISCONNECT") && clientConnected) {
                    // send disconnect message with client ID
                    out.println(input + " " + "%%%" + clientId);
                } else if (input.split(" ")[0].equals("PUT") && clientConnected) {
                    // send put message with client ID
                    out.println(input + "%%%" + clientId);
                    // prompt user for additional input
                    System.out.print("Enter message: ");
                    input = readUserInput.readLine();
                    // send additional input
                    out.println(input);
                } else if (input.split(" ")[0].equals("GET") && clientConnected) {
                    // send get message with client ID
                    out.println(input + "%%%" + clientId);
                } else if (input.split(" ")[0].equals("DELETE") && clientConnected) {
                    // send delete message with client ID
                    out.println(input + "%%%" + clientId);
                } else if (input.split(" ")[0].equals("CONNECT") && !clientConnected) {
                    // extract client ID from input
                    String[] result = Arrays.stream(input.split(" "), 1, input.split(" ").length)
                            .toArray(String[]::new);
                    clientId = String.join(" ", result);
                    // send connect message with client ID
                    out.println(input);
                } else {

                    // disconnect client due to invalid requests
                    System.out.println("Closing connection due to Invalid message");
                    client.close();
                    readUserInput.close();
                    System.exit(0);
                }
                // read server messages
                String serverMessage = in.readLine();
                System.out.println("Server says: " + serverMessage.trim());

                // set connection to true if connection is okay
                if (serverMessage.trim().equals("CONNECT: OK")) {
                    clientConnected = true;
                }
                // close socket and stream and release any resources associated with it
                if (serverMessage.trim().equals("DISCONNECT: OK")
                        || serverMessage.trim().equals("CONNECT: ERROR")
                        || serverMessage.trim().equals("PUT: ERROR")
                        || serverMessage.trim().equals("ERROR: Invalid command")) {
                    client.close();
                    readUserInput.close();
                    System.exit(0);
                }
            }
        } catch (IOException e) {
            System.err.println("Error: Could not connect to server at " + serverAddress + ":" + port);
            System.exit(1);
        }
    }
}
