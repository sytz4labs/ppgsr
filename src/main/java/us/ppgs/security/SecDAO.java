package us.ppgs.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SecDAO implements ApplicationListener<ApplicationReadyEvent> {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		jdbcTemplate.execute("CREATE table if not exists PERSISTENT_LOGINS("
				+ "USERNAME VARCHAR(64) NOT NULL,"
				+ "SERIES VARCHAR(64) NOT NULL,"
				+ "TOKEN VARCHAR(64) NOT NULL,"
				+ "LAST_USED TIMESTAMP NOT NULL,"
				+ "PRIMARY KEY(SERIES))");
		
		jdbcTemplate.execute("CREATE table if not exists SEC_USERS("
				+ "USERNAME VARCHAR(64) NOT NULL,"
				+ "PASSWORD VARCHAR(60) NOT NULL,"
				+ "ENABLED TINYINT DEFAULT 1 NOT NULL,"
				+ "PRIMARY KEY(USERNAME)) "
				+ "AS select 'pseufzer', '$2a$10$l.zrvL/4gPT2rY0vKdp3UOqSP/v6Ssklx2EtxbB8XMUZYC.lbQPpS', 1");

		jdbcTemplate.execute("CREATE table if not exists SEC_USER_ROLES("
				+ "USER_ROLE_ID INT AUTO_INCREMENT,"
				+ "USERNAME VARCHAR(45) NOT NULL,"
				+ "ROLE VARCHAR(45) NOT NULL,"
				+ "PRIMARY KEY(USER_ROLE_ID))");
		
		Integer rolesCount = jdbcTemplate.queryForObject("select count(*) from SEC_USER_ROLES", Integer.class);
		
		if (rolesCount == 0) {
			jdbcTemplate.execute("INSERT INTO SEC_USER_ROLES(USER_ROLE_ID, USERNAME, ROLE) VALUES" + 
					"(1, 'pseufzer', 'ROLE_USER')," + 
					"(2, 'pseufzer', 'ROLE_ADMIN');");
		}
	}

}
