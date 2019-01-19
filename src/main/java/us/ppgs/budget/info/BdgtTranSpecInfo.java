package us.ppgs.budget.info;

import java.math.BigDecimal;

import lombok.Data;

public @Data class BdgtTranSpecInfo {

	public enum Type { DBT, CRDT };

	private int id; // unique identity
	private String description;
	private int nMons; // Months 1 = every month, 6 = every 6 months
	private int day; // day  1,2 .. 31
	private Type type; // average (last year), specified, entered
	private BigDecimal value; // dollar value '-' equals debit 

	public BdgtTranSpecInfo(int id, String description, int nMons, int day, Type type, BigDecimal value) {
		this.id = id;
		this.description = description;
		this.nMons = nMons;
		this.day = day;
		this.type = type;
		this.value = value;
	}
}
