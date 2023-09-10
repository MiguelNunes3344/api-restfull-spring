package com.projects.micdevs.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projects.micdevs.entities.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
