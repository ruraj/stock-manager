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


  val AddItemForm: Form[models.Item] = Form(
    mapping (
      "productCode" -> nonEmptyText,
      "itemCode" -> nonEmptyText,
      "values" -> list(
        tuple (
          "featureCode" -> nonEmptyText,
          "valueCode" -> nonEmptyText,
          "value" -> nonEmptyText
        )
      )
    ) {
      (productCode, code, values) => {
        var valuesSet = Set[Value]()
        values.foreach(v => {
          val feature = Db.query[Feature].whereEqual("code", v._1).fetchOne().get
          val valueCode = v._2
          val value = v._3
          valuesSet += Db.query[Value].whereEqual("code", valueCode).fetchOne() getOrElse {
            Db.save(FeatureValue(feature.code, valueCode))
            Db.save(Value("", value, value))
          }
        })
        Logger.debug("Product: "+productCode+", Code: "+code)
        val product: models.Product = Db.query[models.Product].whereEqual("productCode", productCode).fetchOne().get
        models.Item(product, code, valuesSet)
      }
    }
    {
      (item: Item) => {
        var valuesList = List[(String, String, String)]()
        item.product.features foreach(feature => {
          valuesList :::= List((feature.code, "", ""))
        })

        Some(item.product.productCode, item.code, valuesList)
      }
    }
  )

  def add(productCode: String) = Action {
      val product = Db.query[models.Product].whereEqual("productCode", productCode).fetchOne().get

      val randomCode = product.productCode +"-I"+(Db.query[Item].count()+1)

      val item = models.Item(product, randomCode, Set())

      Ok(views.html.product.item.add.index(product, AddItemForm.fill(item)))
  }

  def addItem(productCode: String) = Action { implicit request =>
    val itemForm = AddItemForm.bindFromRequest()
    if (itemForm.hasErrors) {
      val product = Db.query[models.Product].whereEqual("productCode", productCode).fetchOne().get
      BadRequest(views.html.product.item.add.index(product, itemForm))
    } else {
      val item = itemForm.get

      val itemP = item.copy(values = item.values.map(v => Db.save(v)))

      Db.save(itemP)

      Redirect(controllers.product.routes.Index.detail(Db.query[models.Product].whereEqual("productCode", productCode).fetchOne().get.id))
    }
  }
}
