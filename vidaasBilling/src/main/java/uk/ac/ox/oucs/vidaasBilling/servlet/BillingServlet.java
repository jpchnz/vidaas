package uk.ac.ox.oucs.vidaasBilling.servlet;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;

import uk.ac.ox.oucs.iam.interfaces.security.ReceivePostedData;
import uk.ac.ox.oucs.iam.interfaces.security.SecurePostData;
import uk.ac.ox.oucs.iam.interfaces.security.SendViaPost;
import uk.ac.ox.oucs.iam.interfaces.utilities.SystemVars;
import uk.ac.ox.oucs.vidaasBilling.model.Invoice;
import uk.ac.ox.oucs.vidaasBilling.model.Project;
import uk.ac.ox.oucs.vidaasBilling.model.Project.BillingFrequency;

@SuppressWarnings("serial")
public class BillingServlet extends HttpServlet implements Serializable {
	private static Logger log = Logger.getLogger(BillingServlet.class);
//	private PrintWriter out;
	private Billing billing = null;

	public BillingServlet() throws JAXBException, FileNotFoundException {
		billing = Billing.getInstance();
	}


	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		log.debug("doGet starting ...");
		
//		out = response.getWriter();
//		out.println("Billing subsystem is " + (billing.isBillingEnabled() ? "on" : "off"));
		
		if (log.isDebugEnabled()) {
			Map params = request.getParameterMap();
			Iterator i = params.keySet().iterator();
			log.debug(String.format("Logging %d input parameters", request.getParameterMap().size()));
			while (i.hasNext()) {
				String key = (String) i.next();
				String value = ((String[]) params.get(key))[0];
				log.debug(key + "=" + value);
			}
			log.debug("Input parameters logged");
		}

		String command = request.getParameter(SystemVars.POST_COMMAND_COMMAND_TOKEN);
		if (command == null) {
			log.error("Badly formed post input - sorry it didn't work out");
			return;
		}
		if (command.compareToIgnoreCase("status") == 0) {
//			out.println("All well, master");
		}
		else if (command.compareToIgnoreCase(SystemVars.POST_COMMAND_NEW_DATA_AVAILABLE) == 0) {
			/*
			 * This will be sent from the secure post service. What happens is that when a new project is created, it 
			 * tries to tell this servlet. But it cannot access this servlet directly, so it tells the iam webapp
			 * (that houses a secure post module). That module will process the data and, if it determines the data has passed
			 * security muster, will call this servlet and the execution will end up here.
			 */
			log.info("New data available to collect. Yay");
			addBillingForNewproject();
//			out.println("Sorted");
//			generateAndSendInvoices();
		}
		else if (command.compareToIgnoreCase(SystemVars.POST_COMMAND_NEW_PROJECT) == 0) {
			addBillingForNewproject();
		}
		else if (command.compareToIgnoreCase(SystemVars.POST_COMMAND_UPDATE_PROJECT) == 0) {
			changeBillingForUpdatedProject(request);
		}
		else if (command.compareToIgnoreCase(SystemVars.POST_COMMAND_GENERATE_INVOICES) == 0) {
			/*
			 * Call this when invoices need to be created. This will check
			 * through all projects in the database and, where appropriate,
			 * email invoices to users.
			 */
			log.debug("Generate invoices requested");

			generateAndSendInvoices();

			log.debug("Invoices generated");
		}

//		out.flush();
//		out.close();
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		log.debug("doPost");
		
		doGet(request, response);
	}
	
	
	private void changeBillingForUpdatedProject(HttpServletRequest request) throws FileNotFoundException {
		/*
		 * This needs to be executed when a project is updated (e.g. the
		 * size changes). Since it is currently not possible to update
		 * project details currently, I won't worry too much about it, but
		 * ultimately the project details will need to be updated in the
		 * database and then billing adjusted accordingly.
		 */
		log.debug("Update project requested");
		// ?u=a@a&c=updateProject&projectName=fred&space=100
		/*
		 * This is a command to update an existing project for billing
		 * purposes
		 */
		String email = request.getParameter("u");
		Project project = Billing.getProject(email);
		String projectName = request.getParameter("projectName");

		int projectSpace = Integer.parseInt(request.getParameter("space"));
		project.setProjectSpace(projectSpace);
		project.setProjectName(projectName);
		// project.setOwnerEmail(email);

		Billing.update(project);
		log.debug("Project updated");

		generateAndSendInvoices();
		log.debug("Invoices sent");
	}
	
	private void addBillingForNewproject() throws IOException {
		log.debug("New project requested");

		/*
		 * This will create a project in the database. Thus this method
		 * needs to be called whenever a project is created, but only once,
		 * since a second call will fail (there is a project id constraint
		 * on the table).
		 */
		List<SecurePostData> securePostDataList = ReceivePostedData.getPendingMessageDataAndClear();
		int counter = 0;
		if (securePostDataList.size() == 0) {
			log.info("No data with which to create a new project");
		}
		else {
			if (log.isInfoEnabled()) {
				log.info(String.format("We have %d items to sift through", securePostDataList.size()));
			}
			for (SecurePostData spd : securePostDataList) {
				if (spd.isMessageHasBeenVerified()) {
					// We can go ahead and add this to the database
					log.debug("Project Verified");

					Project p = new Project();
					p.setOwnerEmail(spd.getPostParms().get(SystemVars.POST_COMMAND_EMAIL_TOKEN));
					p.setProjectName(spd.getPostParms().get(SystemVars.POST_COMMAND_PROJECTNAME_TOKEN));
					p.setProjectId(Integer
							.parseInt(spd.getPostParms().get(SystemVars.POST_COMMAND_PROJECTID_TOKEN)));
					p.setProjectSpace(Integer.parseInt(spd.getPostParms().get(
							SystemVars.POST_COMMAND_PROJECTSPACE_TOKEN)));
					p.setBillingFrequency(spd.getPostParms().get(SystemVars.POST_COMMAND_BILLINGFREQUENCY_TOKEN));
					String billFreq = spd.getPostParms().get(SystemVars.POST_COMMAND_BILLINGFREQUENCY_TOKEN);
					if (log.isDebugEnabled()) {
						log.debug("Project name:" + p.getProjectName());
						log.debug("Project id:" + p.getProjectId());
						log.debug("Project billing frequency:" + p.getBillingFrequency());
						log.debug("Project space:" + p.getProjectSpace());
						log.debug("Project owner email:" + p.getOwnerEmail());
						log.debug("Project day last billed:" + p.getProjectName());
						log.debug("Project month last billed:" + p.getMonthLastTimeBilled());
						log.debug("Project year last billed:" + p.getYearLastTimeBilled());
					}
					if (billFreq == null) {
						p.setBillingFrequency(BillingFrequency.annually.toString());
					}
					else {
						if (billFreq.compareToIgnoreCase(BillingFrequency.fiveYearly.toString()) == 0) {
							p.setBillingFrequency(BillingFrequency.fiveYearly.toString());
						}
						else if (billFreq.compareToIgnoreCase(BillingFrequency.annually.toString()) == 0) {
							p.setBillingFrequency(BillingFrequency.annually.toString());
						}
						else {
							p.setBillingFrequency(BillingFrequency.monthly.toString());
						}
					}

					if (log.isInfoEnabled()) {
						log.info(String
								.format("About to create a new project %s in the database with space %d and owner email %s, Billing frequency is %s",
										p.getProjectName(), p.getProjectSpace(), p.getOwnerEmail(),
										p.getBillingFrequency()));
					}

					Billing.create(p);
				}
				else {
					log.info("Project not verified");
				}
//				out.println("Item " + (counter + 1));
//				out.println("Originator for data " + (counter + 1) + " = " + spd.getOriginatorHost());
//				out.println("Timeout = " + spd.isMessageTimedOut());
//				out.println("Verified = " + spd.isMessageHasBeenVerified());
//				out.println("Bad sig = " + spd.isBadSig());
//				for (String s : spd.getPostParms().keySet()) {
//					out.println("\t" + s + "=" + spd.getPostParms().get(s));
//				}
				counter++;
			}
			
//			generateAndSendInvoices();
			log.debug("Invoices sent");
		}
	}

	
	/**
	 * Look through all projects defined in the database and generate invoices for each of those that are due.
	 * Currently, we do not check for a valid message before generating and sending invoices, so in
	 * theory, anybody can do this. However, since the only invoices that are sent out are invoices that should be
	 * sent out, this should not matter. In practice, this routing should be called once a day to ensure 
	 * current invoices are sent.
	 */
	private void generateAndSendInvoices() throws FileNotFoundException {
		log.debug("generateAndSendInvoices");

		/*
		 * This is a command to generate all invoices
		 */
		List<Project> projects = Billing.getProjects();
		if (log.isDebugEnabled()) {
			log.debug("Currently have " + projects.size() + " projects.");
		}

		/*
		 * For now, let's generate a different invoice per project. This is
		 * useful to do since different projects may be paid for by different
		 * grants. Currently we'll send an email about this.
		 */

		try {
			for (Project p : projects) {
				boolean billNeeded = false;
				if (log.isDebugEnabled()) {
					log.debug("Checking ...");
					log.debug("\tProject name:" + p.getProjectName());
					log.debug("\tProject id:" + p.getProjectId());
					log.debug("\tProject billing frequency:" + p.getBillingFrequency());
					log.debug("\tProject space:" + p.getProjectSpace());
					log.debug("\tProject owner email:" + p.getOwnerEmail());
					log.debug("\tProject day last billed:" + p.getProjectName());
					log.debug("\tProject month last billed:" + p.getMonthLastTimeBilled());
					log.debug("\tProject year last billed:" + p.getYearLastTimeBilled());
				}
				Calendar cal = Calendar.getInstance();
				int month = cal.get(Calendar.MONTH) + 1;
				int day = cal.get(Calendar.DAY_OF_MONTH);
				int year = cal.get(Calendar.YEAR);

				if (p.getDayLastTimeBilled() == 0) {
					/*
					 * The project has never had an invoice generated. Let's send one now.
					 */
					billNeeded = true;
				}
				else {
					/*
					 * An invoice has been generated for this project. We need to find out
					 * if another one is due or not.
					 */
					if (log.isDebugEnabled()) {
						log.debug(String.format("Date is %d:%d:%d", day, month, year));
					}

					if (p.getBillingFrequency().compareTo(BillingFrequency.annually.toString()) == 0) {
						// Annual billing
						if (year > (p.getYearLastTimeBilled() + 1)) {
							billNeeded = true;
						}
						else if (year > p.getYearLastTimeBilled()) {
							if (month >= p.getMonthLastTimeBilled()) {
								if (day >= p.getDayLastTimeBilled()) {
									/*
									 * If we get here, the last bill was generated more than a year ago.
									 */
									billNeeded = true;
								}
							}
						}
					}
					else if (p.getBillingFrequency().compareTo(BillingFrequency.fiveYearly.toString()) == 0) {
						// Five yearly billing
						if (year > (p.getYearLastTimeBilled() + 6)) {
							/*
							 * If we get here, the last bill was generated more than 5 years ago.
							 */
							billNeeded = true;
						}
						else if (year > (p.getYearLastTimeBilled() + 5)) {
							if (month >= p.getMonthLastTimeBilled()) {
								if (day >= p.getDayLastTimeBilled()) {
									/*
									 * If we get here, the last bill was generated more than 5 years ago.
									 */
									billNeeded = true;
								}
							}
						}
					}
					else {
						// Assume monthly billing
						if (p.getMonthLastTimeBilled() < month) {
							// Next month - just need to check the day is ok
							// too
							if (p.getDayLastTimeBilled() <= day) {
								/*
								 * If we get here, the last bill was generated more than a month ago.
								 */
								billNeeded = true;
							}
						}
						else if ((p.getMonthLastTimeBilled() == 12) // December
								&& (year > p.getYearLastTimeBilled()) // New
																		// year
						) {
							if ((month == 1) // January
									&& (day >= p.getDayLastTimeBilled())) {
								/*
								 * If we get here, the last bill was generated more than a month ago.
								 */
								billNeeded = true;
							}
							else if (month > 1) {
								/*
								 * If we get here, the last bill was generated more than a month ago.
								 */
								billNeeded = true;
							}
						}
					}
				}

				if (billNeeded
						|| uk.ac.ox.oucs.vidaasBilling.utilities.SystemVars.GENERATE_INVOICES_REGARDLESS_OF_TIME_ELAPSED) {
					/*
					 * Either a new invoice is genuinely needed or we are testing! 
					 */
					if (log.isDebugEnabled()) {
						log.debug("We need to send a bill for this");
						log.debug("Because "
								+ billNeeded
								+ " || "
								+ (uk.ac.ox.oucs.vidaasBilling.utilities.SystemVars.GENERATE_INVOICES_REGARDLESS_OF_TIME_ELAPSED)
								+ " is true");
					}
					int invoicedAmount = p.emailBill();
					if (log.isInfoEnabled()) {
						log.info("Will send an invoice to " + p.getOwnerEmail() + " for project " + p.getProjectName());
						log.info("Invoiced amount:" + invoicedAmount);
					}
					p.setYearLastTimeBilled(year);
					p.setMonthLastTimeBilled(month);
					p.setDayLastTimeBilled(day);
					/*
					 * Update the project with the last billed date
					 */
					Billing.update(p);
					log.debug("Project db entry updated");

					/*
					 * We should now create a record of the invoice
					 */
					Invoice invoice = new Invoice();
					invoice.setRecipientEmail(p.getOwnerEmail());
					invoice.setProjectId(p.getProjectId());
					invoice.setInvoiceDate(new Date());
					invoice.setInvoicedAmount(invoicedAmount);
					Billing.create(invoice);
					log.debug("Billing entry created");
					
					/*
					 * Finally, send the invoice
					 */
					
				}
			}
		}
		catch (JAXBException e) {
			log.error("Unable to generate invoices. This is very bad. Exception likely suggests misconfiguration of web app");
			log.error(e);
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
//			// Send test email
//			Emailer emailer = new Emailer();
//			emailer.sendEmail("david.paine@oucs.oc.ac.uk", "thestoat@gmail.com", "Hi there", "Lovely body");
//			if (true) {
//				return;
//			}
			System.out.println("Send via post");
			SendViaPost post = new SendViaPost();
			for (int i = 0; i < 1; i++) {
				String email = "a@a";
				String projectName = "fred";
				int projectSize = 100;
				Random generator = new Random();
				int projectId = generator.nextInt(50000);
				
				// The following post will create a new project
				String r = post.sendSecurePost(
				// "http://129.67.241.38/iam/ReceivePost",
						//"http://82.71.34.134:8081/vidaasBilling/BillingServlet", 
//						"http://129.67.103.124:8081/vidaasBilling/BillingServlet",
						"http://localhost:8080/vidaasBilling/BillingServlet",
						String.format(
								"%s=%s&%s=%s&%s=%s&%s=%d&%s=%s&%s=%d", SystemVars.POST_COMMAND_EMAIL_TOKEN, email,
								SystemVars.POST_COMMAND_COMMAND_TOKEN, SystemVars.POST_COMMAND_NEW_PROJECT,
								SystemVars.POST_COMMAND_PROJECTNAME_TOKEN, projectName+projectId,
								SystemVars.POST_COMMAND_PROJECTSPACE_TOKEN, projectSize,
								SystemVars.POST_COMMAND_BILLINGFREQUENCY_TOKEN, BillingFrequency.monthly.toString(),
								SystemVars.POST_COMMAND_PROJECTID_TOKEN, projectId));
				System.out.println("Result:" + projectId + "\n" + r);
//				List<SecurePostData> securePostDataList = ReceivePostedData.getPendingMessageDataAndKeep();
//				int counter = 0;
//				for (SecurePostData spd : securePostDataList) {
//					System.out.println("Item " + (counter + 1));
//					System.out.println("Originator for data " + (counter + 1) + " = " + spd.getOriginatorHost());
//					System.out.println("Timeout = " + spd.isMessageTimedOut());
//					System.out.println("Verified = " + spd.isMessageHasBeenVerified());
//					System.out.println("Bad sig = " + spd.isBadSig());
//					System.out.println("Bad priv key = " + spd.isNoPrivateKey());
//					
//					for (String s : spd.getPostParms().keySet()) {
//						System.out.println("\t" + s + "=" + spd.getPostParms().get(s));
//					}
//
//					// if (spd.isMessageHasBeenVerified()) {
//					// // We can go ahead and add this to the databases
//					// Project p = new Project();
//					// p.setOwnerEmail(spd.getPostParms().get(SystemVars.POST_COMMAND_EMAIL_TOKEN));
//					// p.setProjectName(spd.getPostParms().get(SystemVars.POST_COMMAND_PROJECTNAME_TOKEN));
//					// p.setProjectId(Integer
//					// .parseInt(spd.getPostParms().get(SystemVars.POST_COMMAND_PROJECTID_TOKEN)));
//					// p.setProjectSpace(Integer.parseInt(spd.getPostParms().get(
//					// SystemVars.POST_COMMAND_PROJECTSPACE_TOKEN)));
//					// String billFreq =
//					// spd.getPostParms().get(SystemVars.POST_COMMAND_BILLINGFREQUENCY_TOKEN);
//					// if (billFreq == null) {
//					// p.setBillingFrequency(BillingFrequency.annually.toString());
//					// }
//					// else {
//					// if
//					// (billFreq.compareToIgnoreCase(BillingFrequency.fiveYearly.toString())
//					// == 0) {
//					// p.setBillingFrequency(BillingFrequency.fiveYearly.toString());
//					// }
//					// else if
//					// (billFreq.compareToIgnoreCase(BillingFrequency.annually.toString())
//					// == 0) {
//					// p.setBillingFrequency(BillingFrequency.annually.toString());
//					// }
//					// else {
//					// p.setBillingFrequency(BillingFrequency.monthly.toString());
//					// }
//					// }
//					// Billing.create(p);
//					// // for (String s : spd.getPostParms().keySet()) {
//					// //
//					// System.out.println("\t"+s+"="+spd.getPostParms().get(s));
//					// // }
//					// }
//					counter++;
//				}
//				Thread.sleep(2000);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
