package controllers.dealer.settings

import com.avaje.ebean.Ebean
import controllers.Secured
import controllers.dealer.routes
import models.{Db, Dealer}
import play.api.mvc.Controller
import play.mvc.Result
import views.html.dealer.settings.index
import play.mvc.Results.ok
import play.mvc.Results.redirect

/**
 * Created by ruraj on 7/17/14.
 */
object Index extends Controller with Secured {
  def index(dealerId: Long) = withAuth { username => implicit request =>
    if (dealerId == 0) {
      Redirect(routes.Index.index())
    }
    val dealer: Dealer = Db.fetchById[Dealer](dealerId)
    Ok(views.html.dealer.settings.index(dealer))
  }
}