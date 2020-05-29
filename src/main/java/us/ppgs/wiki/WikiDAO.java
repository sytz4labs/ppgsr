package us.ppgs.wiki;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.stereotype.Repository;

@Repository
public class WikiDAO implements ApplicationListener<ApplicationReadyEvent> {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		
		jdbcTemplate.execute("create table if not exists ewiki_version"
				+ " (release int not null)"
				+ " AS select 0");
		
		int release = jdbcTemplate.queryForObject("select release from ewiki_version", Integer.class).intValue();
		
		if (release == 0) {
			jdbcTemplate.execute("create table ewiki (" + 
					"    key varchar(255) not null," +
					"    modified long not null," +
					"    value clob not null," +
					"    PRIMARY KEY (key))");
			
			release = 1;
			jdbcTemplate.update("update ewiki_version set release = ?", new Object[] {release});
		}
	}

	private BytesEncryptor bEncrypt = Encryptors.stronger("HIy5vWBCYugjwRHmDLzf27Ti00Ak6EkSoAjmvQIgADzpq85Fr5bu8zqxCROE7bl", "29E510B3D6677AF0C29FA31B0C88C57F188669C606DBF7245A755AC9A994BCDB");

	public FileInfo getFile(String name) {

		FileInfo[] fia = new FileInfo[1];
		
		jdbcTemplate.query("select * from ewiki where key = ?",
				new Object[] { name },
				new RowCallbackHandler() {
					@Override
					public void processRow(ResultSet rs) throws SQLException {
		
						FileInfo fi = new FileInfo();
						fi.modified = rs.getLong("modified");
						fi.contents = new String(bEncrypt.decrypt(Base64.getDecoder().decode(rs.getString("value"))));
						fia[0] = fi;
					}
		});
		
		return fia[0];
	}
	
	public void saveFile(String name, String contents) {
		
		jdbcTemplate.update("merge into ewiki (key,modified,value)" +
				"values (?,?,?)",
				name,
				System.currentTimeMillis(),
				Base64.getEncoder().encodeToString(bEncrypt.encrypt(contents.getBytes())));
	}
}
