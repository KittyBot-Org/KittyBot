package de.kittybot.kittybot.exceptions;

import io.javalin.http.HttpResponseException;
import org.eclipse.jetty.http.HttpStatus;

import java.util.HashMap;

public class TooManyRequestResponse extends HttpResponseException{

	public TooManyRequestResponse(String message){
		super(HttpStatus.TOO_MANY_REQUESTS_429, message, new HashMap<>());
	}

}
