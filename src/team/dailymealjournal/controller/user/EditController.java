package team.dailymealjournal.controller.user;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;

public class EditController extends Controller {

    @Override
    public Navigation run() throws Exception {
        this.requestScope("mealId");
        this.requestScope("journalId");
        return forward("/user/journal_detail.html");
    }
}
