package payroll.controllers;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import payroll.data.Status;
import payroll.data.entity.Order;
import payroll.exceptions.OrderNotFoundException;
import payroll.repositories.OrderRepository;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

// tag::main[]
@RestController
@RequestMapping(value = "/orders", produces = MediaType.APPLICATION_JSON_VALUE)
public class OrderController {

	private final OrderRepository orderRepository;
	private final OrderResourceAssembler assembler;

	public OrderController(OrderRepository orderRepository,
						   OrderResourceAssembler assembler) {

		this.orderRepository = orderRepository;
		this.assembler = assembler;
	}

	@GetMapping("/")
	public Resources<Resource<Order>> all() {

		List<Resource<Order>> orders = orderRepository.findAll().stream()
				.map(assembler::toResource)
				.collect(Collectors.toList());

		return new Resources<>(orders,
				linkTo(methodOn(OrderController.class).all()).withSelfRel());
	}

	@GetMapping("/{id}")
	public Resource<Order> one(@PathVariable Long id) {
		return assembler.toResource(
				orderRepository.findById(id)
						.orElseThrow(() -> new OrderNotFoundException(id)));
	}

	@PostMapping("/")
	public ResponseEntity<Resource<Order>> newOrder(@RequestBody Order order) {

		order.setStatus(Status.IN_PROGRESS);
		Order newOrder = orderRepository.save(order);

		return ResponseEntity
				.created(linkTo(methodOn(OrderController.class).one(newOrder.getId())).toUri())
				.body(assembler.toResource(newOrder));
	}
	// end::main[]

	// tag::delete[]
	@DeleteMapping("/{id}/cancel")
	public ResponseEntity<ResourceSupport> cancel(@PathVariable Long id) {

		Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));

		if (order.getStatus() == Status.IN_PROGRESS) {
			order.setStatus(Status.CANCELLED);
			return ResponseEntity.ok(assembler.toResource(orderRepository.save(order)));
		}

		return ResponseEntity
				.status(HttpStatus.METHOD_NOT_ALLOWED)
				.body(new VndErrors.VndError("Method not allowed", "You can't cancel an order that is in the " + order.getStatus() + " status"));
	}
	// end::delete[]

	// tag::complete[]
	@PutMapping("/{id}/complete")
	public ResponseEntity<ResourceSupport> complete(@PathVariable Long id) {

		Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));

		if (order.getStatus() == Status.IN_PROGRESS) {
			order.setStatus(Status.COMPLETED);
			return ResponseEntity.ok(assembler.toResource(orderRepository.save(order)));
		}

		return ResponseEntity
				.status(HttpStatus.METHOD_NOT_ALLOWED)
				.body(new VndErrors.VndError("Method not allowed", "You can't complete an order that is in the " + order.getStatus() + " status"));
	}
	// end::complete[]
}
