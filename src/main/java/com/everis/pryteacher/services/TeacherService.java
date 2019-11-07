package com.everis.pryteacher.services;

import java.util.Date;

import com.everis.pryteacher.documents.Teacher;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TeacherService {

	public Flux<Teacher> findAll();
	
	public Mono<Teacher> findById(String id);
	
	public Mono<Teacher> save(Teacher student);
	
	public Mono<Void> delete(Teacher student);
	
	public Flux<Teacher> findByName(String name);
	
	public Mono<Teacher> findByNumberDocument(String numberDocument);
	
	public Flux<Teacher> findByDate(Date start, Date end);
}
