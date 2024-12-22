package p;                                                                                             
                                                                                                       
import java.io.*;                                                                                      
import java.net.*;                                                                                     
import javax.swing.*;                                                                                  
                                                                                                       
public class MultiThreadedServer {                                                                     
                                                                                                       
    private ServerSocket server;                                                                       
    private JTextArea textArea;                                                                        
                                                                                                       
    public MultiThreadedServer(int port) {                                                             
        try {                                                                                          
            // Create the server socket on the specified port                                          
            server = new ServerSocket(port);                                                           
            System.out.println("Server started on port " + port);                                      
                                                                                                       
            // Set up a simple GUI to display client messages                                          
            JFrame frame = new JFrame("Server Console");                                               
            textArea = new JTextArea(20, 50);                                                          
            textArea.setEditable(false);                                                               
            JScrollPane scrollPane = new JScrollPane(textArea);                                        
            frame.add(scrollPane);                                                                     
            frame.pack();                                                                              
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);                                      
            frame.setVisible(true);                                                                    
        } catch (IOException e) {                                                                      
            System.out.println("Could not listen on port " + port);                                    
            System.exit(-1);                                                                           
        }                                                                                              
    }                                                                                                  
                                                                                                       
    public void listenSocket() {                                                                       
        while (true) {                                                                                 
            try {                                                                                      
                // Accept client connections                                                           
                Socket clientSocket = server.accept();                                                 
                System.out.println("Client connected: " + clientSocket.getInetAddress());              
                                                                                                       
                // Create a new ClientWorker for each connection and start its thread                  
                ClientWorker worker = new ClientWorker(clientSocket, textArea);                        
                Thread thread = new Thread(worker);                                                    
                thread.start();                                                                        
            } catch (IOException e) {                                                                  
                System.out.println("Accept failed.");                                                  
                System.exit(-1);                                                                       
            }                                                                                          
        }                                                                                              
    }                                                                                                  
                                                                                                       
    protected void finalize() {                                                                        
        try {                                                                                          
            server.close();                                                                            
        } catch (IOException e) {                                                                      
            System.out.println("Could not close socket");                                              
            System.exit(-1);                                                                           
        }                                                                                              
    }                                                                                                  
                                                                                                       
    public static void main(String[] args) {                                                           
        int port = 4444; // Default port                                                               
        MultiThreadedServer server = new MultiThreadedServer(port);                                    
        server.listenSocket();                                                                         
    }                                                                                                  
}                                                                                                      
                                                                                                       
class ClientWorker implements Runnable {                                                               
    private Socket client;                                                                             
    private JTextArea textArea;                                                                        
                                                                                                       
    public ClientWorker(Socket client, JTextArea textArea) {                                           
        this.client = client;                                                                          
        this.textArea = textArea;                                                                      
    }                                                                                                  
                                                                                                       
    @Override                                                                                          
    public void run() {                                                                                
        try (                                                                                          
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));    
            PrintWriter out = new PrintWriter(client.getOutputStream(), true)                          
        ) {                                                                                            
            String line;                                                                               
            while ((line = in.readLine()) != null) {                                                   
                // Echo the message back to the client                                                 
                out.println("Server: " + line);                                                        
                                                                                                       
                // Append the received message to the server text area                                 
                appendText("Client: " + line + "\n");                                                  
            }                                                                                          
        } catch (IOException e) {                                                                      
            System.out.println("Error handling client connection");                                    
        } finally {                                                                                    
            try {                                                                                      
                client.close();                                                                        
            } catch (IOException e) {                                                                  
                System.out.println("Error closing client socket");                                     
            }                                                                                          
        }                                                                                              
    }                                                                                                  
                                                                                                       
    private synchronized void appendText(String line) {                                                
        textArea.append(line);                                                                         
    }                                                                                                  
}                                                                                                      
                                                                                                       
