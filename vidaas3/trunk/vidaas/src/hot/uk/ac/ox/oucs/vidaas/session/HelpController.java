package uk.ac.ox.oucs.vidaas.session;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("helpController")
@Scope(ScopeType.SESSION)
public class HelpController {
	private String helpText = "Here is today's help";
	
	public String setupHelpText(String id) {
		helpText = id;
		return helpText;
	}

	public String getHelpText() {
		return helpText;
	}

	public void setHelpText(String helpText) {
		this.helpText = helpText;
	}

}
