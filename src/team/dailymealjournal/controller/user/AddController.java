package team.dailymealjournal.controller.user;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;

public class AddController extends Controller {

    @Override
    public Navigation run() throws Exception {
        return forward("/user/add_meal.html");
    }
}