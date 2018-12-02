package mapsql.sql.command;

import mapsql.sql.core.SQLCommand;
import mapsql.sql.core.SQLException;
import mapsql.sql.core.SQLManager;

public class Sources implements SQLCommand {
	private String filename;
	
	public Sources(String filename) {
		this.filename = filename;
	}
	
	@Override
	public String execute(SQLManager manager) throws SQLException {
		System.out.println("Hello!");
		System.load(filename);
		return null;
	}
}
