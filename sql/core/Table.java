package mapsql.sql.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import mapsql.sql.condition.Equals;
import mapsql.util.LinkedList;
import mapsql.util.List;
import mapsql.util.Position;

public class Table {
	private TableDescription description;
	private List<Row> rows = new LinkedList<Row>();
	
	public Table(TableDescription description) {
		this.description = description;
	}
	
	public TableDescription description() {
		return description;
	}

	public List<Row> select(Condition where) throws SQLException {
		List<Row> list = new LinkedList<Row>();
		for (Row row : rows) {
			if (row.satisfies(where, description)) list.insertLast(row);
		}
		return list;
	}

	public void insert(String[] columns, String[] values) throws SQLException {
		Map<String, String> data = new HashMap<String, String>();
		
		for (int i=0; i < columns.length; i++) {
			Field field = description.findField(columns[i]);
			if (field.isUnique()) {
				if (!select(new Equals(columns[i], values[i])).isEmpty()) {
					throw new SQLException("Column '" + columns[i] + "' is UNIQUE - a row with '" + values[i] + "' already exists");
				}
			}
			data.put(columns[i], field.validate(values[i])); 
		}
		
		Field[] fields = description.fields();
		for (int i=0; i < fields.length; i++) {
			if (!data.containsKey(fields[i].name())) {
				String val = fields[i].defaultValue();

				if (fields[i].isUnique()) {
					if (!select(new Equals(fields[i].name(), val)).isEmpty()) {
						throw new SQLException("Column '" + fields[i].name() + "' is UNIQUE - a row with '" + val + "' already exists");
					}
				}
				data.put(fields[i].name(), val);
			}
		}
		rows.insertLast(new Row(data));
	}

	public void update(String[] columns, String[] values, Condition where) throws SQLException {
		
		//find field to update by parameter columns and 
		//replace the corresponding data of the row 
		
		for (Row row : rows) {
			if (row.satisfies(where, description))
			{
				for (int i=0; i < columns.length; i++) {
					Field field = description.findField(columns[i]);

					row.data.replace(field.name(), values[i]);
				}
			}
		}
	}
	
	public void delete(Condition where) throws SQLException {
//		Iterator<Row> it = rows.iterator();
//		
//		while(it.hasNext()) {
//			Row row = it.next();
//			if (row.satisfies(where, description))
//			{
//				it.remove();
//			}
//		}
		
		//create a same new List<Row> list
		List<Row> list = new LinkedList<Row>();
		for (Row row : rows) {
			list.insertLast(row);
		}
		
		//traverse the list to find the delete 
		//position in rows and remove it
		Position <Row> p = rows.first();
		for (Row row : list) {
			if (!row.satisfies(where, description))
			{
				p = rows.next(p);
			}
			else {
				rows.remove(p);
//				p = rows.next(p);
			}
		}
	}
}
