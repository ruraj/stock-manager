package controllers.account.settings

import controllers.Secured
import controllers.account.settings.Password._
import models.User
import play.api.mvc.Controller
import views.html.account.settings.password

/**
 * Index Settings page.
 *
 * User: yesnault
 * Date: 15/05/12
 */
object Index extends Controller with Secured {
  /**
   * Main page set
   *
   * @return index settings
   */
  def index = withAuth { username => implicit request =>
    Ok(password(User.findByEmail(username)))
  }
}