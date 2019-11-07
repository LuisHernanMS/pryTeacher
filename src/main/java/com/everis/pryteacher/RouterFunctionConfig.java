package com.everis.pryteacher;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.everis.pryteacher.controller.TeacherController;
import com.everis.pryteacher.handler.TeacherHandler;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class RouterFunctionConfig {
	
	@Bean
	public RouterFunction<ServerResponse> routes(TeacherController controller){
		return RouterFunctions.route(GET("/api/teacher"), controller::listar)
				.andRoute(GET("/api/teacher/{name}"), controller::ver)
				.andRoute(GET("/api/teacher/fecha/{start}/{end}"), controller::verfec)
				//.andRoute(GET("/api/v2/students/{numberDocument}"), handler::vern)
				.andRoute(POST("/api/teacher"), controller::crear)
				.andRoute(PUT("/api/teacher/{id}"), controller::editar)
				.andRoute(DELETE("/api/teacher/{numberDocument}"), controller::eliminar);
	}
}
