package quizmasters.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason="Debe haber al menos un administrador")
public class UltimoAdministradorException extends RuntimeException {
}
