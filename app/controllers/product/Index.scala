package controllers.product

import models._
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Controller, Action}

/**
 * Created by ruraj on 7/20/14.
 */
object Index extends Controller {

  def add(dealerId: Long) = Action {
    Ok(views.html.product.register.index(AddProductForm, dealerId))
  }

  def detail(id: Long) = Action {
    val product = Db.fetchById[Product](id)
    if (product == null) {
      Redirect(controllers.routes.Application.index)
    }
    Ok(views.html.product.detail(product))
  }

  def addFeature(id: Long) = Action { implicit request =>
    if (request.method.equals("GET")){
      Ok(views.html.product.feature.add.index(Db.fetchById[Product](id), AddFeatureForm))
    } else {

      val params = AddFeatureForm.bindFromRequest()

      if (params.hasErrors) {
        BadRequest(views.html.product.feature.add.index(Db.fetchById[Product](id), params))
      }

      //      val feature = Db.save(models.Feature(id+"F"+System.currentTimeMillis(), params._1, params._2, params._3, params._4, params._5, Set()))
      val featureT = params.get.copy(code = id+"F"+System.currentTimeMillis())

      val feature = Db.save(featureT)

      Db.save(ProductFeature(id, feature.id))

      Ok(views.html.product.feature.add.created())
    }
  }
  val AddProductForm = Form (
    tuple(
      "name" -> nonEmptyText,
      "description" -> text,
      "productCode" -> text
    )
  )

  val AddFeatureForm: Form[models.Feature] = Form {
    mapping (
      "name" -> nonEmptyText,
      "description" -> text,
      "unit" -> text,
      "data-type" -> text,
      "value-type" -> text,
      "derivation" -> text
    ) {
      (name, description, unit, datatype, valuetype, derivation) => {
        models.Feature(null,name,description,unit,datatype,valuetype)
      }
    }
    {
      feature => {
        Some(feature.name,feature.description,feature.unit,feature.dataType,feature.valueType,null)
      }
    }
  }
}
