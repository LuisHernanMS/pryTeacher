package com.everis.pryteacher.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import static org.springframework.web.reactive.function.BodyInserters.*;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.everis.pryteacher.documents.Teacher;
import com.everis.pryteacher.services.TeacherService;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

import reactor.core.publisher.Mono;

@Component
public class TeacherHandler {
	
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
			return null;
		}	
	}
	
	/*public Mono<ServerResponse> vern(ServerRequest request){
		String numberDocument = request.pathVariable("numberDocument");
		return service.findByNumberDocument(numberDocument).flatMap(p -> ServerResponse
				.ok()
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.body(fromObject(p)))
				.switchIfEmpty(ServerResponse.notFound().build());
	}*/
	
	
	public Mono<ServerResponse> crear(ServerRequest request){
	
		Mono<Teacher> teacher= request.bodyToMono(Teacher.class);
		
		return teacher.flatMap(p->{
			Mongo mongo = new Mongo("localhost", 27017);
			DB db = mongo.getDB("pryeveris");
			DBCollection dbCollection = db.getCollection("teacher");
			DBObject query = new BasicDBObject("numberDocument", p.getNumberDocument());
			Integer result = dbCollection.find(query).count(); 
			if (result>0) {
				System.out.println(result + " existe");
				return null;
			}else {
				System.out.println(result + " no existe");
				return service.save(p);
			}	
		}).flatMap(p->ServerResponse.created(URI.create("api/v2/teacher/".concat(p.getId())))
				.body(fromObject(p)));
	}
	
	public Mono<ServerResponse> editar(ServerRequest request){
		Mono<Teacher> student= request.bodyToMono(Teacher.class);
		String id = request.pathVariable("id");
		
		Mono<Teacher> studentDB = service.findById(id);
		
		return studentDB.zipWith(student, (db,req)->{
			db.setName(req.getName());
			db.setFlastName(req.getFlastName());
			db.setMlastName(req.getMlastName());
			db.setBirth(req.getBirth());
			db.setDocument(req.getDocument());
			db.setNumberDocument(req.getNumberDocument());
			db.setGender(req.getGender());
			return db;
		}).flatMap(p->ServerResponse.created(URI.create("/api/v2/teacher".concat(p.getId())))
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.body(service.save(p),Teacher.class))
				.switchIfEmpty(ServerResponse.notFound().build());
	}
	
	public Mono<ServerResponse> eliminar(ServerRequest request){
		String id = request.pathVariable("id");
		Mono<Teacher> studentDB = service.findById(id);
		
		return studentDB.flatMap(p->service.delete(p).then(ServerResponse.noContent().build()))
				.switchIfEmpty(ServerResponse.notFound().build());
	}

	
}
