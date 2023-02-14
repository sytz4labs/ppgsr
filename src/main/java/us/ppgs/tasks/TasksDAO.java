package us.ppgs.tasks;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;

@Repository
public class TasksDAO implements ApplicationListener<ApplicationReadyEvent> {

	@Autowired
	private JdbcTemplate jt;

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		
		jt.execute("create table if not exists tasks_version"
				+ " (release int not null)"
				+ " AS select 0");
		
		int release = jt.queryForObject("select release from tasks_version", Integer.class).intValue();
		
		if (release == 0) {
			jt.execute("create table tasks ("
					+ " id int auto_increment,"
					+ " area varchar(255),"
					+ " priority int,"
					+ " task varchar(255) not null,"
					+ " benefit varchar(255),"
					+ " notes clob,"
					+ " PRIMARY KEY (id))");

			release = 1;
			jt.update("update tasks_version set release = ?", new Object[] {release});
		}
	}

	public void newTask(String area, String task) {
		jt.update("insert into tasks (area, priority, task, benefit, notes) values(?, 1000, ?, '', '')",
				new Object[] { area, task});
	}	

	public List<TaskInfo> getTasks() {
		
		final List<TaskInfo> r = new ArrayList<TaskInfo>();

		jt.query("select id, area, priority, task, benefit, notes from tasks order by priority desc, id desc",
				new RowCallbackHandler() {
					@Override
					public void processRow(ResultSet rs) throws SQLException {
						TaskInfo ti = new TaskInfo(
								rs.getInt("id"),
								rs.getString("area"),
								rs.getInt("priority"),
								rs.getString("task"),
								rs.getString("benefit"),
								rs.getString("notes"));
						
						r.add(ti);
					}
		});
				
		return r;
	}

	public Set<String> getAreas() {
		
		final Set<String> r = new HashSet<String>();
		r.add("");

		jt.query("select distinct area from tasks order by area",
				new RowCallbackHandler() {
					@Override
					public void processRow(ResultSet rs) throws SQLException {
						r.add(rs.getString("area"));
					}
		});
				
		return r;
	}

	public void updateTask(int id, String cmd, String val) {
		if (val.length() == 0 && cmd.equals("task")) {
			jt.update("delete tasks where id=? ", new Object[] { id });
		}
		else {
			jt.update("update tasks set " + cmd + "=? where id=? ", new Object[] { val, id });
		}
	}
}
