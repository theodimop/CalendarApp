package httpserver;

import json_adaptor.JsonAdaptor;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * Server is the end point, accessible from the web.
 */

@Path("/") public class Server {

    /**
     * Returns the result of the jsonAdaptor. it is at /request.
     * @param request the json request.
     * @return the json response.
     */
    @POST @Path("/request") @Consumes({"text/plain"}) @Produces("text/plain") public String getMessage(@HeaderParam("request") String request) {
        return new JsonAdaptor().request(request);
    }
}
