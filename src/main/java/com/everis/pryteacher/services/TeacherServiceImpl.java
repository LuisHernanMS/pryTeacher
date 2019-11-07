package com.everis.pryteacher.services;

import java.util.Date;

import javax.naming.ServiceUnavailableException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.everis.pryteacher.dao.TeacherDao;
import com.everis.pryteacher.documents.Teacher;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TeacherServiceImpl implements TeacherService{

	@Autowired
	private TeacherDao dao;
	
	@Override
	public Flux<Teacher> findAll() {
		// TODO Auto-generated method stub
		return dao.findAll();
	}

	@Override
	public Mono<Teacher> findById(String id) {
		// TODO Auto-generated method stub
		return dao.findById(id);
	}
	

	@Override
	public Mono<Teacher> save(Teacher teacher) {
		
		Mongo mongo = new Mongo("localhost", 27017);
		DB db = mongo.getDB("pryeveris");
		DBCollection dbCollection = db.getCollection("teacher");
		DBObject query = new BasicDBObject("numberDocument", teacher.getNumberDocument());
		Integer result = dbCollection.find(query).count(); 
		if (result>0) {
			return Mono.error(new ServiceUnavailableException("There is a teacher registered with the ID "+teacher.getNumberDocument()));
		}else {
			return dao.save(teacher);
		}	
		
		
	}

	@Override
	public Mono<Void> delete(Teacher teacher) {
		// TODO Auto-generated method stub
		return dao.delete(teacher);
	}

	@Override
	public Flux<Teacher> findByName(String name) {
		// TODO Auto-generated method stub
		return dao.obtenerPorName(name);
	}
	
	@Override
	public Mono<Teacher> findByNumberDocument(String numberDocument) {
		// TODO Auto-generated method stub
		return dao.obtenerPorNumberDocument(numberDocument);
	}

	@Override
	public Flux<Teacher> findByDate(Date start, Date end) {
		return dao.obtenerPorDate(start, end);
	}
}
