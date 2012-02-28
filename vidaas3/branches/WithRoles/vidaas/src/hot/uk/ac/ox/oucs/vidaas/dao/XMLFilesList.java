package uk.ac.ox.oucs.vidaas.dao;

import uk.ac.ox.oucs.vidaas.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("xmlFilesList")
public class XMLFilesList extends EntityQuery<XMLFiles> {

	private static final long serialVersionUID = -7878488490814405340L;

	private static final String EJBQL = "select xmlFiles from XmlFiles xmlFiles";

	private static final String[] RESTRICTIONS = {"lower(xmlFiles.fileName) like lower(concat(#{xmlFilesList.xmlFiles.fileName},'%'))",};

	private XMLFiles xmlFiles = new XMLFiles();

	public XMLFilesList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public XMLFiles getXmlFiles() {
		return xmlFiles;
	}
}
