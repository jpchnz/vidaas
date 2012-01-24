package uk.ac.ox.oucs.vidaas.session;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

//import uk.ac.ox.oucs.vidaas.session.generated.HelpField;

//@Name("helpController")
//@Scope(ScopeType.SESSION)
public class HelpController {
//	private HelpTextGenerator helpTextGenerator;
//	private String helpId = null;
//	
//	public HelpController() {
//		try {
//			helpTextGenerator = new HelpTextGenerator();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	
	public String setupHelpText(String helpId) {
//		HelpField helpField = helpTextGenerator.getHelpField(helpId);
//		if (helpField == null) {
//			return "No help available";
//		}
//		this.helpId = helpId;
//		return helpField.getText();
		return "Here is some help text for id " + helpId;
	}
//
//	public String getHelpText() {
//		if (helpId == null) {
//			return "No help available";
//		}
//		HelpField helpField = helpTextGenerator.getHelpField(helpId);
//		return helpField.getText();
//	}
//
//	public void setHelpId(String helpId) {
//		this.helpId = helpId;
//	}

}
