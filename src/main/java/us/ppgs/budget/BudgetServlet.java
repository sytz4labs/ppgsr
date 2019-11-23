package us.ppgs.budget;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import us.ppgs.budget.dao.BdgtDao;
import us.ppgs.budget.info.BdgtTranInfo;
import us.ppgs.budget.info.BdgtTranSpecInfo;
import us.ppgs.budget.info.TransData;
import us.ppgs.config.dao.ConfigException;

@Controller
@RequestMapping("/budget")
public class BudgetServlet {
	
	@Autowired
	private BdgtDao bdgtDao;

	@Autowired
	private BudgetModel bdgtMdl;
	
	@RequestMapping("")
	public String indexb() {
		return "redirect:/budget/";
	}

	@RequestMapping("/")
	public String indexSlash() {
		return "budget/index";
	}

    @PreAuthorize("hasRole('ADMIN')")
	@RequestMapping("/trans")
    @ResponseBody
	public TransData tranSpec() {
		
		List<BdgtTranSpecInfo> specs = bdgtDao.getTranSpecs();
		return new TransData(specs, bdgtMdl.getTrans(specs, false), new Date());
    }

    @PreAuthorize("hasRole('ADMIN')")
	@RequestMapping("history")
	@ResponseBody
	public TransData history() {

		List<BdgtTranSpecInfo> specs = bdgtDao.getTranSpecs();
		return new TransData(bdgtMdl.getTrans(specs, true));
    }

	private static SimpleDateFormat f = new SimpleDateFormat("MM-dd-yyyy");

    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    @RequestMapping(value="/budgetHandler", method=RequestMethod.POST)
	public TransData editHandler(@RequestBody NVRequest r) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, ConfigException, ParseException {

		if (r.getName().equals("newTran")) {
			bdgtDao.newTran(f.parse(r.getValue()));
		}
		else if (r.getName().equals("addTranSpec")) {
			bdgtDao.newTranspec(r.getValue());
		}
		else if (r.getName().equals("setTran")) {
			String[] values = r.getValue().split("\\|");
			bdgtDao.setTran(Integer.parseInt(values[0]), f.parse(values[1]));
		}
		else if (r.getName().equals("schedTran")) {
			bdgtDao.update("t.state", Integer.parseInt(r.getValue()), BdgtTranInfo.State.Scheduled.toString());
		}
		else if (r.getName().equals("delTran")) {
			bdgtDao.delTran(Integer.parseInt(r.getValue()));
		}
		else if (r.getName().equals("clrTran")) {
			bdgtDao.update("t.state", Integer.parseInt(r.getValue()), BdgtTranInfo.State.Cleared.toString());
		}
		else {
			try {
				String[] subNames = r.getName().split("\\|");
				int id = Integer.parseInt(subNames[1]);
				if (subNames[0].equals("t.date")) {
					bdgtDao.update(subNames[0], id, f.parse(r.getValue()));
				}
				else {
					bdgtDao.update(subNames[0], id, r.getValue());
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
				
		return tranSpec();
	}
}

