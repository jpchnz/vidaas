package uk.ac.ox.oucs.vidaas.session;

import java.util.Date;

import org.jboss.seam.log.Log;

import uk.ac.ox.oucs.vidaas.entity.Project;
import uk.ac.ox.oucs.vidaas.entity.Users;
import uk.ac.ox.oucs.vidaas.entity.Dataspace;
import uk.ac.ox.oucs.vidaas.utility.StringUtility;

import uk.ac.ox.oucs.vidaas.dao.DataspaceHome;

public class CreateDataSpaceController {

	public void createDataSpace(Users userMain, Project parentProject,
			DataspaceHome dataspaceHome, Date creationDate, Log log) {

		Dataspace tempDataSpace = dataspaceHome.getInstance();
		tempDataSpace.setDataspaceName(StringUtility.stringValidation(tempDataSpace.getDataspaceUserFriendlyName()));
		tempDataSpace.setCreationDate(creationDate);
		tempDataSpace.setUsers(userMain);
		tempDataSpace.setProject(parentProject);
		String dataspacePersistString = dataspaceHome.persist();
		log.info("dataspacePersistString {0}", dataspacePersistString);
	}

}
