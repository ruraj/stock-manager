package controllers.account.settings

import controllers.Secured
import models.Token
import models.User
import play.api.Logger
import play.api.i18n.Messages
import play.api.mvc.{Flash, Controller}
import views.html.account.settings.password
import java.net.MalformedURLException

/**
 * User: yesnault
 * Date: 15/05/12
 */
object Password extends Controller with Secured{
  /**
   * Password Page. Ask the user to change his password.
   *
   * @return index settings
   */
  def index = withAuth { username => implicit request =>
    Ok(password(User.findByEmail(username)))
  }

  /**
   * Send a mail with the reset link.
   *
   * @return password page with flash error or success
   */
  def runPassword = withAuth { username => implicit request =>
    val user: User = User.findByEmail(username)
    try {
      Token.sendMailResetPassword(user)
      Flash( Map("success"->Messages("resetpassword.mailsent"))).get("success").getOrElse("")
      Ok(password(user))
    }
    catch {
      case e: MalformedURLException => {
        Logger.error("Cannot validate URL")
        Flash( Map("error"->Messages("error.technical")))
      }
    }
    BadRequest(password(user))
  }
}