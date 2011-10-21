package uk.ac.ox.oucs.vidaas.dao;

import uk.ac.ox.oucs.vidaas.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("webApplicationList")
public class WebApplicationList extends EntityQuery<WebApplication> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1417537320733630914L;

	private static final String EJBQL = "select webApplication from WebApplication webApplication";

	private static final String[] RESTRICTIONS = { "lower(webApplication.url) like lower(concat(#{webApplicationList.webApplication.url},'%'))", };

	private WebApplication webApplication = new WebApplication();

	public WebApplicationList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public WebApplication getWebApplication() {
		return webApplication;
	}
}
