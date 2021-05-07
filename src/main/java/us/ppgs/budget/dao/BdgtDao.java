package us.ppgs.budget.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;

import us.ppgs.budget.info.BdgtTranInfo;
import us.ppgs.budget.info.BdgtTranInfo.State;
import us.ppgs.budget.info.BdgtTranSpecInfo;
import us.ppgs.budget.info.BdgtTranSpecInfo.Type;

@Repository
public class BdgtDao {

	private JdbcTemplate jt;
	private Map<String, String> updates = null;
	
	@Autowired
	public BdgtDao(DataSource ds) {
		jt = new JdbcTemplate(ds);
		updates = new HashMap<String, String>();
		updates.put("ts.description",	"update bdgt_tran_spec set modified = now(), description = ? where id = ?");
		updates.put("ts.nMons",			"update bdgt_tran_spec set modified = now(), nMons = ? where id = ?");
		updates.put("ts.day",			"update bdgt_tran_spec set modified = now(), day = ? where id = ?");
		updates.put("ts.type",			"update bdgt_tran_spec set modified = now(), type = ? where id = ?");
		updates.put("ts.value",			"update bdgt_tran_spec set modified = now(), value = ? where id = ?");
		
		updates.put("t.date",			"update bdgt_tran set modified = now(), date = ? where id = ?");
		updates.put("t.description",	"update bdgt_tran set modified = now(), description = ? where id = ?");
		updates.put("t.type",			"update bdgt_tran set modified = now(), type = ? where id = ?");
		updates.put("t.value",			"update bdgt_tran set modified = now(), value = ? where id = ?");
		updates.put("t.state",			"update bdgt_tran set modified = now(), state = ? where id = ?");
	}

	private class TranSpecRowCallback implements RowCallbackHandler {

		private final List<BdgtTranSpecInfo> r = new ArrayList<BdgtTranSpecInfo>();
		public List<BdgtTranSpecInfo> getTranSpecs() {
			return r;
		}
		
		@Override
		public void processRow(ResultSet rs) throws SQLException {
			r.add(new BdgtTranSpecInfo(rs.getInt("id"),
					rs.getString("description"),
					rs.getInt("nMons"),
					rs.getInt("day"),
					Type.valueOf(rs.getString("type")),
					rs.getBigDecimal("value")));
		}
	};

	// specifications
	public List<BdgtTranSpecInfo> getTranSpecs() {
		
		TranSpecRowCallback cb = new TranSpecRowCallback();
		
		jt.query("select id, description, nMons, day, type, value from bdgt_tran_spec order by description", cb);
		
		return cb.getTranSpecs();
	}

	private BdgtTranSpecInfo getTranSpec(int spec_id) {

		TranSpecRowCallback cb = new TranSpecRowCallback();
		
		jt.query("select id, description, nMons, day, type, value from bdgt_tran_spec where id=?",
				cb,
				spec_id);
		
		return cb.getTranSpecs().get(0);
	}

	// transactions
	public List<BdgtTranInfo> getTrans() {
		final List<BdgtTranInfo> r = new ArrayList<BdgtTranInfo>();
		
		jt.query("select id, spec_id, date, description, type, value, state from bdgt_tran",
				new RowCallbackHandler() {
						@Override
						public void processRow(ResultSet rs) throws SQLException {
							String type = rs.getString("type");
							r.add(new BdgtTranInfo(rs.getInt("id"),
									rs.getInt("spec_id"),
									new Date(rs.getDate("date").getTime()),
									rs.getString("description"),
									type == null ? null : Type.valueOf(rs.getString("type")),
									rs.getBigDecimal("value"),
									State.valueOf(rs.getString("state"))));
						}
		});
		
		return r;
	}
	
	public void update(String field, int id, Object value) {

		jt.update(updates.get(field), new Object[] { value, id });
	}

	public void newTranspec(String value) {
		
		jt.update("insert into bdgt_tran_spec (modified, description, nMons, day, type, value) values (now(), ?, ?, ?, ?, ?)",
				new Object[] {value, 1, 1, BdgtTranSpecInfo.Type.DBT.toString(), 0});
	}

	public void setTran(int specId, Date timeMs) {
		
		BdgtTranSpecInfo spec = getTranSpec(specId);
		
		jt.update("insert into bdgt_tran (modified, spec_id, date, state, value) values (now(), ?, ?, ?, ?)",
				new Object[] {specId, timeMs, State.Proposed.toString(), spec.getValue()});
	}

	public void delTran(int id) {
		jt.update("delete bdgt_tran where id = ?", new Object[] {id});
	}

	public void newTran(Date newDate) {

		jt.update("insert into bdgt_tran (spec_id, date, modified, description, type, state, value) values (0, ?, now(), 'NEW', ?, ?, 0)",
				new Object[] {newDate, BdgtTranSpecInfo.Type.DBT.toString(), State.Scheduled.toString()});
	}
}
