package controllers.dealer.settings;

import com.avaje.ebean.Ebean;
import controllers.dealer.routes;
import models.Dealer;
import play.mvc.Result;

import views.html.dealer.settings.index;

import static play.mvc.Results.ok;
import static play.mvc.Results.redirect;

/**
 * Created by ruraj on 7/17/14.
 */
public class Index {

    public static Result index(Integer dealerId) {

        if (dealerId == null) {
            return redirect(routes.Index.index());
        }
        Dealer dealer = Ebean.find(Dealer.class, dealerId);

        return ok(index.render(dealer));
    }
}
