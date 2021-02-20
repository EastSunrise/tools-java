package wsg.tools.boot.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Base controller
 *
 * @author Kingen
 * @since 2020/6/22
 */
public abstract class AbstractController {

    protected static final ResponseEntity.BodyBuilder OK = ResponseEntity.status(HttpStatus.OK);
    protected static final ResponseEntity.BodyBuilder NOT_FOUND = ResponseEntity.status(HttpStatus.NOT_FOUND);
    protected static final ResponseEntity.BodyBuilder SERVER_ERROR = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR);
    protected static final ResponseEntity.BodyBuilder BAD_REQUEST = ResponseEntity.status(HttpStatus.BAD_REQUEST);
}
