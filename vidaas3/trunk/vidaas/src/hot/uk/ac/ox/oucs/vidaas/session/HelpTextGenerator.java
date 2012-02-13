package uk.ac.ox.oucs.vidaas.session;

import java.io.InputStream;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import uk.ac.ox.oucs.vidaas.session.generated.HelpField;
import uk.ac.ox.oucs.vidaas.session.generated.HelpGroup;
import uk.ac.ox.oucs.vidaas.session.generated.HelpText;

/**
 * HelpTextGenerator prepares a HelpText object and populates it with data from the
 * htlpText.xml resource file. It is then able to provide help text for specific help ids. 
 * 
 * @author oucs0153
 *
 */
public class HelpTextGenerator {
	private HelpText helpText;
	private boolean helpEnabled = true;
	private InputStream inputStream;

	
	/**
	 * Prepare the help text generator.
	 * 
	 * @throws JAXBException
	 */
	public HelpTextGenerator() throws JAXBException {
		helpEnabled = loadHelpFile();
		if (helpEnabled) {
			init();
		}
		else {
			System.out.println("Unable to prepare in context help");
		}
	}

	/**
	 * Find the helpText xml file and prepare an input stream for this resource for JAXB.
	 * 
	 * @return true if was able to read the help text as a resource, else false
	 */
	private boolean loadHelpFile() {
		boolean ret = true;
		inputStream = HelpTextGenerator.class.getClassLoader().getResourceAsStream("helpText.xml");
		if (inputStream == null) {
			System.out.println("Unable to get help file resource");
			ret = false;
		}
		System.out.println("We have our input stream");
		return ret;
	}


	/**
	 * Initialise the help text object using JAXB
	 * 
	 * @throws JAXBException
	 */
	private void init() throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(HelpText.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		helpText = (HelpText) jaxbUnmarshaller.unmarshal(inputStream);
	}
	

	/**
	 * @param helpId the id of the HelpField object required
	 * @return the HelpField object that relates to the required id, or null if help is not availabe 
	 */
	public HelpField getHelpField(String helpId) {
		if (!helpEnabled) {
			return null;
		}

		List<HelpGroup> helpGroupList = helpText.getHelpGroup();
		for (int counter = 0; counter < helpGroupList.size(); counter++) {
			List<HelpField> helpFieldList = helpGroupList.get(counter).getHelpField();
			for (int counter2 = 0; counter2 < helpFieldList.size(); counter2++) {
				if (helpFieldList.get(counter2).getHelpId().compareToIgnoreCase(helpId) == 0) {
					return helpFieldList.get(counter2);
				}
			}
		}
		return null;
	}

	public static void main(String args[]) {
		try {
			HelpTextGenerator helpTextGenerator = new HelpTextGenerator();
			System.out.println(helpTextGenerator.helpEnabled);
			HelpField helpField = helpTextGenerator.getHelpField("CreateDataSpacePopup_Field02");
			if (helpField != null) {
				System.out.println(helpField.getText());
			}
			else {
				System.out.println("No help available");
			}
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
