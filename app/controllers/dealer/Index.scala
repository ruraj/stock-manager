package controllers.dealer

import models._
import models.relations.{UserDealer, DealerProduct}
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

  def addProduct(dealerId: Long) = Action { implicit request =>
    val productForm = controllers.product.Index.AddProductForm.bindFromRequest
    if (productForm.hasErrors) {
      BadRequest(views.html.product.register.index(productForm, dealerId))
    } else {

      val params = productForm.get

      val product = Product(params._1, params._2, params._3, Set[Feature]())

      val dealer = Db.fetchById[Dealer](dealerId)

      val productP = Db.save(product)

      val dealerProduct = Db.query[DealerProduct].whereEqual("dealerId", dealer.id).fetchOne()
      dealerProduct.getOrElse({ Db.save(DealerProduct(dealer.id, Set(productP)))})
      dealerProduct.map(d => d.copy(products = d.products + productP)).map( a => Db.save(a) )

      Ok(views.html.product.register.created())
    }
  }

  def delete(dealerId: Long) = Action { implicit request =>
    val dealer = Db.fetchById[Dealer](dealerId)

    Db.delete(dealer)

    Db.delete(Db.query[UserDealer].whereEqual("dealerId", dealer.id).fetchOne().get)

    Db.query[DealerProduct].whereEqual("dealerId", dealer.id).fetchOne().map(a => if (!a.eq(null)) Db.delete(a))

    Ok(views.html.product.register.created())
  }
  case class Users(users: List[String])

  val AddUserForm : Form[Users] = Form (
    mapping(
      "users" -> list(nonEmptyText)
    )(Users.apply)(Users.unapply)
  )

}