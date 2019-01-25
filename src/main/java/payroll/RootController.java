package payroll;

import org.springframework.hateoas.ResourceSupport;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping("/")
class RootController {

    @GetMapping
    ResourceSupport index() {
        ResourceSupport rootResource = new ResourceSupport();
        rootResource.add(linkTo(methodOn(EmployeeController.class).all()).withRel("employees"));
        rootResource.add(linkTo(methodOn(OrderController.class).all()).withRel("orders"));
        return rootResource;
    }

}