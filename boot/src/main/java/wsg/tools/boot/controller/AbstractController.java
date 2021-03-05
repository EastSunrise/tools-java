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

    static final String ERROR_NOT_FOUND = "error/notFound";

    static final ResponseEntity.BodyBuilder OK = ResponseEntity.status(HttpStatus.OK);
    static final ResponseEntity.BodyBuilder NOT_FOUND = ResponseEntity.status(HttpStatus.NOT_FOUND);
    static final ResponseEntity.BodyBuilder SERVER_ERROR = ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR);
    static final ResponseEntity.BodyBuilder BAD_REQUEST = ResponseEntity
        .status(HttpStatus.BAD_REQUEST);
}
