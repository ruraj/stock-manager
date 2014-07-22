package controllers.dealer

import models._
import play.api.libs.json.Json
import sorm.Persisted

//{User, Db, Dealer}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._ //{Action, Result, Controller}

/**
 * Created by ruraj on 7/17/14.
 */
object Index extends Controller {

  def index = Action {
    val dealers: List[Dealer with Persisted] = Db.query[Dealer]
      .fetch()
      .toList
    Ok(views.html.dealer.index(dealers))
  }

  def detail(id: Long) = Action { request =>
    val dealer: Dealer with Persisted = Db.fetchById[Dealer](id)
    if (id == null || dealer == null) {
      Redirect(controllers.dealer.routes.Index.index)
    }
    Ok(views.html.dealer.detail(dealer))
  }

  def addUser = Action { implicit request =>
    val users: List[String] = AddUserForm.bindFromRequest.get.users
    Ok(Json.toJson(users))
  }

  def addProduct = Action { implicit request =>
    val product: Product = controllers.product.Index.AddProductForm.bindFromRequest.get
    Ok(Json.toJson(product)(Json.writes[Product]))
  }
  case class Users(users: List[String])

  val AddUserForm : Form[Users] = Form (
    mapping(
      "users" -> list(nonEmptyText)
    )(Users.apply)(Users.unapply)
  )

}