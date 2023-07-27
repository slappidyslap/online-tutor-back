package kg.musabaev.onlinetutorback.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CommentNotFoundException extends RuntimeException {
	public CommentNotFoundException() {
		super(CommentNotFoundException.class.getSimpleName());
	}
}
