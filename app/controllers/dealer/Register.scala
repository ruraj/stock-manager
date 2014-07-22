package controllers.dealer

import models.{Db, User, Dealer}
import play.api.data.Form
import play.api.data.Forms._

import play.api.mvc.{Session, Action, Controller}
import views.html.helper.form

/**
 * Created by ruraj on 7/17/14.
 */
object Register extends Controller {
  def index = Action {
    Ok(views.html.dealer.register.index(RegistrationForm))
  }

  def save = Action { implicit request =>
    val dealerForm: Form[Dealer] = RegistrationForm.bindFromRequest

    if (dealerForm.hasErrors) {
      BadRequest(views.html.dealer.register.index(dealerForm))
    }
    val dealer: Dealer = dealerForm.get
    val id = User.findByEmail(Session().get("email").get).id

    val newDealer = new Dealer(dealer.name, id, dealer.pan)

    Db.save(newDealer)
    Ok(views.html.dealer.register.created())
  }

  val RegistrationForm : Form[Dealer] = Form (
    mapping(
      "name" -> text,
      "ownerId" -> longNumber,
      "pan" -> text
    )(Dealer.apply)(Dealer.unapply)
  )

}