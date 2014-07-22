package models

import models.relations.{UserDealer, DealerProduct}
import sorm._ //{Entity, Instance}

/**
 * Created by ruraj on 7/21/14.
 */
object Db extends Instance (
  entities = Set(Entity[User](), Entity[Dealer](), Entity[Product](), Entity[DealerProduct](), Entity[UserDealer]()),
  url = "jdbc:h2:file:data/db",
  user = "ruraj",
  password = "password",
  initMode = InitMode.Create
)