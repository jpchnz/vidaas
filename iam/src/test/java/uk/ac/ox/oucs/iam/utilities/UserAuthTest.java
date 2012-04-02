package uk.ac.ox.oucs.iam.utilities;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import uk.ac.ox.oucs.iam.utilities.UserAuth.FunctonsAvailableEnum;

public class UserAuthTest {
	private UserAuth userAuth = new UserAuth();
	
	@Test
	public void testOwnerAuth() {
		assertTrue(userAuth.isAuthorised("Owner", FunctonsAvailableEnum.IsOwner));
		assertTrue(userAuth.isAuthorised("owner", FunctonsAvailableEnum.DeleteProject));
		assertTrue(userAuth.isAuthorised("owner", FunctonsAvailableEnum.CreatePublicViewsOfData));
		assertTrue(userAuth.isAuthorised("owner", FunctonsAvailableEnum.AddProject));
		assertTrue(userAuth.isAuthorised("owner", FunctonsAvailableEnum.AddUserToProject));
		assertTrue(userAuth.isAuthorised("owner", FunctonsAvailableEnum.RemoveUserFromProject));
		assertTrue(userAuth.isAuthorised("OWNER", FunctonsAvailableEnum.EditUsersRightsForProject));
		assertTrue(userAuth.isAuthorised("owner", FunctonsAvailableEnum.EditProject));
		assertTrue(userAuth.isAuthorised("owner", FunctonsAvailableEnum.CreateDB));
		assertTrue(userAuth.isAuthorised("owner", FunctonsAvailableEnum.EditDB));
		assertTrue(userAuth.isAuthorised("owner", FunctonsAvailableEnum.DeleteDB));
		assertTrue(userAuth.isAuthorised("owner", FunctonsAvailableEnum.AddRemoveEditDataInDB));
		assertTrue(userAuth.isAuthorised("owner", FunctonsAvailableEnum.CreateWebApp));
		assertTrue(userAuth.isAuthorised("owner", FunctonsAvailableEnum.DeleteWebApp));
		assertTrue(userAuth.isAuthorised("owner", FunctonsAvailableEnum.ViewProjectData));
	}

	@Test
	public void testProjectAdminAuth() {
		assertFalse(userAuth.isAuthorised("PROject administrator", FunctonsAvailableEnum.IsOwner));
		assertFalse(userAuth.isAuthorised("Project Administrator", FunctonsAvailableEnum.DeleteProject));
		assertTrue(userAuth.isAuthorised("project administrator", FunctonsAvailableEnum.CreatePublicViewsOfData));
		assertTrue(userAuth.isAuthorised("project administrator", FunctonsAvailableEnum.AddProject));
		assertTrue(userAuth.isAuthorised("project administrator", FunctonsAvailableEnum.AddUserToProject));
		assertTrue(userAuth.isAuthorised("project administrator", FunctonsAvailableEnum.RemoveUserFromProject));
		assertTrue(userAuth.isAuthorised("project administrator", FunctonsAvailableEnum.EditUsersRightsForProject));
		assertTrue(userAuth.isAuthorised("project admiNIStrator", FunctonsAvailableEnum.EditProject));
		assertTrue(userAuth.isAuthorised("project administrator", FunctonsAvailableEnum.CreateDB));
		assertTrue(userAuth.isAuthorised("project administrator", FunctonsAvailableEnum.EditDB));
		assertTrue(userAuth.isAuthorised("project administrator", FunctonsAvailableEnum.DeleteDB));
		assertTrue(userAuth.isAuthorised("project administrator", FunctonsAvailableEnum.AddRemoveEditDataInDB));
		assertTrue(userAuth.isAuthorised("project administrator", FunctonsAvailableEnum.CreateWebApp));
		assertTrue(userAuth.isAuthorised("project administrator", FunctonsAvailableEnum.DeleteWebApp));
		assertTrue(userAuth.isAuthorised("project administrator", FunctonsAvailableEnum.ViewProjectData));
	}
	
	@Test
	public void testContributorAuth() {
		assertFalse(userAuth.isAuthorised("contributor", FunctonsAvailableEnum.IsOwner));
		assertFalse(userAuth.isAuthorised("contributor", FunctonsAvailableEnum.DeleteProject));
		assertFalse(userAuth.isAuthorised("contributor", FunctonsAvailableEnum.CreatePublicViewsOfData));
		assertFalse(userAuth.isAuthorised("contributor", FunctonsAvailableEnum.AddProject));
		assertFalse(userAuth.isAuthorised("contributor", FunctonsAvailableEnum.AddUserToProject));
		assertFalse(userAuth.isAuthorised("contributor", FunctonsAvailableEnum.RemoveUserFromProject));
		assertFalse(userAuth.isAuthorised("contributor", FunctonsAvailableEnum.EditUsersRightsForProject));
		assertFalse(userAuth.isAuthorised("contributor", FunctonsAvailableEnum.EditProject));
		assertTrue(userAuth.isAuthorised("contributor", FunctonsAvailableEnum.CreateDB));
		assertTrue(userAuth.isAuthorised("contributor", FunctonsAvailableEnum.EditDB));
		assertTrue(userAuth.isAuthorised("contributor", FunctonsAvailableEnum.DeleteDB));
		assertTrue(userAuth.isAuthorised("contributor", FunctonsAvailableEnum.AddRemoveEditDataInDB));
		assertTrue(userAuth.isAuthorised("contributor", FunctonsAvailableEnum.CreateWebApp));
		assertTrue(userAuth.isAuthorised("contributor", FunctonsAvailableEnum.DeleteWebApp));
		assertTrue(userAuth.isAuthorised("contributor", FunctonsAvailableEnum.ViewProjectData));
	}
	
	
	
	@Test
	public void testViewerAuth() {
		assertFalse(userAuth.isAuthorised("viewer", FunctonsAvailableEnum.IsOwner));
		assertFalse(userAuth.isAuthorised("viewer", FunctonsAvailableEnum.DeleteProject));
		assertFalse(userAuth.isAuthorised("viewer", FunctonsAvailableEnum.CreatePublicViewsOfData));
		assertFalse(userAuth.isAuthorised("viewer", FunctonsAvailableEnum.AddProject));
		assertFalse(userAuth.isAuthorised("viewer", FunctonsAvailableEnum.AddUserToProject));
		assertFalse(userAuth.isAuthorised("viewer", FunctonsAvailableEnum.RemoveUserFromProject));
		assertFalse(userAuth.isAuthorised("viewer", FunctonsAvailableEnum.EditUsersRightsForProject));
		assertFalse(userAuth.isAuthorised("viewer", FunctonsAvailableEnum.EditProject));
		assertFalse(userAuth.isAuthorised("viewer", FunctonsAvailableEnum.CreateDB));
		assertFalse(userAuth.isAuthorised("viewer", FunctonsAvailableEnum.EditDB));
		assertFalse(userAuth.isAuthorised("viewer", FunctonsAvailableEnum.DeleteDB));
		assertFalse(userAuth.isAuthorised("viewer", FunctonsAvailableEnum.AddRemoveEditDataInDB));
		assertFalse(userAuth.isAuthorised("viewer", FunctonsAvailableEnum.CreateWebApp));
		assertFalse(userAuth.isAuthorised("viewer", FunctonsAvailableEnum.DeleteWebApp));
		assertTrue(userAuth.isAuthorised("viewer", FunctonsAvailableEnum.ViewProjectData));
	}
	
	
	@Test
	public void genericTests() {
		String[] roles = userAuth.getRolesAvailable();
		assertTrue(roles[0].compareToIgnoreCase("owner") == 0);
		assertTrue(roles.length == 4);
		String[] excludeOwnerRoles = userAuth.getRolesAvailableExcludeOwner();
		assertTrue(excludeOwnerRoles.length == (roles.length-1));
		assertTrue(userAuth.getOwnerRole().compareToIgnoreCase("owner") == 0);
	}
}
