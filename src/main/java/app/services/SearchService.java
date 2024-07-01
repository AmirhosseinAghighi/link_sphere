package app.services;

import app.database.ProfileDAO;
import app.database.SearchDAO;
import app.database.UserDAO;
import org.linkSphere.annotations.Inject;

import java.util.List;

public class SearchService {
    @Inject(dependency = "userDAO")
    private static UserDAO userDAO;

    @Inject(dependency = "profileDAO")
    private static ProfileDAO profileDAO;

    @Inject(dependency = "searchDAO")
    private static SearchDAO searchDAO;

    public static List<Object[]> SearchUsers(String query, int limit) {
        List<Object[]> users = searchDAO.SearchUsers(query, limit);
        return users;
    }

    public static List<Object[]> SearchCompanies(String query, int limit) {
        List<Object[]> companies = searchDAO.SearchCompanies(query, limit);
        return companies;
    }
}
