package controllers

import play.api.Logger
import play.api.mvc._

import models.{Db, User}

/**
 * User: yesnault
 * Date: 22/01/12
 */
trait Secured {

  def username(request: RequestHeader) = request.session.get(Security.username)

  def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Application.index)

  def withAuth(f: => String => Request[AnyContent] => Result) = {
    Security.Authenticated(username, onUnauthorized) { user =>
      Logger.debug("Dashboard with user "+user)
      Action(request => f(user)(request))
    }
  }

//  /**
//   * This method shows how you could wrap the withAuth method to also fetch your user
//   * You will need to implement UserDAO.findOneByUsername
//   */
//  def withUser(f: User => Request[AnyContent] => Result) = withAuth { username => implicit request =>
//      Db.query[User].whereEqual("email", username).fetchOne().getOrElse(onUnauthorized(request))
//  }
}