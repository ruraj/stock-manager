package controllers.product

import models._
import org.joda.time.DateTime
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Security, Action, Controller}

/**
 * Created by ruraj on 8/27/14.
 */
object Transaction extends Controller {

  def doTransaction(productId: Long) = Action {
    Ok(makeView(productId))
  }

  def makeView(pId: Long) = {
    val product = Db.fetchById[models.Product](pId)

    val randomCode = product.productCode +"-I"+(Db.query[Item].count()+1)

    val item = models.Item(randomCode, "", "")

    views.html.product.transaction.index(product, TransactionForm, Item.AddItemForm)
  }
  def TransactionForm: Form[List[Transaction]] = Form {
    mapping("transaction-list" ->
      optional(list(
        mapping(
          "item-code" -> nonEmptyText,
          "amount" -> nonEmptyText,
          "rate" -> nonEmptyText,
          "date" -> jodaDate("MM/dd/yyyy")
        ) {
          (itemCode, amount, rate, date) => {
            val item = Db.query[Item].whereEqual("code", itemCode).fetchOne().get
            Logger.debug(date.toString)
            models.Transaction(null, null, null, item, amount.toDouble, rate.toDouble, date)
          }
        } {
          transaction => {
            Some(transaction.item.code, transaction.amount.toString, transaction.rate.toString, transaction.datetime)
          }
        }
      ))) {
        (set) => {
          set.get
        }
      } {
        set => {
          Some(Option(set))
        }
      }
  }

  def doSave(pId: Long) = Action { implicit request =>
    val form = TransactionForm.bindFromRequest()

    if (form.hasErrors || (form equals null)) {
      BadRequest(makeView(pId))
    }

    val user = request.session.get(Security.username).get
    Logger.debug(user)
    val userO = User.findByEmail(user)
    Logger.debug(userO.fullname)
    form.get.map(t => {
      Db.save(t.copy(entryUser = userO, transactionUser = userO, product = Db.fetchById[Product](pId)))
      val item = Db.query[Item].whereEqual("code", t.item.code).fetchOne().get
      val itemStock = Db.query[ItemStock].whereEqual("itemId", item.id).fetchOne()
      if (itemStock.isDefined) {
        val is = itemStock.get
        Db.save(is.copy(amount = is.amount + t.amount))
      } else {
        Db.save(ItemStock(item.id, pId, t.amount))
      }
    })

    Ok(makeView(pId))
  }
}
