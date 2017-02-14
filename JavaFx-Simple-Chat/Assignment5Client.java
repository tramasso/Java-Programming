/*
 * Chat using Client and Server
 * Interface using Javafx
 * Lucas de Morais Tramasso
 * 
 */


package assignment5;

/* io and network imports */
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Consumer;

/* Javafx imports*/
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/* Class Client that contains networking and GUI classes */
public class Assignment5Client extends Application {
	
	private TextArea chatMessages = new TextArea(); //Area to display the messages
	private String ip = "127.0.0.1"; //IP to connect, default to local 
	private int port = 33333; //port number
	
	/* Creates a new connection */
	private Connection connection = new Connection(ip,port, data -> {
		Platform.runLater(() -> {  //this will give the control back to the main thread because its the thread that can edit the gui
			chatMessages.appendText(data.toString() + "\n"); //append received text when consumer accept method is called.
		});
		
	});
	
	/* method to get input send the message and display the sent message on the client window */
	private Parent createContent(){
		chatMessages.setPrefHeight(410); //setting height of text area for messages 
		chatMessages.setEditable(false); //message area is not editable
		
		TextField input = new TextField(); //create the input text field
		input.setPrefHeight(50); //set the input text field height
		input.setPromptText("Type your message"); //set the prompt text to the input field
		
		input.setOnAction(event -> {       //creates an input action
			String message = "Client: ";   // add client tag to the message
			message += input.getText();    //get the text from the input field
			input.clear();				   //clear the input field
			
			chatMessages.appendText(message + "\n");  //show the message on the client text area
			
			try {
				connection.send(message);  //send the message
			} catch (Exception e) {
				
				chatMessages.appendText("ERROR: Could not deliver message" + "\n"); //error in connection
				
			}
		});
		
		
		VBox v = new VBox(30,chatMessages,input);  //vbox for the text area and input area
		v.setPrefSize(500, 500);  //size of the vbox
		v.setStyle("-fx-background: BLACK;");
		return v; //return the vbox with all info
	}
	
	/* init function of the application */
	public void init() throws Exception {
		connection.start(); //calls the method start from connection object
	} 
	
	/* method start will start the application */
	@Override
	public void start(Stage mainStage) throws Exception {
		mainStage.setScene(new Scene(createContent())); //set the scene calling createContent()
		mainStage.setTitle("Chat Client"); //set the window title
		mainStage.setX(700.0);
		mainStage.show();  //display the contents

	}
	
	/* stop function of the application */
	public void stop () throws Exception {
		connection.close(); //closes the connection with the server
	}
	
	public static void main(String[] args) {
		launch(args); //function to launch the application

	}
	
	/* Connection class is the class that does the networking */
	public class Connection {
		private connectionThread cThread = new connectionThread();  //Creates a thread to run the networking
		private Consumer<Serializable> consumerData;  //serializable consumer method to send serializable data through network
		
		private String ip;  
		private int port;
		
		/* Costructor receives ip port and data for consumer */
		public Connection(String ip, int port,Consumer<Serializable> consumerData){
			this.consumerData = consumerData;
			this.ip = ip;
			this.port = port;
			cThread.setDaemon(true); //set daemon thread to prevent execution to stop
		}
		
		/* Start the thread */
		public void start() throws Exception {
			cThread.start();
		}
		
		/* Send data */
		public void send(Serializable data) throws Exception{
			cThread.output.writeObject(data);
		}
		public String getIp() {
			return ip;
		}

		public int getPort() {
			return port;
		}
		/* Close the thread socket */
		public void close() throws Exception {
			cThread.socket.close();
		}
		
		/* class connectionThread that extends Thread and has the networking */
		private class connectionThread extends Thread{
			private Socket socket;  // socket to perform the connection 
			private ObjectOutputStream output; //output steam
			
			
			@Override
			public void run(){
				/* Try block will work if socket is set and both output and input are working
				 * this will close socket and streams automatically if any of them don't work as expected */
				try (Socket socket = new Socket(getIp(), getPort());
						ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
						ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {

					
					chatMessages.clear(); //clear text area
					chatMessages.appendText("Connected to  "  + getIp() + " : " + getPort() + "\n"); //Displays connection details
					this.socket = socket;
					this.output = output;
					socket.setTcpNoDelay(true); //set no delay on the socket, it will not wait the buffer to be full
					
					while(true){  // loop to keep reading data
						Serializable data = (Serializable) input.readObject();
						consumerData.accept(data); //gives received data to consumer
					}
					
				} catch (Exception e){
					consumerData.accept("------- Connection Lost -------"); //if connection can't be established, then display error message
				}
				
			}
		}
	}
	
}

