package controllers

import models.User
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages
import play.api.mvc._

/**
 * Login and Logout.
 * User: ruraj
 */
object Application extends Controller {
  /**
   * Display the login page or dashboard if connected
   *
   * @return login page or dashboard
   */
  def index() = Action { implicit request =>
    val email: Option[String] = request.session.get(Security.username)
    if (email.isDefined) {
      Logger.debug("Index run as "+email)
      val user: User = User.findByEmail(email.get)
      if (user != null && user.validated) {
        Logger.debug("Going to dashboard")
        GO_DASHBOARD
      }
      else {
        Logger.debug("Clearing invalid session credentials")
        Ok(views.html.index(RegisterForm, LoginForm)).withNewSession
      }
    }
    Flash(Map(("success", "Login to System")))
    Ok(views.html.index(RegisterForm, LoginForm))
  }

  /**
   * Handle login form submission.
   *
   * @return Dashboard if auth OK or login form if auth KO
   */
  def authenticate = Action { implicit request =>
    val loginForm: Form[Login] = LoginForm.bindFromRequest
    if (loginForm.hasErrors) {
      Logger.error("Invalid creds")
      BadRequest(views.html.index(RegisterForm, loginForm))
    }
    else {
      Logger.debug("Logged in as "+loginForm.get.email)
      GO_DASHBOARD.withSession(Security.username -> loginForm.get.email)
    }
  }

  /**
   * Logout and clean the session.
   *
   * @return Index page
   */
  def logout = Action { implicit request =>
    Flash( Map(("success", Messages("youve.been.logged.out"))))

    GO_HOME.withNewSession
  }

  def GO_HOME = Redirect(routes.Application.index())
  def GO_DASHBOARD = Redirect(routes.Dashboard.index())

  case class Login(email: String, password: String)
  val LoginForm : Form[Login] = Form (
    mapping (
      "email" -> nonEmptyText,
      "password" -> nonEmptyText
    )(Login.apply)(Login.unapply) verifying(error = "Username/Password is not valid", constraint = {
        case(form: Login) => User.authenticate(form.email, form.password) == null : Boolean
    })
  )
  case class Register(email: String, fullname: String, inputPassword: String)
  val RegisterForm : Form[Register] = Form (
    mapping (
      "email" -> nonEmptyText,
      "fullname" -> nonEmptyText,
      "inputPassword" -> nonEmptyText
    )(Register.apply)(Register.unapply)
  )
}