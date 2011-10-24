package uk.ac.ox.oucs.vidaas.data.holder;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;

@Name("dataHolder")
@AutoCreate
@Scope(ScopeType.SESSION)
public class DataHolder {
	
	public boolean okButton = false;
	
	public boolean mdbParser = false;
	public boolean tableCreator = false;
	public boolean dataLoader = false;
	
	public String currentStatus = "Not Yet Started";

	public boolean isOkButton() {
		return okButton;
	}

	public void setOkButton(boolean okButton) {
		this.okButton = okButton;
	}

	public String getCurrentStatus() {
		return currentStatus;
	}

	public void setCurrentStatus(String currentStatus) {
		this.currentStatus = currentStatus;
	}
	
	public void defaultValues(){
		okButton = true;
		currentStatus = "Not Yet Started";
	}

	public boolean isMdbParser() {
		return mdbParser;
	}

	public void setMdbParser(boolean mdbParser) {
		this.mdbParser = mdbParser;
	}

	public boolean isTableCreator() {
		return tableCreator;
	}

	public void setTableCreator(boolean tableCreator) {
		this.tableCreator = tableCreator;
	}

	public boolean isDataLoader() {
		return dataLoader;
	}

	public void setDataLoader(boolean dataLoader) {
		this.dataLoader = dataLoader;
	}	

}
