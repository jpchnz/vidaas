package uk.ac.ox.oucs.vidaas.session;

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
//        if (true) {
//            helpTextGenerator = null;
//            return;
//        }
        try {
            helpTextGenerator = new HelpTextGenerator();
        }
        catch (JAXBException e) {
            e.printStackTrace();
        }
    }
    
    
    /**
     * @param helpId the id of the required help
     * @return the associated helptext
     */
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

    /**
     * @return helptext for previously setup id
     */
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
    
    /**
     * @param helpId the id of the help required
     * @return the title of the help for the specified id
     */
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

    
    /**
     * Set the help id for future use
     * @param helpId
     */
    public void setHelpId(String helpId) {
        this.helpId = helpId;
    }
}
