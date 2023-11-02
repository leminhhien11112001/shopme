package com.shopme.admin.agency.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.agency.AgencyService;
import com.shopme.common.entity.Agency;
import com.shopme.common.exception.AgencyNotFoundException;

@Controller
public class AgencyController {

	@Autowired
	private AgencyService service;
	
	@GetMapping("/agencies")
	public String listFirstPage(Model model) {
		return listByPage(model, 1, "name", "asc", null);
	}

	@GetMapping("/agencies/page/{pageNum}")
	public String listByPage(Model model, @PathVariable(name = "pageNum") int pageNum, 
						String sortField, String sortDir, String keyword) {

		Page<Agency> page = service.listByPage(pageNum, sortField, sortDir, keyword);
		List<Agency> listAgencies = page.getContent();

		long startCount = (pageNum - 1) * AgencyService.AGENCIES_PER_PAGE + 1;
		model.addAttribute("startCount", startCount);

		long endCount = startCount + AgencyService.AGENCIES_PER_PAGE - 1;
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}

		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("listAgencies", listAgencies);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		model.addAttribute("endCount", endCount);

		return "agencies/agencies";
	}

	@GetMapping("/agencies/{id}/enabled/{status}")
	public String updateAgencyEnabledStatus(@PathVariable("id") Integer id,
			@PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes) {
		
		service.updateAgencyEnabledStatus(id, enabled);
		
		String status = enabled ? "enabled" : "disabled";
		String message = "The Agency ID " + id + " has been " + status;
		
		redirectAttributes.addFlashAttribute("message", message);

		return "redirect:/agencies";
	}
	
	@GetMapping("/agencies/edit/{id}")
	public String editAgency(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
		try {
			Agency agency = service.get(id);
		
			model.addAttribute("agency", agency);
			model.addAttribute("pageTitle", String.format("Edit Agency (ID: %d)", id));

			return "agencies/agency_form";

		} catch (AgencyNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
			return "redirect:/agencies";
		}
	}
	
	@PostMapping("/agencies/save")
	public String saveAgency(Agency agency, Model model, RedirectAttributes ra) {
		service.save(agency);
		
		ra.addFlashAttribute("message", "The agency ID " + agency.getId() + " has been updated successfully.");
		
		return "redirect:/agencies";
	}
}
