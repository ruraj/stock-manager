package controllers

import models.User
import play.api.mvc.Controller

/**
 * User: yesnault
 * Date: 22/01/12
 */
object Dashboard extends Controller with Secured {
  def index = withAuth { username => implicit request =>
    Ok(views.html.dashboard.index(User.findByEmail(username)))
  }
}