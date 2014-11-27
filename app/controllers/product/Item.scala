package controllers.product

import models._
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms._

import play.api.mvc.{Action, Controller}

/**
 * Created by ruraj on 8/10/14.
 */
object Item extends Controller{


  val AddItemForm = Form(
    tuple (
      "productId" -> number,
      "itemCode" -> nonEmptyText,
      "values" -> list(
        tuple (
          "featureId" -> number,
          "valueId" -> number,
          "value" -> nonEmptyText
        )
      )
    )
  )

  def add(productCode: String) = Action {
      val product = Db.query[models.Product].whereEqual("productCode", productCode).fetchOne().get

      Ok(views.html.product.item.add.index(product, AddItemForm))
  }

  def addItem(productCode: String) = Action { implicit request =>
    val itemForm = AddItemForm.bindFromRequest()
    if (itemForm.hasErrors) {
      val product = Db.query[models.Product].whereEqual("productCode", productCode).fetchOne().get
      BadRequest(views.html.product.item.add.index(product, itemForm))
    } else {
      val itemF = itemForm.get

      val item = Db.save(models.Item(itemF._2, "", ""))

      Db.save(ItemProduct(item.id, itemF._1))

      itemF._3 foreach (value => {
        val feature = Db.fetchById[Feature](value._1)
        if (feature.valueType equalsIgnoreCase  "list") {
          val storedValue = Db.fetchById[Value](value._2)
          Db.save(ItemValue(item.id, storedValue.id, feature.id))
          Logger.debug(feature.id+" "+storedValue.id+" "+storedValue)
        } else {
          val storedValue = Db.save(Value("", value._3, value._3))
          Db.save(ItemValue(item.id, storedValue.id, feature.id))
        }
      })

      Redirect(controllers.product.routes.Index.detail(Db.query[models.Product].whereEqual("productCode", productCode).fetchOne().get.id))
    }
  }
}
