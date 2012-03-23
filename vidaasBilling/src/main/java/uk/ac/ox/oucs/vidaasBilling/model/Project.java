package uk.ac.ox.oucs.vidaasBilling.model;

import java.io.FileNotFoundException;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;

import uk.ac.ox.oucs.vidaasBilling.servlet.Billing;
import uk.ac.ox.oucs.vidaasBilling.utilities.Emailer;

public class Project implements java.io.Serializable {
	private static Logger log = Logger.getLogger(Project.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int id;
	public static enum BackupPolicy { everyDay, everyWeek, everyFortnight, everyMonth };
	private int projectSpace, projectId;
	private String ownerEmail;
	private String projectName;
	public static enum BillingFrequency { monthly, annually, fiveYearly }; 
	private String billingFrequency;
	private int dayLastTimeBilled = 0, monthLastTimeBilled = 0, yearLastTimeBilled = 0;
	
	
	
	public Project() {}
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getProjectSpace() {
		return projectSpace;
	}
	public void setProjectSpace(int projectSpace) {
		this.projectSpace = projectSpace;
	}
	public String getOwnerEmail() {
		return ownerEmail;
	}
	public void setOwnerEmail(String ownerEmail) {
		this.ownerEmail = ownerEmail;
	}


	public String getProjectName() {
		return projectName;
	}


	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}


	public String getBillingFrequency() {
		return billingFrequency;
	}


	public void setBillingFrequency(String billingFrequency) {
		this.billingFrequency = billingFrequency;
	}



	/**
	 * Create an invoice of the bill for a specific project
	 * @return the final cost of the bill
	 * @throws FileNotFoundException
	 * @throws JAXBException
	 */
	public int emailBill() throws FileNotFoundException, JAXBException {
		String emailBody = "";
		int invoicedAmount = Billing.getInstance().calculateCost(getProjectSpace());
		emailBody += String.format("Dear %s\n\n", getOwnerEmail());
		emailBody += String.format("This is a breakdown of your invoice for project %s.\n\n", getProjectName());
		emailBody += String.format("Project disk space reserved: %s\n", getProjectSpace());
		emailBody += String.format("Cost : %s\n", invoicedAmount);
		
		Emailer emailer = new Emailer();
		String recipient;
		if (uk.ac.ox.oucs.vidaasBilling.utilities.SystemVars.USE_TEST_EMAIL_ADDRESS) {
			recipient = "thestoat@gmail.com";
		}
		else {
			recipient = getOwnerEmail();
		}
		if (false) {
			// Can't connect to the smtp from the University
			emailer.sendEmail("dave@thepaines.com",
					recipient, "A bill for you", emailBody);
		}
		
		if (log.isDebugEnabled()) {
			log.debug("Sending the following email");
			log.debug(emailBody);
		}
				
		return invoicedAmount;
	}


	public int getDayLastTimeBilled() {
		return dayLastTimeBilled;
	}


	public void setDayLastTimeBilled(int dayLastTimeBilled) {
		this.dayLastTimeBilled = dayLastTimeBilled;
	}


	public int getMonthLastTimeBilled() {
		return monthLastTimeBilled;
	}


	public void setMonthLastTimeBilled(int monthLastTimeBilled) {
		this.monthLastTimeBilled = monthLastTimeBilled;
	}


	public int getYearLastTimeBilled() {
		return yearLastTimeBilled;
	}


	public void setYearLastTimeBilled(int yearLastTimeBilled) {
		this.yearLastTimeBilled = yearLastTimeBilled;
	}


	public int getProjectId() {
		return projectId;
	}


	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}
}
