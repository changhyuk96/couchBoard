package couchBoard.util;

import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.couchbase.client.core.error.subdoc.PathNotFoundException;

@RestControllerAdvice
public class cbExceptionAdvice {

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public Object notReuqestException(HttpMessageNotReadableException e) {
		return "Error::: "+ e.getMessage().substring(0, e.getMessage().indexOf(":"));
	}
	
	@ExceptionHandler(PathNotFoundException.class)
	public Object PathNotFoundExceptionCatch(PathNotFoundException e) {
		return "Error::: "+ e.getMessage().substring(0, e.getMessage().indexOf(":"));
	}
	
	// 405
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public Object MethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
		return "Error::: " + e.getLocalizedMessage();
	}
}
