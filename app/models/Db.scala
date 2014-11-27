package models

import models.relations.{UserDealer, DealerProduct}
import sorm._ //{Entity, Instance}

/**
 * Created by ruraj on 7/21/14.
 */
object Db extends Instance (
  entities = Set(
    Entity[User](), Entity[Dealer](), Entity[Token](),
    Entity[Value](),
    Entity[Product](), Entity[Feature](), Entity[ValueFeature](),
    Entity[ProductFeature](), Entity[Formula](), Entity[FeatureFormula](),
    Entity[Item](), Entity[ItemProduct](), Entity[ItemValue](), Entity[ItemStock](),
    Entity[DealerProduct](), Entity[UserDealer](),
    Entity[Transaction]()),
  url = "jdbc:h2:file:sms",
  user = "ruraj",
  password = "password",
  initMode = InitMode.Create
)