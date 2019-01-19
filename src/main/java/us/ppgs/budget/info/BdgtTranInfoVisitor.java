package us.ppgs.budget.info;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import us.ppgs.budget.info.BdgtTranSpecInfo.Type;

public class BdgtTranInfoVisitor extends BdgtTranInfo {

	public BdgtTranInfoVisitor(BdgtTranSpecInfo spec, BdgtTranInfo tran) {
		super(tran.getId(), tran.getSpecId(), tran.getDate(), tran.getDescription(), tran.getType(), tran.getValue(), tran.getState());
		this.spec = spec;
		this.firstGenerated = false;
	}

	public BdgtTranInfoVisitor(BdgtTranSpecInfo spec, Calendar current, boolean firstGenerated) {
		super(-1, spec.getId(), new Date(current.getTimeInMillis()), null, null, spec.getValue(), State.Estimate);
		this.spec = spec;
		this.firstGenerated = firstGenerated;
	}
	
	private BdgtTranSpecInfo spec;
	private BigDecimal subTotal = new BigDecimal(0);
	private boolean firstGenerated = false;
	
	public BdgtTranSpecInfo getSpec() {
		return spec;
	}
	
	public BigDecimal getSubTotal() {
		return subTotal;
	}
	public void setSubTotal(BigDecimal subTotal) {
		this.subTotal = subTotal;
	}
	public boolean getFirstGenerated() {
		return firstGenerated;
	}

	// convenience
	@Override
	public String getDescription() {
		if (spec == null) {
			return super.getDescription();
		}
		else {
			return spec.getDescription();
		}
	}

	@Override
	public Type getType() {
		if (spec == null) {
			return super.getType();
		}
		else {
			return spec.getType();
		}
	}
	
	public int getMonth() {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.MONTH) + 1;
	}
}
