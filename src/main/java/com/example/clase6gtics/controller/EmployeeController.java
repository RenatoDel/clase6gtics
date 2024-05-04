package com.example.clase6gtics.controller;

import com.example.clase6gtics.entity.Employee;
import com.example.clase6gtics.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/employee")
public class EmployeeController {

    private final EmployeeRepository employeeRepository;

    public EmployeeController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @GetMapping(value = {"", "/"})
    public String listaEmpleados(Model model) {
        model.addAttribute("listaEmpleados", employeeRepository.findAll());
        return "employee/list";
    }

    @GetMapping("/new")
    public String nuevoEmpleadoFrm(Model model, @ModelAttribute("employee") Employee employee) {
        model.addAttribute("listaJefes", getListaJefes());
        return "employee/editFrm";
    }

    @PostMapping("/save")
    public String guardarEmpleado(RedirectAttributes attr, @ModelAttribute("employee") Employee employee,
                                  @RequestParam("birthdateStr") String birthdateStr,
                                  @RequestParam("hiredateStr") String hiredateStr) {

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        try {
            employee.setBirthdate(formatter.parse(birthdateStr));
            employee.setHiredate(formatter.parse(hiredateStr));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        employeeRepository.save(employee);
        attr.addFlashAttribute("msg", "Empleado guardado exitosamente");
        return "redirect:/employee";
    }

    @GetMapping("/edit")
    public String editarEmpleado(Model model, @ModelAttribute("employee") Employee employee,
                                 @RequestParam("id") int id) {
        Optional<Employee> optional = employeeRepository.findById(id);

        if (optional.isPresent()) {
            employee = optional.get();
            model.addAttribute("employee", employee);
            model.addAttribute("listaJefes", getListaJefes());
            return "employee/editFrm";
        } else {
            return "redirect:/employee";
        }
    }

    @GetMapping("/delete")
    public String borrarEmpleado(@RequestParam("id") int id, RedirectAttributes attr) {
        Optional<Employee> optional = employeeRepository.findById(id);

        if (optional.isPresent()) {
            employeeRepository.deleteById(id);
            attr.addFlashAttribute("msg", "Empleado borrado exitosamente");
        }
        return "redirect:/employee";
    }

    public List<Employee> getListaJefes() {
        List<Employee> listaJefes = employeeRepository.findAll();
        Employee e = new Employee();
        e.setId(0);
        e.setFirstname("--No tiene Jefe--");
        listaJefes.add(0, e);
        return listaJefes;
    }
}