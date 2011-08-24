package uk.ac.ox.oucs.vidaas.session;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


import uk.ac.ox.oucs.vidaas.dao.*;

import uk.ac.ox.oucs.vidaas.entity.*;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.log.Log;

@Name("navigationController")
@Scope(ScopeType.SESSION)
public class NavigationController {
	
	@Logger private Log log;
	
	@In (create = true)
    @Out (required = true)
    private UserProjectHome userProjectHome;
	
	@In (create = true)
    @Out (required = true)
    private ProjectHome projectHome;
	
	@In (create = true)
    @Out (required = true)
    private ProjectDatabaseHome projectDatabaseHome;
	
	@In (create = true)
    @Out (required = true)
    DataspaceHome dataspaceHome;
	
	private Users userMain;	
	private Logins loginMain;
	
	private Project currentProject;
	private Dataspace currentDataspace;
	
	@RequestParameter("projectIDValue")
	private Integer projectIDValue;
	
	@RequestParameter("dataspaceIDValue")
	private Integer dataspaceIDValue;
	
	private String homePageMainBodyNavigation = "homeWelcome.xhtml";
	private String homePageNavigation = "empty.xhtml";
	
	private String createProjectFormInclude = "/popup/createProjectForm.xhtml";
	private boolean createProjectFormRender = false;
	
	private String createDataSpaceInclude = "/popup/createDataSpaceForm.xhtml";
	private boolean createDataSpaceFormRender = false;
	
	private String createDatabaseInclude = "/popup/createDatabaseForm.xhtml";
	private boolean databaseFormRender = false;
	
	private String parseDatabaseInclude = "/popup/parseDatabaseForm.xhtml";
	private boolean parseDatabaseFormRender = false;
	
	private String addProjectMemberInclude = "/popup/addProjectMemberForm.xhtml";
	private boolean projectMemberFormRender = false;
	
	public String getHomePageMainBodyNavigation() {
		return homePageMainBodyNavigation;
	}

	public void setHomePageMainBodyNavigation(String homePageMainBodyNavigation) {
		this.homePageMainBodyNavigation = homePageMainBodyNavigation;
	}

	public String getHomePageNavigation() {
		return homePageNavigation;
	}

	public void setHomePageNavigation(String homePageNavigation) {
		this.homePageNavigation = homePageNavigation;
	}
	
	public Integer getProjectIDValue() {
		log.info("getProjectIDValue {0}", projectIDValue);
		return projectIDValue;
	}

	public void setProjectIDValue(Integer projectIDValue) {
		log.info("setProjectIDValue {0}", projectIDValue);
		this.projectIDValue = projectIDValue;
	}

	public String getCreateProjectFormInclude() {
		return createProjectFormInclude;
	}

	public void setCreateProjectFormInclude(String createProjectFormInclude) {
		this.createProjectFormInclude = createProjectFormInclude;
	}

	public boolean isCreateProjectFormRender() {
		return createProjectFormRender;
	}

	public void setCreateProjectFormRender(boolean createProjectFormRender) {
		this.createProjectFormRender = createProjectFormRender;
	}
	
	public String getCreateDataSpaceInclude() {
		return createDataSpaceInclude;
	}

	public void setCreateDataSpaceInclude(String createDataSpaceInclude) {
		this.createDataSpaceInclude = createDataSpaceInclude;
	}

	public boolean isCreateDataSpaceFormRender() {
		return createDataSpaceFormRender;
	}

	public void setCreateDataSpaceFormRender(boolean createDataSpaceFormRender) {
		this.createDataSpaceFormRender = createDataSpaceFormRender;
	}
	
	public String getCreateDatabaseInclude() {
		return createDatabaseInclude;
	}

	public void setCreateDatabaseInclude(String createDatabaseInclude) {
		this.createDatabaseInclude = createDatabaseInclude;
	}

	public boolean isDatabaseFormRender() {
		return databaseFormRender;
	}

	public void setDatabaseFormRender(boolean databaseFormRender) {
		this.databaseFormRender = databaseFormRender;
	}
	
	public String getParseDatabaseInclude() {
		return parseDatabaseInclude;
	}

	public void setParseDatabaseInclude(String parseDatabaseInclude) {
		this.parseDatabaseInclude = parseDatabaseInclude;
	}

	public boolean isParseDatabaseFormRender() {
		return parseDatabaseFormRender;
	}

	public void setParseDatabaseFormRender(boolean parseDatabaseFormRender) {
		this.parseDatabaseFormRender = parseDatabaseFormRender;
	}
	
	public String getAddProjectMemberInclude() {
		return addProjectMemberInclude;
	}

	public void setAddProjectMemberInclude(String addProjectMemberInclude) {
		this.addProjectMemberInclude = addProjectMemberInclude;
	}

	public boolean isProjectMemberFormRender() {
		return projectMemberFormRender;
	}

	public void setProjectMemberFormRender(boolean projectMemberFormRender) {
		this.projectMemberFormRender = projectMemberFormRender;
	}

	public Users getUserMain() {
		if(userMain == null){
			userMain = ((Users) Contexts.getSessionContext().get(
			"userMain"));
		}
		return userMain;
	}

	public Logins getLoginMain() {
		
		return loginMain;
	}

	public void welcomePage(){
		homePageMainBodyNavigation = "homeWelcome.xhtml";
	}
	

	public void listUserProjects(){
		homePageMainBodyNavigation = "/custom/projectByUserList.xhtml";
	}
	
	public List<UserProject> userProjectsList() {
		return userProjectHome.findByUserID(getUserMain().getUserId());
	}
	
	public List<ProjectDatabase> listProjectDatabase(Integer projectIDValue){
		return projectDatabaseHome.findByProjectID(projectIDValue);
		// currentDataspace
	}
	
	
	public List<ProjectDatabase> listDatabaseByDataspace(){
		Set<ProjectDatabase> tempProjectDatabaseSet  = currentDataspace.getProjectDatabases();
		log.info("currentDataspace.getDataSpaceId() {0} {1}", currentDataspace.getDataSpaceId(), tempProjectDatabaseSet.size());
		
		List<ProjectDatabase> list = new ArrayList<ProjectDatabase>(tempProjectDatabaseSet);
		return list;
		// currentDataspace
	}
	
	
	public List<UserProject> listProjectMembers(Integer projectIDValue){
		List<UserProject> userProject = userProjectHome.findByProjectID(projectIDValue);
		//System.out.println("listProjectMembers(Integer " + projectIDValue + ")   " + userProject.size() + "  " + userProject.get(0).getUsers().getFirstName());
		return userProject;
	}
	
	public List<Dataspace> listProjectDataspaces(Integer projectIDValue){
		List<Dataspace> dataspacesList = dataspaceHome.findByProjectID(projectIDValue);
		log.info("listProjectDataspaces ***************************************   dataspaceIDValue:  {0} {1}",  dataspacesList.size(), projectIDValue);
		return dataspacesList;
	}
	
	public void addProjectMemberInitial(){
		projectMemberFormRender = false;
		log.info("addProjectMemberInitial() ................................................................... addProjectMemberInitial()");
		addProjectMemberInclude = "/popup/addProjectMemberForm.xhtml";
	}
	
	public void addProjectMemberConfirmation(){
		projectMemberFormRender = true;
		log.info("createDatabaseStructureConfirmation()");
		addProjectMemberInclude = "/popup/addProjectMemberConfirmation.xhtml";
	}
	
	public void createProjectInitial(){
		createProjectFormRender = false;
		log.info("createProjectInitial()");
		createProjectFormInclude = "/popup/createProjectForm.xhtml";
	}
	
	public void createProjectConfirmation(){
		createProjectFormRender = true;
		createProjectFormInclude = "/popup/createProjectConfirmation.xhtml";
	}
	
	public void createProjectDataSpaceInitial(){
		createProjectFormRender = false;
		log.info("createProjectInitial()");
		createDataSpaceInclude = "/popup/createDataSpaceForm.xhtml";
	}
	
	public void createProjectDataspaceConfirmation(){
		createProjectFormRender = true;
		createDataSpaceInclude = "/popup/createDataSpaceConfirmation.xhtml";
	}
	
	public void createDatabaseInitial(){
		databaseFormRender = false;
		createDatabaseInclude = "/popup/createDatabaseForm.xhtml";
	}
	
	public void createDatabaseConfirmation(){
		databaseFormRender = true;
		createDatabaseInclude = "/popup/createDatabaseConfirmation.xhtml";
	}
	
	public void createParseDatabaseInitial(){
		
	}
	
	public void createParseDatabaseConfirmation(){
		
	}	
	public void singleDataspaceDisplayPage(){
		log.info("singleDatabaseDisplayPage ***************************************   dataspaceIDValue: " + dataspaceIDValue);
		log.info("singleDatabaseDisplayPage ***************************************   projectIDValue: " + projectIDValue);
		
		projectHome.setId(projectIDValue);
		currentProject = projectHome.getInstance();
		Contexts.getSessionContext().set("currentProject", currentProject);
		
		dataspaceHome.setId(dataspaceIDValue);
		currentDataspace = dataspaceHome.getInstance();
		Contexts.getSessionContext().set("currentDataspace", currentDataspace);
		
		homePageMainBodyNavigation = "/custom/singleDataspaceByProject.xhtml";
	}
	
	public void createProjectMember(){
		log.info("createProjectMember ***************************************   projectIDValue: " + projectIDValue);
		if(projectIDValue != null){
			Contexts.getSessionContext().set("projectIDValue", projectIDValue);
		} else {
			projectIDValue = (Integer)Contexts.getSessionContext().get("projectIDValue");
		}
		
		projectHome.setId(projectIDValue);
		currentProject = projectHome.getInstance();
		Contexts.getSessionContext().set("currentProject", currentProject);
		//homePageMainBodyNavigation = "/custom/createUserForm.xhtml";
	}
	
	
}
