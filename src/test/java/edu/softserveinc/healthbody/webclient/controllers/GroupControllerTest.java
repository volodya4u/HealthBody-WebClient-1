package edu.softserveinc.healthbody.webclient.controllers;

import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;
import org.testng.annotations.Test;

import edu.softserveinc.healthbody.webclient.healthbody.webservice.GroupDTO;
import edu.softserveinc.healthbody.webclient.healthbody.webservice.UserDTO;
import edu.softserveinc.healthbody.webclient.wrapperD.GroupDTORest;
import edu.softserveinc.healthbody.webclient.wrapperD.URLFormatter;

public class GroupControllerTest extends ControllerTestSupport {

	private static GroupDTO group(String id) {
		GroupDTO group = new GroupDTO();
		group.setIdGroup(id);
		group.setName("Group " + id);
		return group;
	}

	private UserDTO storedUser(String roleName, GroupDTO... groups) {
		UserDTO user = user(LOGIN, roleName);
		user.getGroups().addAll(Arrays.asList(groups));
		when(service.getUserByLogin(LOGIN)).thenReturn(user);
		return user;
	}

	@Test
	public void listsAPageOfGroupsFromTheRestService() throws Exception {
		List<GroupDTORest> page = Collections.singletonList(new GroupDTORest());
		when(service.getAllGroupsParticipants(1, Integer.MAX_VALUE))
				.thenReturn(Arrays.asList(group("g1"), group("g2"), group("g3")));
		storedUser("user");

		try (MockedConstruction<URLFormatter> formatters = mockConstruction(URLFormatter.class,
				(formatter, context) -> when(formatter.getGroupsByPartnumberPartsize("GroupsParticipants", 2, 1))
						.thenReturn(page))) {
			mockMvc(new GroupController()).perform(get("/listGroups.html").param("groupsParticipantsPartnumber", "2"))
					.andExpect(view().name("listGroups")).andExpect(model().attribute("currentPage", 2))
					.andExpect(model().attribute("lastpagePartNumber", 3)).andExpect(model().attribute("groups", page));
		}
	}

	@Test
	public void adminsEditTheGroupDescription() throws Exception {
		storedUser("admin");
		when(service.getGroupById("g1")).thenReturn(group("g1"));

		mockMvc(new GroupController()).perform(get("/group.html").param("nameGroup", "g1"))
				.andExpect(view().name("editGroupDescription"));
	}

	@Test
	public void membersSeeTheirGroup() throws Exception {
		storedUser("user", group("g1"));
		GroupDTO group = group("g1");
		when(service.getGroupById("g1")).thenReturn(group);

		mockMvc(new GroupController()).perform(get("/group.html").param("nameGroup", "g1"))
				.andExpect(view().name("group")).andExpect(model().attribute("group", group));
	}

	@Test
	public void otherUsersAreOfferedToJoin() throws Exception {
		storedUser("user", group("g2"));
		when(service.getGroupById("g1")).thenReturn(group("g1"));

		mockMvc(new GroupController()).perform(get("/group.html").param("nameGroup", "g1"))
				.andExpect(view().name("joinGroup"));
	}

	@Test
	public void joiningAddsTheGroupToTheUser() throws Exception {
		UserDTO user = storedUser("user");
		GroupDTO group = group("g1");
		when(service.getGroupById("g1")).thenReturn(group);

		mockMvc(new GroupController()).perform(get("/joinGroup.html").param("nameGroup", "g1"))
				.andExpect(view().name("userCabinet"));

		verify(service).updateUser(user);
		assertTrue(user.getGroups().contains(group));
	}

	@Test
	public void savingAGroupSendsTheEditedFields() throws Exception {
		storedUser("admin");

		mockMvc(new GroupController())
				.perform(post("/editingGroup").param("idGroup", "g1").param("name", "Runners")
						.param("descriptions", "We run every morning").param("status", "active"))
				.andExpect(view().name("editGroupDescription"));

		ArgumentCaptor<GroupDTO> saved = ArgumentCaptor.forClass(GroupDTO.class);
		verify(service).updateGroup(saved.capture());
		assertEquals(saved.getValue().getIdGroup(), "g1");
		assertEquals(saved.getValue().getName(), "Runners");
		assertEquals(saved.getValue().getDescriptions(), "We run every morning");
		assertEquals(saved.getValue().getStatus(), "active");
	}
}
