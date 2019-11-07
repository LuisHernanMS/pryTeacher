package com.everis.pryteacher.dao;

import java.util.Date;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.everis.pryteacher.documents.Teacher;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TeacherDao extends ReactiveMongoRepository<Teacher, String>{
	
	public Flux<Teacher> findByName(String name);
	
	@Query("{ $or : [{ name : ?0  }, { numberDocument : ?0  }, { id : ?0}]}")
	public Flux<Teacher> obtenerPorName(String name);
	
	public Mono<Teacher> findByNumberDocument(String numberDocument);
	
	@Query("{ numberDocument : ?0  }")
	public Mono<Teacher> obtenerPorNumberDocument(String numberDocument);

	//public Flux<student> findByDate(String start, String end);
	
	@Query("{ birth : {$gt : ?0, $lt : ?1}  }")
	public Flux<Teacher> obtenerPorDate(Date start, Date end);
}
