package Utilites;

import sql.ConnectionHandler;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Joshua Channon on 12/12/2016.
 */
public class H2TestServer{

	private static final H2TestServer INSTANCE;
	static {
		try {
			INSTANCE = new H2TestServer();
		} catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	private Connection connection;
	private Statement stmt;

	private H2TestServer() throws Exception {
		// build database
		String driver = "org.h2.Driver";
		String protocol = "jdbc:h2:mem:MODE=MySQL";

		Class.forName(driver).newInstance();
		connection = DriverManager.getConnection(protocol);
		stmt = connection.createStatement();

		// change the connection in ConnectionHandler to the h2 database in
		// memory
		ConnectionHandler ch = ConnectionHandler.getInstance();
		Field f = ConnectionHandler.class.getDeclaredField("connection");
		f.setAccessible(true);
		f.set(ch, connection);
	}

	public static H2TestServer getInstance() {
		return INSTANCE;
	}

	public void createTables() throws SQLException {
		String createUsers = "create table users (\n"
				+ "name varchar(45),\n"
				+ "email varchar(45) NOT NULL,\n"
				+ "primary key(email)\n"
				+ ")";
		String createLocations = "create table locations (\n"
				+ "loc_name varchar(45) NOT NULL,\n"
				+ "capacity int,\n"
				+ "primary key(loc_name)\n"
				+ ")";
		String createEvents = "create table events (\n"
				+ "id int NOT NULL,\n"
				+ "description varchar(10000),\n"
				+ "loc_name varchar(45),\n"
				+ "startTime datetime,\n"
				+ "endTime datetime,\n"
				+ "email varchar(45),\n"
				+ "primary key(id),\n"
				+ "foreign key(email) references users(email)\n"
				+ "on delete cascade on update cascade,\n"
				+ "foreign key(loc_name) references locations(loc_name)\n"
				+ "on delete cascade on update cascade\n"
				+ ")";
		String createInvited = "create table invited (\n"
				+ "id int NOT NULL,\n"
				+ "email varchar(45),\n"
				+ "primary key(id, email),\n"
				+ "foreign key(id) references events(id)\n"
				+ "on delete cascade on update cascade,\n"
				+ "foreign key(email) references users(email)\n"
				+ "on delete cascade on update cascade\n"
				+ ")";

		stmt.execute(createUsers);
		stmt.execute(createLocations);
		stmt.execute(createEvents);
		stmt.execute(createInvited);
	}

	public void dropTables() throws SQLException {
		stmt.execute("DROP TABLE events");
		stmt.execute("DROP TABLE users");
		stmt.execute("DROP TABLE locations");
		stmt.execute("DROP TABLE invited");
	}

	public Connection getConnection() {
		return connection;
	}


}
