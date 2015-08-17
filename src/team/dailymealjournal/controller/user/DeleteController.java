package team.dailymealjournal.controller.user;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;

public class DeleteController extends Controller {

    @Override
    public Navigation run() throws Exception {
        return forward("/user/journal_detail.html");
    }
}