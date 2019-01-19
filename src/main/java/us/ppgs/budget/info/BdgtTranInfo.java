package us.ppgs.budget.info;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import us.ppgs.budget.info.BdgtTranSpecInfo.Type;

public @Data class BdgtTranInfo {

	public BdgtTranInfo(int id, int specId, Date date, String description, Type type, BigDecimal value, State state) {
		this.id = id;
		this.specId = specId;
		this.date = date;
		this.description = description;
		this.value = value;
		this.type = type;
		this.state = state;
	}
	
	public enum State { Estimate, Proposed, Scheduled, Cleared };
	
	private int id; // unique identity
	private int specId; // points to spec that generated it
	@JsonFormat(pattern="MM-dd-yyyy")
	protected Date date; // date of transaction
	private String description;
	private Type type; // average (last year), specified, entered
	private State state; //auto, guess, scheduled, cleared
	private BigDecimal value; // dollar value
}
