package app.views;

import app.services.AuthService;
import app.services.SearchService;
import com.google.gson.Gson;
import org.linkSphere.annotations.UseLogger;
import org.linkSphere.annotations.http.Endpoint;
import org.linkSphere.annotations.http.Get;
import org.linkSphere.annotations.useGson;
import org.linkSphere.http.dto.Req;
import org.linkSphere.http.dto.Res;
import org.linkSphere.util.Logger;

import java.util.HashMap;
import java.util.List;

@Endpoint("/search")
@useGson
@UseLogger
public class Search {
    private static Gson gson;
    private static Logger logger;

    @Get("/{searchQuery}")
    public void search(Req req, Res res) {
        if (!AuthService.isAuthorized(req.getCookies())) {
            res.sendError(401, "Unauthorized");
            return;
        }

        String searchQuery = req.getDynamicParameters().get("searchQuery");
        HashMap<String, String> queries = req.getQueryParameters();
        logger.debug("Searched \"" + searchQuery + "\" and " + queries + " queries");
        int limit = 30;
        String searchType = queries.get("type");
        if (searchType == null)
            searchType = "users";

        if (queries.containsKey("limit")) {
            try {
                int tempLimit = Integer.parseInt(queries.get("limit"));
                if (tempLimit <= 30 && tempLimit > 0) {
                    limit = tempLimit;
                } else {
                    logger.info("Ignoring search limit query because it's not valid");
                }
            } catch (NumberFormatException e) {
                logger.info("Ignoring search limit query because it's not valid");
            }
        }
        List<Object[]> results = null;
        if (searchType.equals("companies")) {
            results = SearchService.SearchCompanies(searchQuery, limit);
        } else {
            results = SearchService.SearchUsers(searchQuery, limit);
        }

        res.send(200, "{\"code\": 200, \"results\": \"" + gson.toJson(results, List.class) + "\"}");
    }
}
