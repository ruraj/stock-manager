package controllers.product

import models.Product
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Controller, Action}

/**
 * Created by ruraj on 7/20/14.
 */
object Index extends Controller {

  def index = Action {
    Ok(views.html.product.index())
  }

  def add(dealerId: Long) = Action {
    Ok(views.html.product.register.index(AddProductForm, dealerId))
  }

  def detail(id: Long) = Action {
    Ok(views.html.product.detail(id))
  }
  val AddProductForm : Form[Product] = Form (
    mapping(
      "name" -> nonEmptyText,
      "description" -> text,
      "productCode" -> text
    )(Product.apply)(Product.unapply)
  )
}
