package us.ppgs.wiki;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.stereotype.Repository;

import us.ppgs.linkfarm.info.LFList;

@Repository
public class WikiDAO implements ApplicationListener<ApplicationReadyEvent> {

	@Autowired
	private JdbcTemplate jt;

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		
		jt.execute("create table if not exists ewiki_version"
				+ " (release int not null)"
				+ " AS select 0");
		
		int release = jt.queryForObject("select release from ewiki_version", Integer.class).intValue();
		
		if (release == 0) {
			jt.execute("create table ewiki (" +
					"    id int auto_increment," +
					"    sort int default -1 not null," + 
					"    page varchar(255) not null," +
					"    tab varchar(255) not null," +
					"    modified long not null," +
					"    contents clob not null," +
					"    unique (page, tab)," +
					"    primary key (id))");


			release = incrementRelease(2);
		}
	}

	private Integer incrementRelease(Integer release) {
		release = release + 1;
		jt.update("update ewiki_version set release = ?", new Object[] {release});
		return release;
	}

	private BytesEncryptor bEncrypt = Encryptors.stronger("HIy5vWBCYugjwRHmDLzf27Ti00Ak6EkSoAjmvQIgADzpq85Fr5bu8zqxCROE7bl", "29E510B3D6677AF0C29FA31B0C88C57F188669C606DBF7245A755AC9A994BCDB");

	private class EwikiExtractor implements RowMapper<PageInfo> {
		@Override
		public PageInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new PageInfo(rs.getInt("id"),
					rs.getInt("sort"),
					rs.getString("page"),
					rs.getString("tab"),
					rs.getLong("modified"),
					new String(bEncrypt.decrypt(Base64.getDecoder().decode(rs.getString("contents")))));
		}
	}
	
	public List<PageInfo> getPage(String page) {

		return jt.query("select * from ewiki where page = ? order by sort",
				new EwikiExtractor(),
				page);
	}
	
	public void savePageContents(PageInfo req) {
		if (req.getId() < 0) {
			jt.update("insert into ewiki (sort,page,tab,modified,contents)" +
					"values (?,?,?,?,?)",
					req.getSort(),
					req.getPage(),
					req.getTab(),
					System.currentTimeMillis(),
					Base64.getEncoder().encodeToString(bEncrypt.encrypt(req.getContents().getBytes())));
		}
		else {
			jt.update("update ewiki set modified=?, contents=? where id=?",
					System.currentTimeMillis(),
					Base64.getEncoder().encodeToString(bEncrypt.encrypt(req.getContents().getBytes())),
					req.getId());
		}
	}

	public void savePageTab(PageInfo req) {
		if (req.getId() < 0) {
			jt.update("insert into ewiki (sort,page,tab,modified,contents)" +
					"values (?,?,?,?,?)",
					req.getSort(),
					req.getPage(),
					req.getTab(),
					System.currentTimeMillis(),
					Base64.getEncoder().encodeToString(bEncrypt.encrypt("".getBytes())));
			jt.execute("update ewiki set sort = id where sort = -1");
		}
		else {
			if (req.getTab().trim().length() == 0) {
				jt.update("delete ewiki where id=?",
						req.getId());
			}
			else {
				jt.update("update ewiki set tab=? where id=?",
						req.getTab(),
						req.getId());
			}
		}
	}

	public void saveMoveTab(PageInfo req, String direction) {
		
		var ids = new LFList<Integer>();
		var sorts = new ArrayList<Integer>();
		var indexA = new AtomicInteger();
		
		jt.query("select id, sort from ewiki where page = ? order by sort", new RowCallbackHandler() {
			private int index = 0;
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				var id = rs.getInt("id");
				ids.add(id);
				sorts.add(rs.getInt("sort"));
				if (id == req.getId()) {
					indexA.set(index);
				}
				index++;
			}
		}, req.getPage());
		
		if ("up".equals(direction)) {
			ids.rotateMinus(indexA.get());
		}
		else {
			ids.rotatePlus(indexA.get());
		}
		
		for (int i = 0; i < ids.size(); i++) {
			jt.update("update ewiki set sort = ? where id = ?", sorts.get(i), ids.get(i));
		}
	}
}
