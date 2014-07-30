package controllers.account

import controllers.Application.Register
import models.User
import models.utils.Hash
import models.utils.Mail
import org.apache.commons.mail.EmailException
import org.joda.time.DateTime
import play.Configuration
import play.Logger
import play.api.mvc.{Flash, Controller, Action}
import play.api.data.Form
import play.api.i18n.Messages
import views.html.account.signup.created
import java.net.MalformedURLException
import java.net.URL
import java.util.{Date, UUID}
import views.html.defaultpages.badRequest

import controllers.Application
/**
 * Signup to PlayStartApp : save and send confirm mail.
 * <p/>
 * User: yesnault
 * Date: 31/01/12
 */
object Signup extends Controller {
  /**
   * Display the create form.
   *
   * @return create form
   */
  def create = Action { implicit request =>
    Ok(views.html.account.signup.create(Application.RegisterForm))
  }

  /**
   * Display the create form only (for the index page).
   *
   * @return create form
   */
  def createFormOnly = Action { implicit request =>
    Ok(views.html.account.signup.create(Application.RegisterForm))
  }

  /**
   * Save the new user.
   *
   * @return Successfull page or created form if bad
   */
  def save = Action { implicit request =>
    val registerForm: Form[Application.Register] = Application.RegisterForm.bindFromRequest
    if (registerForm.hasErrors) {
      BadRequest(create(registerForm))
    }
    val register: Register = registerForm.get
    val resultError = checkBeforeSave(registerForm, register.email)
    if (resultError != null) {
      resultError
    }
    try {
      val user: User = new User(register.email, register.fullname, UUID.randomUUID.toString, Hash.createPassword(register.inputPassword), new DateTime(), false)
      user.save()
      sendMailAskForConfirmation(user)
      Ok(created())
    }
    catch {
      case e: EmailException => {
        Logger.debug("Signup.save Cannot send email", e)
        doFlash( "error", Messages("error.sending.email"))
      }
      case e: Exception => {
        Logger.error("Signup.save error", e)
        doFlash( "error", Messages("error.technical"))
      }
    }
    BadRequest(create(registerForm))
  }

  /**
   * Check if the email already exists.
   *
   * @param registerForm User Form submitted
   * @param email email address
   * @return Index if there was a problem, null otherwise
   */
  private def checkBeforeSave(registerForm: Form[Register], email: String)(implicit flash: Flash) = {
    if (User.findByEmail(email) != null) {
      doFlash( "error", Messages("error.email.already.exist"))
      BadRequest(create(registerForm))
    }
    null
  }

  /**
   * Send the welcome Email with the link to confirm.
   *
   * @param user user created
   * @throws EmailException Exception when sending mail
   */
  private def sendMailAskForConfirmation(user: User) {
    val subject: String = Messages("mail.confirm.subject")
    var urlString: String = "http://" + Configuration.root.getString("server.hostname")
    urlString += "/confirm/" + user.confirmationToken
    val url: URL = new URL(urlString)
    val message: String = Messages("mail.confirm.message", url.toString)
    val envelop: Mail.Envelop = new Mail.Envelop(subject, message, user.email)
    Mail.sendMail(envelop)
  }

  /**
   * Valid an account with the url in the confirm mail.
   *
   * @param token a token attached to the user we're confirming.
   * @return Confirmationpage
   */
  def confirm(token: String) = Action { implicit request =>
    val user: User = User.findByConfirmationToken(token)
    if (user == null) {
      doFlash("error", Messages("error.unknown.email"))
      BadRequest(views.html.account.signup.confirm())
    }
    if (user.validated) {
      doFlash("error", Messages("error.account.already.validated"))
      BadRequest(views.html.account.signup.confirm())
    }
    try {
      if (User.confirm(user)) {
        sendMailConfirmation(user)
        doFlash("success", Messages("account.successfully.validated"))
        Ok(views.html.account.signup.confirm())
      }
      else {
        Logger.debug("Signup.confirm cannot confirm user")
        doFlash("error", Messages("error.confirm"))
        BadRequest(views.html.account.signup.confirm())
      }
    }
    catch {
      case e: EmailException => {
        Logger.debug("Cannot send email", e)
        doFlash("error", Messages("error.sending.confirm.email"))
      }
    }
    BadRequest(views.html.account.signup.confirm())
  }

  def doFlash(tpe: String, message: String) = Flash ( Map( (tpe, message)))
  def create(form: Form[Application.Register])(implicit flash: Flash) = views.html.account.signup.create(form)
  /**
   * Send the confirm mail.
   *
   * @param user user created
   * @throws EmailException Exception when sending mail
   */
  private def sendMailConfirmation(user: User) {
    val subject: String = Messages("mail.welcome.subject")
    val message: String = Messages("mail.welcome.message")
    val envelop: Mail.Envelop = new Mail.Envelop(subject, message, user.email)
    Mail.sendMail(envelop)
  }
}