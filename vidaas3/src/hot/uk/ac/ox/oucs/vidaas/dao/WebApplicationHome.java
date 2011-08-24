package uk.ac.ox.oucs.vidaas.dao;

import uk.ac.ox.oucs.vidaas.entity.*;
import java.util.ArrayList;
import java.util.List;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("webApplicationHome")
public class WebApplicationHome extends EntityHome<WebApplication> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8205164329954147910L;

	public void setWebApplicationWebId(Integer id) {
		setId(id);
	}

	public Integer getWebApplicationWebId() {
		return (Integer) getId();
	}

	@Override
	protected WebApplication createInstance() {
		WebApplication webApplication = new WebApplication();
		return webApplication;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
	}

	public boolean isWired() {
		return true;
	}

	public WebApplication getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<ProjectDatabase> getProjectDatabases() {
		return getInstance() == null ? null : new ArrayList<ProjectDatabase>(
				getInstance().getProjectDatabases());
	}

}
