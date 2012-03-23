package uk.ac.ox.oucs.vidaasBilling.servlet;

import java.util.ArrayList;
import java.util.List;

public class VidaasWorkspace {
	public static enum BackupPolicy {daily, weekly, fortnightly, monthly};
	public static enum DBSize {oneGig, tenGig, fiftyGig, oneHundredGig};
	private int numberOfProjects;
	private class Project {
		int numberOfDatabases;
		private class Database {
			private DBSize size;
			private BackupPolicy backupPolicy;
		}
		
	}
	private List<Project> projectList = new ArrayList<Project>();
}
