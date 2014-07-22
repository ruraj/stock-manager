package controllers.account

import models.Token
import models.User
import models.utils.AppException
import models.utils.Mail
import org.apache.commons.mail.EmailException
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.data.validation.Constraints
import play.api.i18n.Messages
import play.api.mvc.{Flash, Action, Controller, Result}
import views.html.account.reset.ask
import views.html.account.reset.reset
import views.html.account.reset.runAsk
import java.net.MalformedURLException

import views.html.defaultpages.badRequest

/**
 * Token password :
 * - ask for an email address.
 * - send a link pointing them to a reset page.
 * - show the reset page and set them reset it.
 * <p/>
 * <p/>
 * User: yesnault
 * Date: 20/01/12
 */
object Reset extends Controller {
  /**
   * Display the reset password form.
   *
   * @return reset password form
   */
  def ask = Action { implicit request =>
    Ok(views.html.account.reset.ask(AskForm))
  }

  /**
   * Run ask password.
   *
   * @return reset password form if error, runAsk render otherwise
   */
  def runAsk = Action { implicit request =>
    val askForm: Form[Ask] = AskForm.bindFromRequest
    if (askForm.hasErrors) {
      flash("error", Messages("signup.valid.email"))
      BadRequest(views.html.account.reset.ask(askForm))
    }
    val email: String = askForm.get.email
    Logger.debug("runAsk: email = " + email)
    val user: User = User.findByEmail(email)
    Logger.debug("runAsk: user = " + user)
    if (user == null) {
      Logger.debug("No user found with email " + email)
      sendFailedPasswordResetAttempt(email)
      Ok(views.html.account.reset.runAsk())
    }
    Logger.debug("Sending password reset link to user " + user)
    try {
      Token.sendMailResetPassword(user)
      Ok(views.html.account.reset.runAsk())
    }
    catch {
      case e: MalformedURLException => {
        Logger.error("Cannot validate URL", e)
        flash("error", Messages("error.technical"))
      }
    }
    BadRequest(views.html.account.reset.ask(askForm))
  }

  /**
   * Sends an email to say that the password reset was to an invalid email.
   *
   * @param email the email address to send to.
   */
  private def sendFailedPasswordResetAttempt(email: String) {
    val subject: String = Messages("mail.reset.fail.subject")
    val message: String = Messages("mail.reset.fail.message", email)
    val envelop: Mail.Envelop = new Mail.Envelop(subject, message, email)
    Mail.sendMail(envelop)
  }

  def reset(token: String) = Action { implicit request =>
    if (token == null) {
      flash("error", Messages("error.technical"))
      BadRequest(views.html.account.reset.ask(AskForm))
    }
    val resetToken: Token = Token.findByTokenAndType(token, Token.TypeToken.password)
    if (resetToken == null) {
      flash("error", Messages("error.technical"))
      BadRequest(views.html.account.reset.ask(AskForm))
    }
    if (resetToken.isExpired) {
      resetToken.delete
      flash("error", Messages("error.expiredresetlink"))
      BadRequest(views.html.account.reset.ask(AskForm))
    }
    Ok(views.html.account.reset.reset(ResetForm, token))
  }

  /**
   * @return reset password form
   */
  def runReset(token: String) = Action { implicit request =>
    val resetForm: Form[Reset] = ResetForm.bindFromRequest
    if (resetForm.hasErrors) {
      flash("error", Messages("signup.valid.password"))
      BadRequest(views.html.account.reset.reset(resetForm, token))
    }
    try {
      val resetToken: Token = Token.findByTokenAndType(token, Token.TypeToken.password)
      if (resetToken == null) {
        flash("error", Messages("error.technical"))
        BadRequest(views.html.account.reset.reset(resetForm, token))
      }
      if (resetToken.isExpired) {
        resetToken.delete
        flash("error", Messages("error.expiredresetlink"))
        BadRequest(views.html.account.reset.reset(resetForm, token))
      }
      val user: User = User.findByConfirmationToken(token)
      if (user == null) {
        flash("error", Messages("error.technical"))
        BadRequest(views.html.account.reset.reset(resetForm, token))
      }
      val password: String = resetForm.get.inputPassword
      user.changePassword(password)
      sendPasswordChanged(user)
      flash("success", Messages("resetpassword.success"))
      Ok(views.html.account.reset.reset(resetForm, token))
    }
    catch {
      case e: EmailException => {
        flash("error", Messages("error.technical"))
        BadRequest(views.html.account.reset.reset(resetForm, token))
      }
    }
  }

  /**
   * Send mail with the new password.
   *
   * @param user user created
   * @throws EmailException Exception when sending mail
   */
  private def sendPasswordChanged(user: User) {
    val subject: String = Messages("mail.reset.confirm.subject")
    val message: String = Messages("mail.reset.confirm.message")
    val envelop: Mail.Envelop = new Mail.Envelop(subject, message, user.email)
    Mail.sendMail(envelop)
  }

  case class Ask(email: String)
  val AskForm : Form[Ask] = Form (
    mapping(
      "email" -> nonEmptyText
    )(Ask.apply)(Ask.unapply)
  )

  case class Reset(inputPassword: String)
  val ResetForm = Form (
    mapping(
      "inputPassword" -> nonEmptyText
    )(Reset.apply)(Reset.unapply)
  )

  def flash(tpe: String, message: String) = Flash ( Map( (tpe, message)))
}