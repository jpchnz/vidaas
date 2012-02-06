package uk.ac.ox.oucs.iam.roles;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;



public class IAMRoleManagerTest {
	/**
	 * Test projects
	 * @throws IOException
	 */
	@Test
	public void test1() throws IOException {
		IAMRoleManager iamPost = new IAMRoleManager();
		
		System.out.println("Checking is owner ...");
		assertTrue(iamPost.isOwner("owner"));
		assertFalse(iamPost.isOwner("Admin"));
		
		System.out.println("Checking is allowed to create project ...");
		assertTrue(iamPost.getProjectAuthentication().isAllowedToCreateDatabaseByRole("owner"));
		assertTrue(iamPost.getProjectAuthentication().isAllowedToCreateDatabaseByRole("Project Administrator"));
		assertTrue(iamPost.getProjectAuthentication().isAllowedToCreateDatabaseByRole("contributor"));
		assertFalse(iamPost.getProjectAuthentication().isAllowedToCreateDatabaseByRole("Viewer"));
		assertFalse(iamPost.getProjectAuthentication().isAllowedToCreateDatabaseByRole("Rubbish input"));
		
		System.out.println("Checking is allowed to add others for project ...");
		assertTrue(iamPost.getProjectAuthentication().isAllowedToAddOthersForProject("owner"));
		assertTrue(iamPost.getProjectAuthentication().isAllowedToAddOthersForProject("Project Administrator"));
		assertFalse(iamPost.getProjectAuthentication().isAllowedToAddOthersForProject("contributor"));
		assertFalse(iamPost.getProjectAuthentication().isAllowedToAddOthersForProject("Viewer"));
		assertFalse(iamPost.getProjectAuthentication().isAllowedToAddOthersForProject("Rubbish input"));
		
		System.out.println("Checking is allowed to remove others for project ...");
		assertTrue(iamPost.getProjectAuthentication().isAllowedToRemoveOthersForProject("owner"));
		assertTrue(iamPost.getProjectAuthentication().isAllowedToRemoveOthersForProject("Project Administrator"));
		assertFalse(iamPost.getProjectAuthentication().isAllowedToRemoveOthersForProject("contributor"));
		assertFalse(iamPost.getProjectAuthentication().isAllowedToRemoveOthersForProject("Viewer"));
		assertFalse(iamPost.getProjectAuthentication().isAllowedToRemoveOthersForProject("Rubbish input"));
		
		System.out.println("Checking is allowed to create public views of data ...");
		assertTrue(iamPost.getProjectAuthentication().isCreatePublicViewsOfData("owner"));
		assertTrue(iamPost.getProjectAuthentication().isCreatePublicViewsOfData("Project Administrator"));
		assertFalse(iamPost.getProjectAuthentication().isCreatePublicViewsOfData("contributor"));
		assertFalse(iamPost.getProjectAuthentication().isCreatePublicViewsOfData("Viewer"));
		assertFalse(iamPost.getProjectAuthentication().isCreatePublicViewsOfData("Rubbish input"));
		
		System.out.println("Checking is allowed to alter others role ...");
		assertTrue(iamPost.getProjectAuthentication().isAllowedToAlterOtherUsersRole("owner"));
		assertTrue(iamPost.getProjectAuthentication().isAllowedToAlterOtherUsersRole("Project Administrator"));
		assertFalse(iamPost.getProjectAuthentication().isAllowedToAlterOtherUsersRole("contributor"));
		assertFalse(iamPost.getProjectAuthentication().isAllowedToAlterOtherUsersRole("Viewer"));
		assertFalse(iamPost.getProjectAuthentication().isAllowedToAlterOtherUsersRole("Rubbish input"));
		
		System.out.println("Checking is allowed to view project data ...");
		assertTrue(iamPost.getProjectAuthentication().isViewProjectData("owner"));
		assertTrue(iamPost.getProjectAuthentication().isViewProjectData("Project Administrator"));
		assertTrue(iamPost.getProjectAuthentication().isViewProjectData("contributor"));
		assertTrue(iamPost.getProjectAuthentication().isViewProjectData("Viewer"));
		assertFalse(iamPost.getProjectAuthentication().isViewProjectData("Rubbish input"));
		
		System.out.println("Checking is allowed to create database in project ...");
		assertTrue(iamPost.getProjectAuthentication().isAllowedToCreateDatabaseByRole("owner"));
		assertTrue(iamPost.getProjectAuthentication().isAllowedToCreateDatabaseByRole("Project Administrator"));
		assertTrue(iamPost.getProjectAuthentication().isAllowedToCreateDatabaseByRole("contributor"));
		assertFalse(iamPost.getProjectAuthentication().isAllowedToCreateDatabaseByRole("Viewer"));
		assertFalse(iamPost.getProjectAuthentication().isAllowedToCreateDatabaseByRole("Rubbish input"));
		
		System.out.println("Checking is allowed to edit database in project ...");
		assertTrue(iamPost.getProjectAuthentication().isAllowedToEditDatabaseByRole("owner"));
		assertTrue(iamPost.getProjectAuthentication().isAllowedToEditDatabaseByRole("Project Administrator"));
		assertTrue(iamPost.getProjectAuthentication().isAllowedToEditDatabaseByRole("contributor"));
		assertFalse(iamPost.getProjectAuthentication().isAllowedToEditDatabaseByRole("Viewer"));
		assertFalse(iamPost.getProjectAuthentication().isAllowedToEditDatabaseByRole("Rubbish input"));
		
		System.out.println("Checking is allowed to delete database in project ...");
		assertTrue(iamPost.getProjectAuthentication().isAllowedToDeleteDatabaseByRole("owner"));
		assertTrue(iamPost.getProjectAuthentication().isAllowedToDeleteDatabaseByRole("Project Administrator"));
		assertTrue(iamPost.getProjectAuthentication().isAllowedToDeleteDatabaseByRole("contributor"));
		assertFalse(iamPost.getProjectAuthentication().isAllowedToDeleteDatabaseByRole("Viewer"));
		assertFalse(iamPost.getProjectAuthentication().isAllowedToDeleteDatabaseByRole("Rubbish input"));
		
		System.out.println("Checking is allowed to delete project ...");
		assertTrue(iamPost.getProjectAuthentication().isAllowedToRemoveProject("owner"));
		assertFalse(iamPost.getProjectAuthentication().isAllowedToRemoveProject("Project Administrator"));
		assertFalse(iamPost.getProjectAuthentication().isAllowedToRemoveProject("contributor"));
		assertFalse(iamPost.getProjectAuthentication().isAllowedToRemoveProject("Viewer"));
		assertFalse(iamPost.getProjectAuthentication().isAllowedToRemoveProject("Rubbish input"));
	}
	
	
	/**
	 * Test Web Apps
	 * @throws IOException
	 */
	@Test
	public void test2() throws IOException {
		IAMRoleManager iamPost = new IAMRoleManager();
		
		System.out.println("Checking webapp authentication ...");
		assertTrue(iamPost.getWebAppAuthentication().isAllowedToCreateWebAppByRole("contributor"));
		assertTrue(iamPost.getWebAppAuthentication().isAllowedToCreateWebAppByRole("owner"));
		assertTrue(iamPost.getWebAppAuthentication().isAllowedToCreateWebAppByRole("Project Administrator"));
		assertFalse(iamPost.getWebAppAuthentication().isAllowedToCreateWebAppByRole("viewer"));
		
		assertTrue(iamPost.getWebAppAuthentication().isAllowedToDeleteWebAppByRole("contributor"));
		assertTrue(iamPost.getWebAppAuthentication().isAllowedToDeleteWebAppByRole("owner"));
		assertTrue(iamPost.getWebAppAuthentication().isAllowedToDeleteWebAppByRole("Project Administrator"));
		assertFalse(iamPost.getWebAppAuthentication().isAllowedToDeleteWebAppByRole("viewer"));
	}
	
	
	/**
	 * Test db authentication
	 * @throws IOException
	 */
	@Test
	public void test3() throws IOException {
IAMRoleManager iamPost = new IAMRoleManager();
		
		System.out.println("Checking db authentication ...");
		assertTrue(iamPost.getDatabaseAuthentication().isAllowedToAddEditOrRemoveDBData("contributor"));
		assertTrue(iamPost.getDatabaseAuthentication().isAllowedToAddEditOrRemoveDBData("owner"));
		assertTrue(iamPost.getDatabaseAuthentication().isAllowedToAddEditOrRemoveDBData("Project Administrator"));
		assertFalse(iamPost.getDatabaseAuthentication().isAllowedToAddEditOrRemoveDBData("viewer"));
	}
}
