package models

import sorm.Persisted

/**
 * Product class
 * Created by ruraj on 7/20/14.
 */
case class Product(name: String, description: String, productCode: String)

/**
 * Static container for Product class
 */
object Product {
  def featureCount(id: Long): Long = {
    Db.query[ProductFeature].whereEqual("productId", id).count()
  }

  def features(id: Long): Set[Feature with Persisted] = {
    var list = Set[Feature with Persisted]()
    for (pf <- Db.query[ProductFeature].whereEqual("productId", id).fetch()) {
      list = list + Db.fetchById[Feature](pf.featureId)
    }
    list
  }

  def itemCount(id: Long): Long = {
    Db.query[ItemProduct].whereEqual("productId", id).count()
  }

  def items(id: Long): Set[Item with Persisted] = {
    var list: Set[Item with Persisted] = Set()
    for (o <- Db.query[ItemProduct].whereEqual("productId", id).fetch()) {
      list = list + Db.fetchById[Item](o.itemId)
    }
    list
  }
}
/**
 * Feature class
 * @param dataType TEXT, NUMBER
 * @param valueType LIST, FIXED, DERIVED
 */
case class Feature(code: String, name: String, description: String, unit: String, dataType: String, valueType: String)

/**
 * Static container for Feature class
 */
object Feature {
  def valuesFor(id: Long): Set[Value with Persisted] = {
    var list = Set[Value with Persisted]()
    Db.query[ValueFeature].whereEqual("featureId", id).fetch() foreach (vf => {
      list = list + Db.fetchById[Value](vf.valueId)
    })
    list
  }

  def formulaFor(id: Long): String = {
    val ff = Db.query[FeatureFormula].whereEqual("featureId", id).fetchOne()
    if (ff.isDefined) {
      Db.fetchById[Formula](ff.get.formulaId).formula
    } else {
      ""
    }
  }
}
case class ProductFeature(productId: Long, featureId: Long)

case class Formula(formula: String)

case class FeatureFormula(featureId: Long, formulaId: Long)

case class Value(code: String, name: String, value: String)

case class ValueFeature(valueId: Long, featureId: Long)

case class Item(code: String, name: String, description: String)

object Item {

  def valuesFor(id: Long): Set[(Long, Value with Persisted)] = {
    var list = Set[(Long, Value with Persisted)]()
    for (iv <- Db.query[ItemValue].whereEqual("itemId", id).fetch()){
      list = list + ((iv.featureId, Db.fetchById[Value](iv.valueId)))
    }
    list
  }
}
case class ItemProduct(itemId: Long, productId: Long)

case class ItemValue(itemId: Long, valueId: Long, featureId: Long)

case class ItemStock(itemId: Long, productId: Long, amount: Double)