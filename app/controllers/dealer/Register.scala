package controllers.dealer

import models.relations.UserDealer
import models.{Db, User, Dealer}
import play.api.data.Form
import play.api.data.Forms._

import play.api.mvc.{Security, Session, Action, Controller}
import views.html.helper.form

/**
 * Created by ruraj on 7/17/14.
 */
object Register extends Controller {
  def index = Action {
    Ok(views.html.dealer.register.index(RegistrationForm))
  }

  def save = Action { implicit request =>
    val dealerForm = RegistrationForm.bindFromRequest

    if (dealerForm.hasErrors) {
      BadRequest(views.html.dealer.register.index(dealerForm))
    }
    val (name, pan) = dealerForm.get
    val user = User.findByEmail(request.session.get(Security.username).get)
    val dealer: Dealer = new Dealer(name, user.id, pan)

    val newDealer = new Dealer(dealer.name, user.id, dealer.pan)

    val saved = Db.save(newDealer)

    val userDealer = Db.query[UserDealer].whereEqual("dealerId", saved.id).fetchOne()
    userDealer.getOrElse({ Db.save(UserDealer(saved.id, Set(user)))})
    userDealer.map(a => a.copy(users = a.users + user)).map( a => Db.save(a) )

    Ok(views.html.dealer.register.created())
  }

  val RegistrationForm = Form (
    tuple(
      "name" -> text,
      "pan" -> text
    )
  )

}