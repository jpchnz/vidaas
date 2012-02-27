package uk.ac.ox.oucs.vidaas.session;

import java.io.FileNotFoundException;

import javax.xml.bind.JAXBException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import uk.ac.ox.oucs.vidaas.session.generated.HelpField;

@Name("helpController")
@Scope(ScopeType.SESSION)
public class HelpController {
	private HelpTextGenerator helpTextGenerator;
	private String helpId = null;
	
	public HelpController() {
		try {
			helpTextGenerator = new HelpTextGenerator();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (JAXBException e) {
			e.printStackTrace();
		}
	}
	
	public String setupHelpText(String helpId) {
		if (helpTextGenerator == null) {
			return "No help available";
		}
		HelpField helpField = helpTextGenerator.getHelpField(helpId);
		if (helpField == null) {
			return "No help available";
		}
		this.helpId = helpId;
		return helpField.getText();
	}

	public String getHelpText() {
		if ( (helpId == null) || (helpTextGenerator == null) ) {
			return "No help available";
		}
		HelpField helpField = helpTextGenerator.getHelpField(helpId);
		if (helpField == null) {
			return "No help available";
		}
		return helpField.getText();
	}
	
	public String getHelpTitle(String helpId) {
		if (helpTextGenerator == null) {
			return "No help available";
		}
		HelpField helpField = helpTextGenerator.getHelpField(helpId);
		if (helpField == null) {
			return "No help available";
		}
		this.helpId = helpId;
		return helpField.getTitle();
	}

	public void setHelpId(String helpId) {
		this.helpId = helpId;
	}
}
