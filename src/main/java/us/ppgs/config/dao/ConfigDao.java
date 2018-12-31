package us.ppgs.config.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;

@Repository
public class ConfigDao {

	private JdbcTemplate jt;

	@Autowired
	public ConfigDao(DataSource ds) {
		jt = new JdbcTemplate(ds);
	}
	
	public Map<String, ConfigPair> getCache() {
		final Map<String, ConfigPair> cache = new TreeMap<String, ConfigPair>();

		jt.query("select id, name, multi_line, length, value from config", new RowCallbackHandler() {
			
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				ConfigPair c = new ConfigPair(rs.getInt("id"),
											rs.getString("name"),
											rs.getBoolean("multi_line"),
											rs.getInt("length"),
											rs.getString("value"));
				cache.put(c.getName(), c);
			}
		});
		
//		jt.query("select name, value from config where not multi_line", new RowCallbackHandler() {
//			@Override
//			public void processRow(ResultSet rs) throws SQLException {
//				ConfigPair cp = cache.get(rs.getString(1));
//				cp.setValue(rs.getString(2));
//			}
//		});
		
		return cache;
	}

	public String getValue(String key) {
		final String[] r = new String[1];
		
		jt.query("select value from config where name = ?",
				new Object[] {key}, 
				new RowCallbackHandler() {
						@Override
						public void processRow(ResultSet rs) throws SQLException {
							r[0] = rs.getString(1);
						}
		});
		
		return r[0];
	}

	public void add(String name, String value) throws ConfigException {
		try {
			jt.update("insert into config (name, multi_line, length, value) values(?, ?, ?, ?)",
					new Object[] { name, isMultiLine(value), value.length(), value });
		}
		catch(DuplicateKeyException dae) {
			throw new ConfigException("new", "Config name '" + name + "'already exists");
		}
	}

	public void delete(int id) {
		jt.update("delete from config where id=? ", new Object[] { id });
	}
	
	public void save(String name, String value) {
		jt.update("update config set multi_line=?, length=?, value=? where name=?",
				new Object[] {isMultiLine(value), value.length(), value, name});
	}
	
	public void save(int id, String value) {
		jt.update("update config set multi_line=?, length=?, value=? where id=?",
				new Object[] {isMultiLine(value), value.length(), value, id});
	}
	
	public void rename(int id, String nameTo) throws ConfigException {
		try {
			jt.update("update config set name=? where id=?",
					new Object[] {nameTo, id});
		}
		catch(DuplicateKeyException dae) {
			throw new ConfigException("", "Config name '" + nameTo + "'already exists");
		}
	}
	
	private boolean isMultiLine(String value) {
		return value.contains("\n") || value.equals("\\n");
	}
}
