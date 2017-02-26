package assignment6;

/* Database connectivity
 * Lucas de Morais Tramasso

 This program creates an interface to manage a simple database with simple fields as seen below.
 It uses mySQL, make sure to configure the driver and set the right information in order to connect.
 Although it is simple, this program gives a good base for javafx interface and database connectivity. 
 */


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Assignment6 extends Application{
	/* TextFields for Interface, they are used for input and output of the information */
	TextField fieldID = new TextField();
	TextField fieldLastName = new TextField();
	TextField fieldFirstName = new TextField();
	TextField fieldMi = new TextField();
	TextField fieldAddress = new TextField();
	TextField fieldCity = new TextField();
	TextField fieldState = new TextField();
	TextField fieldTelephone = new TextField();
	TextField fieldEmail = new TextField();
	
	/* Labels for all TextFields*/
	Label labelID = new Label("ID:");
	Label labelLastName = new Label("Last Name:");
	Label labelFirstName = new Label("First Name:");
	Label labelMi = new Label("Middle Initial:");
	Label labelAddress = new Label("Address:");
	Label labelCity = new Label("City:");
	Label labelState = new Label("State:");
	Label labelTelephone = new Label("Telephone:");
	Label labelEmail = new Label("E-mail:");
	
	/* All Buttons */
	Button insertButton = new Button("Insert");
	Button viewButton = new Button("View");
	Button updateButton = new Button("Update");
	Button clearButton = new Button("Clear");
	
	/* Strings used as data to establish connection to the database 
	 * Change the database URL, user and password to the database you want to connect */
	String dbDriver = "com.mysql.jdbc.Driver";
	String dbUrl = "jdbc:mysql://localhost:3306/Java_test?autoReconnect=true&useSSL=false"; //turned SSL off to avoid errors and warnings
	String dbUser = "root";			//
	String dbPassword = "password";
	
	/* output area for some warnings and messages to the user */
	Text output = new Text();
	
	/* Connection and statement variables to handle the connection and data to mySQL server */
	private Connection connection;
	private Statement statement;

	/* Main method will just launch the interface */
	public static void main(String[] args) {
		launch(args);
	}
	
	/* Start method is the method that draws the interface */
	@Override
	public void start(Stage primaryStage){
		primaryStage.setTitle("Assignment 6");  //set the title to the window
		GridPane grid = new GridPane();	//create a new pane with grid layout
		grid.setAlignment(Pos.TOP_LEFT); //align it to the top left of the window
		grid.setHgap(10); //Horizontal gap between rows/columns of the grid
		grid.setVgap(10); //Vertical gap between rows/columns of the grid
		grid.setPadding(new Insets(10,10,10,10)); //Padding to the grid inside the window
		
		/* Set sizes, translations to all labels and fields and then add them to the grid 
		 * grid.add(textfield/label/other,column,row,columnspan,rowspan)	*/
		
		fieldID.setMaxWidth(30);
		fieldID.setTranslateX(30);
		grid.add(labelID, 0, 1);
		grid.add(fieldID, 0, 1);
		
		labelLastName.setPrefWidth(70);
		fieldLastName.setMaxWidth(200);
		grid.add(labelLastName, 0, 2);
		grid.add(fieldLastName, 1, 2);
		
		labelFirstName.setTranslateX(215);
		grid.add(labelFirstName, 1, 2,2,1);
		grid.add(fieldFirstName, 3, 2);
		
		fieldMi.setMaxWidth(40);
		grid.add(labelMi, 4, 2);
		grid.add(fieldMi, 5, 2);
		
		fieldAddress.setMaxWidth(250);
		fieldAddress.setTranslateX(65);
		grid.add(labelAddress, 0, 3);
		grid.add(fieldAddress, 0, 3,2,1);
		
		grid.add(labelCity, 2, 3);
		grid.add(fieldCity, 3, 3);
		
		fieldState.setMaxWidth(40);
		fieldState.setTranslateX(45);
		grid.add(labelState, 4, 3);
		grid.add(fieldState, 4, 3);
		
		fieldTelephone.setMaxWidth(200);
		grid.add(labelTelephone, 0, 4);
		grid.add(fieldTelephone, 1, 4);
		
		labelEmail.setTranslateX(220);
		fieldEmail.setTranslateX(20);
		grid.add(labelEmail, 1, 4);
		grid.add(fieldEmail, 2, 4,2,1);
		
		/* Creates a Hbox (Horizontal layout) to add all buttons */
		HBox buttons = new HBox(10);
		buttons.setAlignment(Pos.TOP_LEFT); //align the buttons to top left of the hbox
		buttons.getChildren().add(viewButton);
		buttons.getChildren().add(insertButton);
		buttons.getChildren().add(updateButton);
		buttons.getChildren().add(clearButton);
		
		/* Add the hbox to the previously creted grid */
		grid.add(buttons, 1, 7);
		
		/* adds the output field to the grid */
		grid.add(output,3,7,3,2);
		output.setWrappingWidth(300.0); // set the wrapping of the output so text wont go out of the window 
		
		//Call initDB method, this will establish connection to the database
		initDB();
		
		/* Set action to the view button */
		viewButton.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent e){
				try {
					statement = connection.createStatement(); //statement for mySQL
					/* query string holds the select statement for mySQL */
					String queryString = "select id,lastName,firstName,mi,address,city,state,telephone,email from Staff where Staff.id = '" + fieldID.getText().trim() + "'";
					
					ResultSet results = statement.executeQuery(queryString); //Save the results received from the execution ot the query
					
					/* If there is a result, display it on the interface */
					if(results.next()){
						fieldLastName.setText(results.getString(2));
						fieldFirstName.setText(results.getString(3));
						fieldMi.setText(results.getString(4));
						fieldAddress.setText(results.getString(5));
						fieldCity.setText(results.getString(6));
						fieldState.setText(results.getString(7));
						fieldTelephone.setText(results.getString(8));
						fieldEmail.setText(results.getString(9));
						output.setText("Record Found");
					
					/* If there is no result, then there is no record for that id */
					}else {
						output.setText("Record Not Found");
					}
				}catch (SQLException e1){
					e1.printStackTrace();
				}
			}
		});
		
		/* Set Action to the clear button */
		clearButton.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent e){
				clearGrid(grid); //Calls clearGrid method 
				output.setText(""); //delete any text that is being displayed on the output
			}
		});
		 
		/* Set Action to the insert button */
		insertButton.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent e){
				try {
					statement = connection.createStatement(); //statement for mySQL communication
					/* sqlInput is the string with the sql insert method */
					String sqlInput = "insert into Staff (id,lastName,firstName,mi,address,city,state,telephone,email) values ('" + 
										fieldID.getText().trim() + "','" + fieldLastName.getText().trim() + "','" + fieldFirstName.getText().trim() 
										+ "','" + fieldMi.getText().trim() + "','" + fieldAddress.getText().trim() + "','" + fieldCity.getText().trim()
										+ "','" + fieldState.getText().trim() + "','" + fieldTelephone.getText().trim() + "','" + fieldEmail.getText().trim()
										+ "');";
					/* executes the sql code on sqlInput */
					statement.executeUpdate(sqlInput);
					output.setText("Record Inserted"); //Displays that the Record was succesfuly inserted
					clearGrid(grid); //Clear the grid
					statement.close(); //close statement
					
				}catch (SQLException e2){
					output.setText(e2.getMessage()); //if there is an exception, display it to the user
				}
			}
		});
		
		/* Set update button action */
		updateButton.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent e){
				try {
					statement = connection.createStatement(); //statement for mySQL communication
					/* sqlInput is the string with the sql insert method for updates */
					String sqlInput = "update Staff set lastName='" + fieldLastName.getText().trim()
										+ "',firstName='" + fieldFirstName.getText().trim() + "',mi='" + fieldMi.getText().trim()
										+ "',address='" + fieldAddress.getText().trim() + "',city='" + fieldCity.getText().trim()
										+ "',state='" + fieldState.getText().trim() + "',telephone='" + fieldTelephone.getText().trim()
										+ "',email='" + fieldEmail.getText().trim() + "' where id='" + fieldID.getText().trim() + "';";
					/* executes the sql code on sqlInput */
					statement.executeUpdate(sqlInput);
					output.setText("Record Updated"); //Displays that the Record was succesfuly updated
					statement.close();  //close statement
				}catch(SQLException e3){
					output.setText(e3.getMessage()); //if there is an exception, display it to the user
				}
			}
		});
		
		Scene scene = new Scene(grid,750,250); //Creates a scene and adds the grid to it
		primaryStage.setScene(scene); //set the scene as the primaryStage
		primaryStage.show(); //display the scene to the user
	}
	
	/* Method to search all TextFields in a given grid and clear the text of them */
	public void clearGrid(GridPane grid){
		for (Node node : grid.getChildren()){
			if(node instanceof TextField){
				((TextField)node).setText("");
				
			}
		}
	}
	/* Method to initialize the database connection */
	public void initDB(){
		try {
			Class.forName(dbDriver); //gets database driver 
			
			connection = DriverManager.getConnection(dbUrl,dbUser,dbPassword);	 //establish connection
		}catch (ClassNotFoundException e){
			e.printStackTrace();
		}catch (SQLException e){
			e.printStackTrace();
		}
	}
	
}
