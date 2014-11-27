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
object Transaction extends Controller {

  val formatter = DateTimeFormat.forPattern("MM/dd/yyyy")

  def showMe() = Action { implicit request =>

    val form = request.body.asFormUrlEncoded
    val pCode = form.get("product-code")(0)
    val from = form.get("start")(0)
    val to = form.get("end")(0)

    val transactions = Db.query[Transaction]
      .whereLargerOrEqual("datetime", DateTime.parse(from, formatter))
      .whereSmallerOrEqual("datetime", DateTime.parse(to, formatter))
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
    Logger.debug(transactions.size.toString)

    transactions foreach(transaction => {
      if (transaction.product.productCode equals pCode) {
        Logger.debug("Transaction for "+pCode)
        var item = Json.arr()
        Item.valuesFor(Db.query[Item].whereEqual("code", transaction.item.code).fetchOne().get.id) foreach (value => {
          item = item append Json.obj(("featureId", Json.toJson(value._1)), ("value", Json.toJson(value._2.value)))
        })
        array = array append Json.obj(
          ("user", Json.toJson(transaction.entryUser.fullname)),
          ("transactionUser", Json.toJson(transaction.transactionUser.fullname)),
          ("amount", Json.toJson(transaction.amount)),
          ("rate", Json.toJson(transaction.rate)),
          ("datetime", Json.toJson(transaction.datetime.toString(formatter))),
          ("itemCode", transaction.item.code),
          ("item", item)
        )
      }
    })
    json = json + ("data", array)

    Ok(json.toString())
  }


  def index(dealerId: Long) = Action { implicit request =>
    Ok(views.html.report.transaction(Db.fetchById[Dealer](dealerId)))
  }
}
