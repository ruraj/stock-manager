package controllers.product

import controllers.Application
import models.{ValueFeature, Value, Feature, Db}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Action, Controller}

import scala.util.Random

/**
 * Created by ruraj on 8/7/14.
 */
object Feature extends Controller {

  def addValue(code: String) = Action { implicit request =>

    val form = AddValueForm.bindFromRequest()

    if (form.hasErrors) {
      val feature = Db.query[models.Feature].whereEqual("code", code).fetchOne().get
      BadRequest(views.html.product.feature.values(feature, form))
    }

    val valueT = form.get

    val feature = Db.query[models.Feature].whereEqual("code", code).fetchOne()

    val randomCode = code + "-V" + Db.query[Value].count()

    val value = Db.save(valueT.copy(code = randomCode))

    Db.save(ValueFeature(value.id, feature.get.id))

    Redirect(controllers.product.routes.Feature.values(feature.get.code))
  }
  def values(code: String) = Action {

    val feature = Db.query[models.Feature].whereEqual("code", code).fetchOne().get

    if (feature == null) {
      Application.GO_DASHBOARD
    }
    Ok(views.html.product.feature.values(feature, AddValueForm))
  }

  def delete(code: String) = Action {
    Db.delete(Db.query[Feature].whereEqual("code", code).fetchOne())
    Application.GO_DASHBOARD
  }

  val AddValueForm :Form[Value] = Form {
    mapping (
      "name" -> nonEmptyText,
      "value" -> nonEmptyText
    ) {
      (name, value) => {
        Value("", name, value)
      }
    }
    {
      value =>
        Some(value.name, value.value)
    }
  }
}
