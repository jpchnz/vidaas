package uk.ac.ox.oucs.vidaasBilling.utilities;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

public class HibernateUtil {
	private static SessionFactory sessionFactory;
	private static ServiceRegistry serviceRegistry;

//	private static SessionFactory configureSessionFactory() throws HibernateException {
//	    Configuration configuration = new Configuration();
//	    configuration.configure();
//	    serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();        
//	    sessionFactory = configuration.buildSessionFactory(serviceRegistry);
//	    return sessionFactory;
//	}
	private static Logger log = Logger.getLogger(HibernateUtil.class);
//	private static final SessionFactory sessionFactory;

	static {
		log.debug("init of HibernateUtil");
		try {
			// Create the SessionFactory
			Configuration configuration = new Configuration();
		    configuration.configure();
			serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();        
			sessionFactory = configuration.buildSessionFactory(serviceRegistry);
		} catch (Throwable ex) {
			// Make sure you log the exception, as it might be swallowed
			log.error("Initial SessionFactory creation failed: "
					+ ex.getMessage());
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static final ThreadLocal hibernateSession = new ThreadLocal();

	public static Session currentSession() {
//		log.debug("currentSession");
		if (sessionFactory != null) {
			return sessionFactory.getCurrentSession();
		}
		Session s = (Session) hibernateSession.get();
		// Open a new Session, if this Thread has none yet
		if (s == null) {
//			s = sessionFactory.openSession();
			s = sessionFactory.getCurrentSession();
			hibernateSession.set(s);
		}
		return s;
	}

	public static void closeSession() {
		Session s = (Session) hibernateSession.get();
		if (s != null)
			s.close();
		hibernateSession.set(null);
	}
}
