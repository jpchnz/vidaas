package uk.ac.ox.oucs.iam.servlet;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;



public class AccessDB {
	private static Logger log = Logger.getLogger(AccessDB.class);
	
	public static uk.ac.ox.oucs.iam.servlet.model.IamPublicKey getKey(String uuid) {
		log.debug("Get Key:" + uuid);
		Transaction tx = null;
		Session session = HibernateUtil.currentSession();
		try {
			tx = session.beginTransaction();
			List keys = session.createSQLQuery("select * from iamPublicKey where uuid=:uuid")
					.addEntity(uk.ac.ox.oucs.iam.servlet.model.IamPublicKey.class).setParameter("uuid", uuid).list();
			tx.commit();
			if ( (keys != null) && (keys.size() != 0) ) {
				return (uk.ac.ox.oucs.iam.servlet.model.IamPublicKey) keys.get(0);
			}
		} catch (RuntimeException e) {
			log.error("Run time exception");
			log.error(e);
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
	
	public static boolean create(uk.ac.ox.oucs.iam.servlet.model.IamPublicKey o) {
		log.debug("Create key");
		Transaction tx = null;
		Session session = HibernateUtil.currentSession();
		try {
			tx = session.beginTransaction();
			log.debug("tx started");
			session.save(o);
			log.debug("saved");
			tx.commit();
			log.debug("returning");
			return true;
		} catch (RuntimeException e) {
			log.error("Exception while trying to create key:", e);
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
		return false;
	}
}
