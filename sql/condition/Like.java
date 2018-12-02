package mapsql.sql.condition;

import java.util.Map;
import java.util.regex.Pattern;

import mapsql.sql.core.Field;
import mapsql.sql.core.SQLException;
import mapsql.sql.core.TableDescription;
import mapsql.sql.field.CHARACTER;

public class Like extends AbstractCondition {
	private String column;
	private String value;
	
	public Like(String column, String value) {
		this.column = column;
		this.value = value;
	}

	@Override
	public boolean evaluate(TableDescription description, Map<String, String> data) throws SQLException {

		Field field = description.findField(column);
		
		// check for field of type CHARACTER
		if (!Field.CHARACTER.equals(field.type())) 
			throw new SQLException("Error: not CHARACTER field.");
		
		String val = (String) field.toValue(data.get(column));
		
		// change into regular expression
		// replace % with .*
		String pattern = value.replaceAll("%", ".*"); 
		pattern = "^" + pattern + "$";
		
		Pattern pat = Pattern.compile(pattern);
		
		return pat.matcher(val).matches();
		
	}
}
