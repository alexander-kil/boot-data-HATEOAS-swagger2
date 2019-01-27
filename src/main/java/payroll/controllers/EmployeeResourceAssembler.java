package payroll.controllers;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;
import payroll.data.entity.Employee;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
class EmployeeResourceAssembler implements ResourceAssembler<Employee, Resource<Employee>> {

	@Override
	public Resource<Employee> toResource(Employee employee) {
		return new Resource<>(employee,
			linkTo(methodOn(EmployeeController.class).one(employee.getId())).withSelfRel(),
			linkTo(methodOn(EmployeeController.class).all()).withRel("employees"));
	}
}
