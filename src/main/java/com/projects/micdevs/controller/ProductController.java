package com.projects.micdevs.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projects.micdevs.dtos.ProductRecordDto;
import com.projects.micdevs.entities.Product;
import com.projects.micdevs.repositories.ProductRepository;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import jakarta.validation.Valid;

@RestController
public class ProductController {
	
	@Autowired
	private ProductRepository repository;
	
	
	@PostMapping("/products")
	public ResponseEntity<Product> saveProduct(@RequestBody @Validated ProductRecordDto productRecordDto) {
		Product p = new Product();
		BeanUtils.copyProperties(productRecordDto, p);
		return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(p));
	}
	@GetMapping("/products")
	public ResponseEntity<List<Product>> allProducts() {
		List<Product> list = repository.findAll();
		if(!list.isEmpty()) {
			for(Product x: list) {
				Long id = x.getId();
				x.add(linkTo(methodOn(ProductController.class).findById(id)).withSelfRel());
				
			}
		}
		return ResponseEntity.ok().body(list);
	}
	
	@GetMapping("/products/{id}")
	public ResponseEntity<Object> findById(@PathVariable(value = "id") Long id) {
		Optional<Product> p = repository.findById(id);
		if(p.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product Not Found");
		}
		p.get().add(linkTo(methodOn(ProductController.class).allProducts()).withRel("Product List"));
		return ResponseEntity.status(HttpStatus.OK).body(p);
	}
	
	@PutMapping("/products/{id}")
	public ResponseEntity<Object> updateProduct(@PathVariable(value = "id") Long id,@RequestBody @Valid ProductRecordDto productRecord) {
		Optional<Product> p = repository.findById(id);
		if(p.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not Found");
		}
		Product productMod = p.get();
		BeanUtils.copyProperties(productRecord, productMod);
		return ResponseEntity.status(HttpStatus.OK).body(repository.save(productMod));
	}
	
	@DeleteMapping("/products/{id}")
	public ResponseEntity<Object> deleteById(@PathVariable(value = "id")Long id) {
		Optional<Product> p = repository.findById(id);
		if(p.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not Found");
		}
		repository.deleteById(p.get().getId());
		return ResponseEntity.status(HttpStatus.OK).body("Deleted");
	}
}
