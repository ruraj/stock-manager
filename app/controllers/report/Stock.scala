package controllers.report

import models._
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import sorm.Persisted

/**
 * Created by ruraj on 10/8/14.
 */
object Stock extends Controller {

  val formatter = DateTimeFormat.forPattern("MM/dd/yyyy")

  def showMe() = Action { implicit request =>

    val form = request.body.asFormUrlEncoded
    val pCode = form.get("product-code")(0)

    val itemStocks = Db.query[ItemStock]
//      .whereEqual("product$id", 1)
      .fetch()

    var json = Json.obj()
    var columns = Json.arr()
    val product: models.Product with Persisted = Db.query[models.Product].whereEqual("productCode", pCode).fetchOne().get
    val features = Product.features(product.id)
    features.map(feature => {
      columns = columns append Json.obj(("id", Json.toJson(feature.id)), ("name", Json.toJson(feature.name)), ("description", Json.toJson(feature.description)))
    } )
    json = json + ("columns", columns)

    var array = Json.arr()
    Logger.debug(itemStocks.size.toString)

    itemStocks foreach(itemStock => {
      if (itemStock.productId == product.id) {
        Logger.debug("Stock for "+pCode)
        var item = Json.arr()
        Item.valuesFor(itemStock.itemId) foreach (value => {
          item = item append Json.obj(("featureId", Json.toJson(value._1)), ("value", Json.toJson(value._2.value)))
        })
        array = array append Json.obj(
          ("amount", Json.toJson(itemStock.amount)),
          ("item", item)
        )
      }
    })
    json = json + ("data", array)

    Ok(json.toString())
  }


  def index(dealerId: Long) = Action { implicit request =>
    Ok(views.html.report.stock(Db.fetchById[Dealer](dealerId)))
  }
}
