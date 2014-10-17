package models

import models.relations.{UserDealer, DealerProduct}
import sorm._ //{Entity, Instance}

/**
 * Created by ruraj on 7/21/14.
 */
object Db extends Instance (
  entities = Set(
    Entity[User](), Entity[Dealer](), Entity[Token](),
    Entity[Product](), Entity[Feature](), Entity[models.Value](), Entity[FeatureValue](), Entity[Item](),
    Entity[DealerProduct](), Entity[UserDealer](),
    Entity[Transaction]()),
  url = "jdbc:h2:file:sms",
  user = "ruraj",
  password = "password",
  initMode = InitMode.Create
)