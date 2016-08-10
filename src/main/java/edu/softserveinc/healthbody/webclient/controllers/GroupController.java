package edu.softserveinc.healthbody.webclient.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import edu.softserveinc.healthbody.webclient.api.GroupDTO;
import edu.softserveinc.healthbody.webclient.api.HealthBodyService;
import edu.softserveinc.healthbody.webclient.api.HealthBodyServiceImplService;
import edu.softserveinc.healthbody.webclient.api.UserDTO;

@Controller
public class GroupController {
	
	@RequestMapping(value = "/listGroups.html", method = RequestMethod.GET)
	public String getGroups(Model model, @Autowired HealthBodyServiceImplService healthBody, 
			@RequestParam(value="groupsParticipantsPartnumber", required=false) Integer groupsParticipantsPartnumber) {
		final Integer DEFAULT_QUONTITY_GROUPS_PER_PAGE = 1;
			if(groupsParticipantsPartnumber == null || groupsParticipantsPartnumber <= 0){
				groupsParticipantsPartnumber = 1;
			}
		int currentPage = groupsParticipantsPartnumber;
		int startPartNumber = (int) (groupsParticipantsPartnumber - 5 > 0?groupsParticipantsPartnumber - 5:1);
		int endpagePartNumber = startPartNumber + 2;
			
		String userLogin = SecurityContextHolder.getContext().getAuthentication().getName();	
		HealthBodyService service = healthBody.getHealthBodyServiceImplPort();
		model.addAttribute("user", service.getUserByLogin(userLogin));
	    model.addAttribute("startPartNumber",startPartNumber);
	    model.addAttribute("endpagePartNumber",endpagePartNumber);
	    model.addAttribute("currentPage",currentPage);
		model.addAttribute("groups", service.getAllGroupsParticipants(groupsParticipantsPartnumber, DEFAULT_QUONTITY_GROUPS_PER_PAGE));
		return "listGroups";
	}
	
	@RequestMapping(value = "/group.html", method = RequestMethod.GET)
	public String getGroup(Model model, @Autowired HealthBodyServiceImplService healthBody, String nameGroup) {
		String userLogin = SecurityContextHolder.getContext().getAuthentication().getName();
		HealthBodyService service = healthBody.getHealthBodyServiceImplPort();
		boolean test = false;
		for (GroupDTO group : service.getUserByLogin(userLogin).getGroups()) {
			if(group.getName().equals(nameGroup)) {
				test = true;
			}
		}
		model.addAttribute("user", service.getUserByLogin(userLogin));
		model.addAttribute("group", service.getGroupByName(nameGroup));
		if (test) {
			return "group";
		} else {
			return "Join the group";
		}
	}
	
	@RequestMapping(value = "/Join the group.html",  method = RequestMethod.GET)
	public String joinGroup(Model model, @Autowired HealthBodyServiceImplService healthBody, String nameGroup) {
		String userLogin = SecurityContextHolder.getContext().getAuthentication().getName();
		HealthBodyService service = healthBody.getHealthBodyServiceImplPort();
		UserDTO user = service.getUserByLogin(userLogin);
		user.getGroups().add(service.getGroupByName(nameGroup));
		service.updateUser(user);
		model.addAttribute("user", service.getUserByLogin(userLogin));
		return "usercabinet";
	}
}
