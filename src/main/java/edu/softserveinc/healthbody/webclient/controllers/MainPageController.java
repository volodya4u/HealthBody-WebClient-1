package edu.softserveinc.healthbody.webclient.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import edu.softserveinc.healthbody.webclient.api.CompetitionDTO;
import edu.softserveinc.healthbody.webclient.api.HealthBodyService;
import edu.softserveinc.healthbody.webclient.api.HealthBodyServiceImplService;
import edu.softserveinc.healthbody.webclient.api.UserDTO;

@Controller
public class MainPageController {
	
	private final Integer COMPETITIONS_PER_PAGE = 10;
	
	@RequestMapping(value = "/main.html",method = RequestMethod.GET)
	public String getListCurrentCompetitions(Model model, @Autowired HealthBodyServiceImplService healthBody,
			@RequestParam(value = "partNumber", required = false) Integer partNumber, HttpServletRequest request) {
		if (partNumber == null) {
			partNumber = 1;
		}
		int currentPage = partNumber;
		int startPartNumber = 1;		
		HealthBodyService service = healthBody.getHealthBodyServiceImplPort();
		int n = service.getAllActiveCompetitions(1, Integer.MAX_VALUE).size();
		int lastPartNumber = (int) Math.ceil(n * 1.0 / COMPETITIONS_PER_PAGE);
		model.addAttribute("startPartNumber", startPartNumber);
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("lastPartNumber", lastPartNumber);
		model.addAttribute("getAllComp", service.getAllActiveCompetitions(partNumber, COMPETITIONS_PER_PAGE));		
		return "main";
	}
	
	@RequestMapping(value = "/register_in_comp",method = RequestMethod.GET)
	public String takePartInCompettion(Model model, @Autowired HealthBodyServiceImplService healthBody,
			String nameCompetition){	
		String uLogin = SecurityContextHolder.getContext().getAuthentication().getName();
		
		HealthBodyService service = healthBody.getHealthBodyServiceImplPort();		
		service.addUserInCompetition(nameCompetition, uLogin);		
		model.addAttribute("user", service.getUserByLogin(uLogin));
		return "redirect:main.html";
	}

}
