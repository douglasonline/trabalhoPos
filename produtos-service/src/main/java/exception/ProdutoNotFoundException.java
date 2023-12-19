package exception;


import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class ProdutoNotFoundException extends WebApplicationException {

    public ProdutoNotFoundException(String message) {
        super(Response.status(Response.Status.NOT_FOUND)
                .entity(message)
                .type(MediaType.TEXT_PLAIN)
                .build());
    }
}