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

  def add = Action {
    Ok(views.html.product.register.index(AddProductForm))
  }

  val AddProductForm : Form[Product] = Form (
    mapping(
      "name" -> text,
      "description" -> text,
      "productCode" -> text
    )(Product.apply)(Product.unapply)
  )
}
