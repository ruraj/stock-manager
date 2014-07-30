package models

import models.relations.DealerProduct
import models.relations.UserDealer
import sorm.Persisted

/**
 * Created by ruraj on 7/16/14.
 */
case class Dealer(name: String, ownerId: Long, pan: String) {
  def owner(): User with Persisted = {
    Db.fetchById[User](this.ownerId)
    //    return Ebean.find(classOf[User], this.ownerId)
  }
}

object Dealer {
  def productCount(id: Long) : Int = {
    Db.query[DealerProduct].whereEqual("dealerId", id).fetchOne().getOrElse(new DealerProduct(0, Set())).products.size //Finder[_, _](classOf[Integer], classOf[DealerProduct]).where.eq("dealerId", this.id).findRowCount
  }

  def products(id: Long) : Set[Product with Persisted] = {
    Db.transaction(
      for (product <- Db.query[DealerProduct].whereEqual("dealerId", id).fetchOne().getOrElse(new DealerProduct(0, Set())).products) yield Db.query[Product].whereEqual("name", product.name).fetchOne().get
    )

//    Without for
//
//    Db.transaction(
//      Db.query[DealerProduct].whereEqual("dealerId", id).fetchOne().getOrElse(new DealerProduct(0, Set())).products.map(product => Db.query[Product].whereEqual("name", product.name).fetchOne().get)
//    )
    //    return new Model.Finder[_, _](classOf[Integer], classOf[DealerProduct]).where.eq("dealerId", this.id).findList
  }

  def userCount(id: Long) : Long = {
    Db.query[UserDealer].whereEqual("dealerId", id).fetchOne().getOrElse(new UserDealer(0, Set())).users.size
    //    return new Model.Finder[_, _](classOf[Integer], classOf[UserDealer]).where.eq("dealerId", this.id).findRowCount
  }

  def users(id: Long) = {
    Db.query[UserDealer].whereEqual("dealerId", id).fetchOne().getOrElse(new UserDealer(0, Set())).users.toList
    //    val res: List[User] = new ArrayList[User]
    //    import scala.collection.JavaConversions._
    //    for (userDealer <- Ebean.find(classOf[UserDealer]).findList) {
    //      res.add(Ebean.find(classOf[User], userDealer.userId))
    //    }
    //    return res
  }
}