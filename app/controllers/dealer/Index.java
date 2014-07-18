package controllers.dealer;

import com.avaje.ebean.Ebean;
import controllers.Secured;
import models.Dealer;
import play.mvc.Result;
import play.mvc.Controller;

import play.mvc.Security;
import views.html.dealer.index;
import views.html.dealer.detail;

import java.util.List;

/**
 * Created by ruraj on 7/17/14.
 */
//@Security.Authenticated(Secured.class)
public class Index extends Controller {


    public static Result index() {
        List<Dealer> dealers = new Dealer.Finder(Integer.class, Dealer.class).all();
        return ok(index.render(dealers));
    }

    public static Result detail(Integer id) {
        return ok(detail.render(Ebean.find(Dealer.class, id)));
    }
}
