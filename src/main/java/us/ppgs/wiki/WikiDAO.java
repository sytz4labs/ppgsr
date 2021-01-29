package us.ppgs.wiki;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
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
		
		if (release == 1) {
			jdbcTemplate.execute("alter table ewiki rename to ewiki_old");

			jdbcTemplate.execute("create table ewiki (" + 
					"    file varchar(255) not null," +
					"    page varchar(255) not null," +
					"    modified long not null," +
					"    contents clob not null," +
					"    PRIMARY KEY (file, page))");

			jdbcTemplate.execute("insert into ewiki select key, '', modified, value from ewiki_old");

			release = 2;
			jdbcTemplate.update("update ewiki_version set release = ?", new Object[] {release});
		}
	}

	private BytesEncryptor bEncrypt = Encryptors.stronger("HIy5vWBCYugjwRHmDLzf27Ti00Ak6EkSoAjmvQIgADzpq85Fr5bu8zqxCROE7bl", "29E510B3D6677AF0C29FA31B0C88C57F188669C606DBF7245A755AC9A994BCDB");

	private class EwikiExtractor implements RowMapper<FileInfo> {
		@Override
		public FileInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new FileInfo(rs.getString("file"),
					rs.getString("page"),
					rs.getLong("modified"),
					new String(bEncrypt.decrypt(Base64.getDecoder().decode(rs.getString("contents")))));
		}
	}
	
	public List<FileInfo> getFile(String name) {

		return jdbcTemplate.query("select * from ewiki where file = ?",
				new EwikiExtractor(),
				name);
	}
	
	public void saveFile(String file, String page, String contents) {
		
		jdbcTemplate.update("merge into ewiki (file,page,modified,contents)" +
				"values (?,?,?,?)",
				file,
				page,
				System.currentTimeMillis(),
				Base64.getEncoder().encodeToString(bEncrypt.encrypt(contents.getBytes())));
	}
}
