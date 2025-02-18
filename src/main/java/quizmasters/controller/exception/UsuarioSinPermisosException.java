package quizmasters.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason="Usuario no administrador")
public class UsuarioSinPermisosException extends RuntimeException {
}
