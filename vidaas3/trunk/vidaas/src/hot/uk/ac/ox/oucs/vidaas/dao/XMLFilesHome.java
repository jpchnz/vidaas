package uk.ac.ox.oucs.vidaas.dao;

import java.util.List;

import javax.persistence.Query;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

import uk.ac.ox.oucs.vidaas.entity.Dataspace;
import uk.ac.ox.oucs.vidaas.entity.ProjectDatabase;
import uk.ac.ox.oucs.vidaas.entity.Users;
import uk.ac.ox.oucs.vidaas.entity.XMLFiles;


@Name("xmlFilesHome")
@SuppressWarnings("unchecked")
public class XMLFilesHome extends EntityHome<XMLFiles>{

	@In(create = true)
	ProjectDatabaseHome projectDatabaseHome;
	@In(create = true)
	UsersHome usersHome;

	public void setXmlFilesFileId(Integer id) {
		setId(id);
	}

	public Integer getXmlFilesFileId() {
		return (Integer) getId();
	}

	@Override
	protected XMLFiles createInstance() {
		XMLFiles xmlFiles = new XMLFiles();
		return xmlFiles;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		ProjectDatabase projectDatabase = projectDatabaseHome.getDefinedInstance();
		if (projectDatabase != null) {
			getInstance().setProjectDatabase(projectDatabase);
		}
		Users users = usersHome.getDefinedInstance();
		if (users != null) {
			getInstance().setUsers(users);
		}
	}

	public boolean isWired() {
		if (getInstance().getProjectDatabase() == null)
			return false;
		if (getInstance().getUsers() == null)
			return false;
		return true;
	}

	public XMLFiles getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}
	
	public List<XMLFiles> findByDatabaseID(int databaseIDValue){
		Query query = this.getEntityManager().createNamedQuery("XMLFiles.findByDatabaseID");
		query.setParameter("databaseID", databaseIDValue);		
		List<XMLFiles> xmlFilesList = query.getResultList();
		return xmlFilesList;
	}
	
	public List<XMLFiles> findByFileID(int fileIDValue){
		Query query = this.getEntityManager().createNamedQuery("XMLFiles.findByFileID");
		query.setParameter("fileId", fileIDValue);		
		List<XMLFiles> xmlFilesList = query.getResultList();
		return xmlFilesList;
	}
	
	public List<XMLFiles> findByFileName(String FileNameValue){
		Query query = this.getEntityManager().createNamedQuery("XMLFiles.findByFileName");
		query.setParameter("fileName", new String(FileNameValue));		
		List<XMLFiles> xmlFilesList = query.getResultList();
		return xmlFilesList;
	}
	
	public List<XMLFiles> findByFileNameAndDatabaseID(int databaseIDValue, String FileNameValue){
		Query query = this.getEntityManager().createNamedQuery("XMLFiles.findByFileNameAndDatabaseID");
		query.setParameter("databaseID", databaseIDValue);
		query.setParameter("fileName", new String(FileNameValue));		
		List<XMLFiles> xmlFilesList = query.getResultList();
		return xmlFilesList;
	}

}
