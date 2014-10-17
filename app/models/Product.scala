package models

/**
 * Created by ruraj on 7/20/14.
 */
case class Product(name: String, description: String, productCode: String, features: Set[Feature]) {
  def featureCount() = {
    features.size
  }

  def itemCount() = {
    0
  }

  def items() = {
    Db.query[Item].whereEqual("product", this).fetch().toList
  }
}

/**
 * TEXT, NUMBER
 * @param typeName
 */
case class DataType(typeName: String)

/**
 * LIST, FIXED, DERIVED
 * @param typeName
 */
case class ValueType(typeName: String)

case class Feature(code: String, name: String, description: String, unit: String, dataType: String, valueType: String, derivation: String, values: Set[Value])

case class FeatureValue(featureCode: String, valueCode: String)

case class Value(code: String, name: String, value: String)

case class Item(product: Product, code: String, values: Set[Value])