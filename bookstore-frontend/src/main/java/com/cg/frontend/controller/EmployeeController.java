package com.cg.frontend.controller;

import com.cg.frontend.dto.EmployeeDto;
import com.cg.frontend.dto.JobDto;
import com.cg.frontend.dto.PageMetaDto;
import com.cg.frontend.service.EmployeeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public String listEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(required = false) String search,
            Model model) {

        List<EmployeeDto> employees;
        PageMetaDto pageMeta;

        if (search != null && !search.isBlank()) {
            employees = employeeService.searchEmployees(search);
            pageMeta = new PageMetaDto();
            pageMeta.setTotalElements(employees.size());
            pageMeta.setTotalPages(1);
            pageMeta.setNumber(0);
            pageMeta.setSize(employees.size());
        } else {
            employees = employeeService.getAllEmployees(page, size);
            pageMeta = employeeService.getPageMeta(page, size);
        }

        model.addAttribute("employees", employees);
        model.addAttribute("pageMeta", pageMeta);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("search", search);
        model.addAttribute("newEmployee", new EmployeeDto());
        model.addAttribute("pageTitle", "Employees — Swati's Module");
        return "employees/list";
    }

    @GetMapping("/{empId}")
    public String viewEmployee(@PathVariable String empId, Model model) {
        EmployeeDto employee = employeeService.getEmployeeById(empId);
        if (employee == null) {
            return "redirect:/employees";
        }
        JobDto job = employeeService.getJobById(employee.getJobId());
        List<EmployeeDto> colleagues = job != null
                ? employeeService.getEmployeesByJobId(employee.getJobId())
                : java.util.Collections.emptyList();

        model.addAttribute("employee", employee);
        model.addAttribute("job", job);
        model.addAttribute("colleagues", colleagues);
        model.addAttribute("pageTitle", "Employee Detail — " + employee.getFullName());
        return "employees/detail";
    }

    @PostMapping("/create")
    public String createEmployee(@ModelAttribute EmployeeDto employee, RedirectAttributes ra) {
        String error = employeeService.createEmployee(employee);
        if (error != null) {
            ra.addFlashAttribute("errorMsg", error);
            return "redirect:/employees?error=create";
        }
        return "redirect:/employees?success=create";
    }

    @PostMapping("/update/{empId}")
    public String updateEmployee(@PathVariable String empId, @ModelAttribute EmployeeDto employee, RedirectAttributes ra) {
        employee.setEmpId(empId);
        String error = employeeService.updateEmployee(empId, employee);
        if (error != null) {
            ra.addFlashAttribute("errorMsg", error);
            return "redirect:/employees?error=update";
        }
        return "redirect:/employees?success=update";
    }
}
