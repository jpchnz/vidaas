package uk.ac.ox.oucs.vidaas.session;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.jboss.seam.log.Log;

import uk.ac.ox.oucs.vidaas.session.generated.HelpField;
import uk.ac.ox.oucs.vidaas.session.generated.HelpGroup;
import uk.ac.ox.oucs.vidaas.session.generated.HelpText;

public class HelpTextGenerator {
	private HelpText helpText;
	private File helpFile;
	boolean helpEnabled = true;
	private Log log;

	public HelpTextGenerator(Log log) throws JAXBException, FileNotFoundException {
		this.log = log;
		try {
			loadHelpFile();
		}
		catch (IOException e) {
			log.error("Unable to find helpfile in the standard place");
			helpFile = new File("/tmp/helpText.xml");
		}
		
		init();
	}
	
	private HelpTextGenerator() throws JAXBException, FileNotFoundException {
		try {
			loadHelpFile();
		}
		catch (IOException e) {
			System.out.println("Unable to find helpfile in the standard place");
			helpFile = new File("/tmp/helpText.xml");
		}
		
		init();
	}

	private void loadHelpFile() throws IOException {
		InputStream inputStream = HelpTextGenerator.class.getClassLoader().getResourceAsStream("helpText.xml");
		if (inputStream == null) {
			if (log == null) {
				System.out.println("Unable to get resource");
			}
			else {
				log.error("Unable to get resource");
			}
			throw new IOException();
		}
		helpFile = new File("helpfile");
		OutputStream out = new FileOutputStream(helpFile);
		byte buf[] = new byte[1024];
		int len;
		while ((len = inputStream.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		out.close();
		inputStream.close();
		if (log == null) {
			System.out.println("Help text has been loaded");
		}
		else {
			log.error("Help text has been loaded");
		}
	}

	public HelpTextGenerator(String fileName) throws JAXBException {
		helpFile = new File(fileName);
		init();
	}

	private void init() throws JAXBException {
		if (!helpFile.exists()) {
			System.out.println("Unable to find help file. Help will not be available.");
			helpEnabled = false;
		}
		else {
			JAXBContext jaxbContext = JAXBContext.newInstance(HelpText.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			helpText = (HelpText) jaxbUnmarshaller.unmarshal(helpFile);
		}
	}

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
