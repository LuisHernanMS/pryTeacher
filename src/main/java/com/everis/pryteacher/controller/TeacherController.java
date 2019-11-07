package com.everis.pryteacher.controller;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.everis.pryteacher.documents.Teacher;
import com.everis.pryteacher.services.TeacherService;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/teacher")
public class TeacherController {
	
	@Autowired
	private TeacherService service;
	
	
	
	public Mono<ServerResponse> listar(ServerRequest request){
		return ServerResponse.ok()
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.body(service.findAll(), Teacher.class)
				.switchIfEmpty(ServerResponse.notFound().build());
	}
	
	public Mono<ServerResponse> ver(ServerRequest request){
		String name = request.pathVariable("name");
		
			return ServerResponse.ok()
					.contentType(MediaType.APPLICATION_JSON_UTF8)
					.body(service.findByName(name), Teacher.class)
					.switchIfEmpty(ServerResponse.notFound().build());
	}
	
	public Mono<ServerResponse> verfec(ServerRequest request){
		String start = request.pathVariable("start");
		String end = request.pathVariable("end");
		start=start+" 00:00:00.000 +0000";
		end=end+" 00:00:00.000 +0000";
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z");
		try {
			Date inicio=df.parse(start);
			Date fin=df.parse(end);
			return ServerResponse.ok()
					.contentType(MediaType.APPLICATION_JSON_UTF8)
					.body(service.findByDate(inicio, fin), Teacher.class)
					.switchIfEmpty(ServerResponse.notFound().build());
		} catch (Exception e) {
			ServerResponse.notFound().build();
			return Mono.error(e);
		}	
	}
	
	public Mono<ServerResponse> crear(ServerRequest request){
		
		Mono<Teacher> student= request.bodyToMono(Teacher.class);
		
		return student.flatMap(p->{
				return service.save(p);
		}).flatMap(p->ServerResponse.created(URI.create("api/teacher/".concat(p.getId())))
				.body(fromObject(p)));
	}
	
	public Mono<ServerResponse> editar(ServerRequest request){
		Mono<Teacher> student= request.bodyToMono(Teacher.class);
		String id = request.pathVariable("id");
		
		Mono<Teacher> teacherDB = service.findById(id);
		
		return teacherDB.zipWith(student, (db,req)->{
			db.setName(req.getName());
			db.setFlastName(req.getFlastName());
			db.setMlastName(req.getMlastName());
			db.setBirth(req.getBirth());
			db.setDocument(req.getDocument());
			db.setNumberDocument(req.getNumberDocument());
			db.setGender(req.getGender());
			return db;
		}).flatMap(p->ServerResponse.created(URI.create("/api/teacher".concat(p.getId())))
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.body(service.save(p),Teacher.class))
				.switchIfEmpty(ServerResponse.notFound().build());
	}
	
	public Mono<ServerResponse> eliminar(ServerRequest request){
		String numberDocument = request.pathVariable("numberDocument");
		Mono<Teacher> teacherDB = service.findByNumberDocument(numberDocument);
		return teacherDB.flatMap(q->service.delete(q).then(ServerResponse.noContent().build()))
				.switchIfEmpty(ServerResponse.notFound().build());
	}
	
}
