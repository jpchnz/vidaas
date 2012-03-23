package uk.ac.ox.oucs.vidaasBilling.utilities;

import org.hibernate.*;
import org.hibernate.cfg.*;

public class HibernateUtil {
	private static final SessionFactory sessionFactory;

	static {
		try {
			// Create the SessionFactory
			sessionFactory = new Configuration().configure()
					.buildSessionFactory();
		} catch (Throwable ex) {
			// Make sure you log the exception, as it might be swallowed
			System.out.println("Initial SessionFactory creation failed: "
					+ ex.getMessage());
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static final ThreadLocal hibernateSession = new ThreadLocal();

	public static Session currentSession() {
		Session s = (Session) hibernateSession.get();
		// Open a new Session, if this Thread has none yet
		if (s == null) {
			s = sessionFactory.openSession();
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
