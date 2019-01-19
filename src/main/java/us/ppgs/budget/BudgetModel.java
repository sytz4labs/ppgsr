package us.ppgs.budget;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import us.ppgs.budget.dao.BdgtDao;
import us.ppgs.budget.info.BdgtTranInfo;
import us.ppgs.budget.info.BdgtTranInfoVisitor;
import us.ppgs.budget.info.BdgtTranSpecInfo;

@Component
public class BudgetModel {

	@Autowired
	private BdgtDao bdgtDao;

    private void setDayWithLimits(Calendar cal, int day) {
    	if (day == 0) {
    		// set to end of month
    		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
    	}
    	else { // day > 0. set day of month or max
    		cal.set(Calendar.DAY_OF_MONTH, Math.min(day, cal.getActualMaximum(Calendar.DAY_OF_MONTH)));
    	}
    }
    
    public List<BdgtTranInfoVisitor> getTrans(List<BdgtTranSpecInfo> specs, boolean history) {

    	List<BdgtTranInfoVisitor> visitTrans = new ArrayList<>();
    	Map<Integer, Date> lastDateIndex = new HashMap<Integer, Date>();

    	getDBtrans(specs, visitTrans, lastDateIndex);
    	
    	generateTransFromSpecs(specs, visitTrans, lastDateIndex);
    	
    	// sort by date
    	Collections.sort(visitTrans, new Comparator<BdgtTranInfoVisitor>() {
			@Override
			public int compare(BdgtTranInfoVisitor o1, BdgtTranInfoVisitor o2) {
				return o1.getDate().compareTo(o2.getDate());
			}
		});

    	return calculateBalances(visitTrans, history);
    }

    private void getDBtrans(List<BdgtTranSpecInfo> specs, List<BdgtTranInfoVisitor> visitTrans, Map<Integer, Date> lastDateIndex) {
    	// index specifications from database
    	Map<Integer, BdgtTranSpecInfo> specIndex = new HashMap<Integer, BdgtTranSpecInfo>();
    	for (BdgtTranSpecInfo spec : specs) {
    		specIndex.put(spec.getId(), spec);
    	}
    	
    	// get previous transactions from database and calc last dates of spec transactions
    	List<BdgtTranInfo> trans = bdgtDao.getTrans();
    	for (BdgtTranInfo tran : trans) {
    		visitTrans.add(new BdgtTranInfoVisitor(specIndex.get(tran.getSpecId()), tran));

    		Date specLastDate = lastDateIndex.get(tran.getSpecId());
    		Date tranDate = tran.getDate();
    		if (specLastDate == null || tranDate.compareTo(specLastDate) > 0) {
    			lastDateIndex.put(tran.getSpecId(), tranDate);
    		}
    	}

    }
    
    private void generateTransFromSpecs(List<BdgtTranSpecInfo> specs, List<BdgtTranInfoVisitor> visitTrans, Map<Integer, Date> lastDateIndex) {
    	Calendar today = Calendar.getInstance();
		Calendar specLastCal = Calendar.getInstance();
    	Calendar endOfRun = Calendar.getInstance();
    	endOfRun.add(Calendar.MONTH, 7);
    	endOfRun.set(Calendar.DAY_OF_MONTH, 1);
    	endOfRun.set(Calendar.HOUR_OF_DAY, 0);
    	endOfRun.set(Calendar.MINUTE, 0);
    	endOfRun.set(Calendar.SECOND, 0);
    	endOfRun.set(Calendar.MILLISECOND, 0);

    	// generate projection of specs
    	for (BdgtTranSpecInfo spec : specs) {
    		if (spec.getNMons() > 0) {
	    		// get the last day a transaction was created for this spec
	    		Date specLastDate = lastDateIndex.get(spec.getId());
	    		if (specLastDate == null) {
	        		specLastCal = (Calendar) today.clone();
	    		}
	    		else {
	    			specLastCal.setTime(specLastDate);
	    		}
	    		
	    		// generate transactions
	    		if (spec.getDay() >= 0) {
	    			addMonthlyProjections(visitTrans, specLastCal, spec, endOfRun);
	    		}
	    		else {
	    			addWeeklyProjections(visitTrans, specLastCal, spec, endOfRun);
	    		}
    		}
    	}
    }
    
    private void addMonthlyProjections(List<BdgtTranInfoVisitor> visitTrans, Calendar specLastCal, BdgtTranSpecInfo spec, Calendar endOfRun) {

		specLastCal.add(Calendar.MONTH, spec.getNMons());
		setDayWithLimits(specLastCal, spec.getDay());
		Calendar loopCal = null;

    	boolean firstGenerated = true;
		int loopIndex = 0;
		do {
			loopCal = (Calendar) specLastCal.clone();

			// advance to next month of spec
			loopCal.add(Calendar.MONTH, spec.getNMons() * loopIndex);
			setDayWithLimits(loopCal, spec.getDay());
			if (loopCal.before(endOfRun)) {
				visitTrans.add(new BdgtTranInfoVisitor(spec, loopCal, firstGenerated));
			}

			firstGenerated = false;
			loopIndex++;
		} while (loopCal.before(endOfRun));
    }
    
    private void addWeeklyProjections(List<BdgtTranInfoVisitor> visitTrans, Calendar specLastCal, BdgtTranSpecInfo spec, Calendar endOfRun) {

		Calendar loopCal = (Calendar) specLastCal.clone();
    	loopCal.add(Calendar.DAY_OF_MONTH, ((-spec.getDay() - loopCal.get(Calendar.DAY_OF_WEEK) - 1) % 7) + 1);

    	boolean firstGenerated = true;
		do {
			loopCal.add(Calendar.DAY_OF_MONTH, 7);
			if (loopCal.before(endOfRun)) {
				visitTrans.add(new BdgtTranInfoVisitor(spec, loopCal, firstGenerated));
			}

			firstGenerated = false;
		} while (loopCal.before(endOfRun));
    }
    
    private List<BdgtTranInfoVisitor> calculateBalances(List<BdgtTranInfoVisitor> visitTrans, boolean history) {

    	BigDecimal lastBal = new BigDecimal(0);
    	boolean clearedTranOnlySoFar = true;
    	BdgtTranInfoVisitor lastClearedTran = null;
    	List<BdgtTranInfoVisitor> returnTrans = new ArrayList<BdgtTranInfoVisitor>(visitTrans.size());
    	for (BdgtTranInfoVisitor tran : visitTrans) {
    		if (tran.getType().equals(BdgtTranSpecInfo.Type.CRDT)) {
        		lastBal = lastBal.add(tran.getValue());
    		}
    		else {
        		lastBal = lastBal.subtract(tran.getValue());
    		}
    		tran.setSubTotal(lastBal);

    		if (!history && clearedTranOnlySoFar && tran.getState().equals(BdgtTranInfo.State.Cleared)) {
    			lastClearedTran = tran;
    		}
    		else {
    			if (lastClearedTran != null) {
    				returnTrans.add(lastClearedTran);
    				lastClearedTran = null;
    			}
				returnTrans.add(tran);
				clearedTranOnlySoFar = false;
    		}
    	}
    	
    	return returnTrans;
    }
}
