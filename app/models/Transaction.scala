package models

import org.joda.time.DateTime

/**
 * Created by ruraj on 10/7/14.
 */
case class Transaction(entryUser: User, transactionUser: User, product: Product, item: Item, amount: Double, rate: Double, datetime: DateTime)
