package uk.ac.ox.oucs.vidaasBilling.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import uk.ac.ox.oucs.vidaasBilling.model.Invoice;
import uk.ac.ox.oucs.vidaasBilling.model.Project;
import uk.ac.ox.oucs.vidaasBilling.servlet.generated.BillingPolicy;
import uk.ac.ox.oucs.vidaasBilling.servlet.generated.Costing;
import uk.ac.ox.oucs.vidaasBilling.servlet.generated.PerProjectDatabaseCost;
import uk.ac.ox.oucs.vidaasBilling.utilities.HibernateUtil;

public class Billing {
	private InputStream inputStream;
	private boolean billingEnabled;
	private BillingPolicy billingPolicy;
	private VidaasWorkspace vidaasWorkspace = new VidaasWorkspace();
	private static Billing instance = null;

	public static Billing getInstance() throws FileNotFoundException, JAXBException {
		if (instance == null) {
			instance = new Billing();
		}
		
		return instance;
	}

	private Billing() throws JAXBException, FileNotFoundException {
		billingEnabled = loadBillingFile();
		if (billingEnabled) {
			init();
		}
		else {
			System.out.println("Unable to prepare billing system");
		}
	}

	
	public int calculateCost(int projectSpace) {
		int perProjectCost = billingPolicy.getVidaasUser().getProjectCost().getStandardProjectCost().intValue();
		PerProjectDatabaseCost ppdc = billingPolicy.getVidaasUser().getProjectCost().getProjectDatabaseCost().getPerProjectDatabaseCost();
		int diskSpaceUsageCost = 0;
		List<Costing> costing = ppdc.getCosting();
		for (Costing c : costing) {
			if (c.getSizeInGig().intValue() == projectSpace) {
				diskSpaceUsageCost = c.getCost().intValue();
			}
		}
		return diskSpaceUsageCost+perProjectCost;
	}
	
	private boolean loadBillingFile() throws FileNotFoundException {
		boolean ret = true;
		inputStream = BillingPolicy.class.getClassLoader().getResourceAsStream(
				"billingPolicy.xml");
		if (inputStream == null) {
			System.out.println("Unable to get help file resource");
			if (true)
				return false;
			File f = new File("src/main/billingPolicy.xml");
			System.out.println(f.getAbsolutePath());
			inputStream = new FileInputStream(f);
			if (inputStream == null) {
				System.out.println("Still unable to get help file resource");
				ret = false;
			}
		}
		return ret;
	}

	/**
	 * Initialise the help text object using JAXB
	 * 
	 * @throws JAXBException
	 */
	private void init() throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(BillingPolicy.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		billingPolicy = (BillingPolicy) jaxbUnmarshaller.unmarshal(inputStream);
	}

	public void addDatabaseForUser(String email) {

	}

	public void addProjectForUser(String email, String projectName) {

	}

	public int calculateProjectCost(int numberOfProjects) {
		int cost = 0;

		cost = billingPolicy.getVidaasUser().getProjectCost()
				.getStandardProjectCost().intValue()
				* numberOfProjects;

		return cost;
	}
	
	public static Project getProject(String ownerEmail) {
		Transaction tx = null;
		Session session = HibernateUtil.currentSession();
		try {
			tx = session.beginTransaction();
			List projects = session.createSQLQuery("select * from project")
					.addEntity(Project.class).list();
			for (Iterator iter = projects.iterator(); iter.hasNext();) {
				Project element = (Project) iter.next();
				if (element.getOwnerEmail().compareTo(ownerEmail) == 0) {
					return element;
				}
			}
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				try {
					tx.rollback();
				} catch (HibernateException e1) {
				}
				throw e;
			}
		}

		return null;
	}
	
	public static void create(Project project) {
		Transaction tx = null;
		Session session = HibernateUtil.currentSession();
		try {
			tx = session.beginTransaction();
			session.save(project);
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				try {
					// Second try catch as the rollback could fail as well
					tx.rollback();
				} catch (HibernateException e1) {
				}
				// throw again the first exception
				throw e;
			}
		}
	}
	
	public static void create(Invoice invoice) {
		Transaction tx = null;
		Session session = HibernateUtil.currentSession();
		try {
			tx = session.beginTransaction();
			session.save(invoice);
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				try {
					// Second try catch as the rollback could fail as well
					tx.rollback();
				} catch (HibernateException e1) {
				}
				// throw again the first exception
				throw e;
			}
		}
	}

	public static void update(Project project) {
		Transaction tx = null;
		Session session = HibernateUtil.currentSession();
		try {
			tx = session.beginTransaction();
			session.update(project);
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				try {
					// Second try catch as the rollback could fail as well
					tx.rollback();
				} catch (HibernateException e1) {
					// logger.debug(“Error rolling back transaction”);
				}
				// throw again the first exception
				throw e;
			}
		}
	}
	
	public static List<Project> getProjects() {
		Transaction tx = null;
		Session session = HibernateUtil.currentSession();
		try {
			tx = session.beginTransaction();
			List projects = session.createSQLQuery("select * from project").addEntity(Project.class).list();
			tx.commit();
			return projects;
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				try {
					tx.rollback();
				} catch (HibernateException e1) {
				}
				throw e;
			}

		}
		return null;
	}
	
		

	public boolean isBillingEnabled() {
		return billingEnabled;
	}

	public static void main(String args[]) throws JAXBException,
			FileNotFoundException {
		Billing b = new Billing();

		System.out.println("Cost:" + b.calculateProjectCost(4));

//		User u = new User();
//		u.setEmail("a@a");
//		u.setNumberOfProjects(1);
//		updateUser(u);
		System.out.println("Done");
	}
}
