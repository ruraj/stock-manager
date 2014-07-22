package controllers.account.settings

import controllers.Secured
import models.Token
import models.User
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages
import play.api.mvc.{Session, Flash, Controller}
import views.html.account.settings.email
import views.html.account.settings.emailValidate
import java.net.MalformedURLException

/**
 * Settings -> Email page.
 * <p/>
 * User: yesnault
 * Date: 23/06/12
 */
object Email extends Controller with Secured {
  /**
   * Password Page. Ask the user to change his password.
   *
   * @return index settings
   */
  def index = withAuth { username => implicit request =>
    val user: User = User.findByEmail(username)
    val askForm = AskForm.bind(Map("email" -> user.email))
    Ok(email(User.findByEmail(username), askForm))
  }

  /**
   * Send a mail to confirm.
   *
   * @return email page with flash error or success
   */
  def runEmail = withAuth { username => implicit request =>
    val askForm = AskForm.bindFromRequest
    val user: User = User.findByEmail(username)
    if (askForm.hasErrors) {
      flash("error", Messages("signup.valid.email"))
      BadRequest(email(user, askForm))
    }
    try {
      val mail: String = askForm.get._1
      Token.sendMailChangeMail(user, mail)
      flash("success", Messages("changemail.mailsent"))
      Ok(email(user, askForm))
    }
    catch {
      case e: MalformedURLException => {
        Logger.error("Cannot validate URL")
        flash("error", Messages("error.technical"))
      }
    }
    BadRequest(email(user, askForm))
  }

  /**
   * Validate a email.
   *
   * @return email page with flash error or success
   */
  def validateEmail(token: String) = withAuth { username => implicit request =>
    val user: User = User.findByEmail(username)
    if (token == null) {
      flash("error", Messages("error.technical"))
      BadRequest(emailValidate(user))
    }
    val resetToken: Token = Token.findByTokenAndType(token, Token.TypeToken.email)
    if (resetToken == null) {
      flash("error", Messages("error.technical"))
      BadRequest(emailValidate(user))
    }
    if (resetToken.isExpired) {
      resetToken.delete
      flash("error", Messages("error.expiredmaillink"))
      BadRequest(emailValidate(user))
    }
    user.email_(resetToken.email)
    user.save
    Session() + ("email", resetToken.email)
    flash("success", Messages("account.settings.email.successful", user.email))
    Ok(emailValidate(user))
  }

  val AskForm = Form(
    tuple(
      "email" -> nonEmptyText,
      "dummy" -> text
    )
  )
  def flash(tpe: String, message: String) = Flash ( Map( (tpe, message)))
}