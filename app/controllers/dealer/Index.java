package controllers.dealer;

import play.mvc.Result;
import play.mvc.Controller;

import views.html.dealer.index;

/**
 * Created by ruraj on 7/17/14.
 */
public class Index extends Controller {


    public static Result index() {
        return ok(index.render());
    }
}
