package models

import models.TypeToken.TypeToken
import models.utils.Mail
import org.joda.time.DateTime
import play.Configuration
import play.api.Logger
import play.api.i18n.Messages
import java.net.URL
import java.util.UUID

object TypeToken extends Enumeration("reset", "email") {
  type TypeToken = Value
  val password, email = Value
}

case class Token(token: String, userId: Long, tokenType: TypeToken, dateCreation: DateTime, email: String) {
  /**
    * @return true if the reset token is too old to use, false otherwise.
   */
  def isExpired: Boolean = {
    dateCreation != null && dateCreation.isBefore(expirationTime)
  }
  private final val EXPIRATION_DAYS: Int = 1
  /**
    * @return a date before which the password link has expired.
   */
  private def expirationTime: DateTime = {
    val cal: DateTime = DateTime.now()
    cal.minusDays(EXPIRATION_DAYS)
  }
}

/**
  * @author wsargent
 * @since 5/15/12
 */
object Token {
  /**
    * Retrieve a token by id and type.
   *
   * @param token token Id
   * @param type  type of token
   * @return a resetToken
   */
  def findByTokenAndType(token: String, `type`: TypeToken): Token = {
    //    return find.where.eq("token", token).eq("type", `type`).findUnique
    Db.query[Token].whereEqual("token", token).whereEqual("type", `type`).fetchOne().get
  }

  /**
    * Return a new Token.
   *
   * @param user  user
   * @param type  type of token
   * @param email email for a token change email
   * @return a reset token
   */
  private def getNewToken(user: User, tokenType: TypeToken, email: String): Token = {
    val token: Token = new Token(UUID.randomUUID.toString, user.getId, tokenType, DateTime.now, email)
    Db.save(token)
  }

  /**
    * Send the Email to confirm ask new password.
   *
   * @param user the current user
   * @throws java.net.MalformedURLException if token is wrong.
   */
  def sendMailResetPassword(user: User) {
    sendMail(user, TypeToken.password, null)
  }

  /**
    * Send the Email to confirm ask new password.
   *
   * @param user  the current user
   * @param email email for a change email token
   * @throws java.net.MalformedURLException if token is wrong.
   */
  def sendMailChangeMail(user: User, email: String) {
    sendMail(user, TypeToken.email, email)
  }

  /**
    * Send the Email to confirm ask new password.
   *
   * @param user  the current user
   * @param type  token type
   * @param email email for a change email token
   * @throws java.net.MalformedURLException if token is wrong.
   */
  private def sendMail(user: User, tokenType: TypeToken, email: String) {
    val token: Token = getNewToken(user, tokenType, email)
    val externalServer: String = Configuration.root.getString("server.hostname")
    var subject: String = null
    var message: String = null
    var toMail: String = null
    val urlString: String = "http://" + externalServer + "/" + tokenType.toString + "/" + token.token
    val url: URL = new URL(urlString)
    tokenType match {
      case password =>
        subject = Messages("mail.reset.ask.subject")
        message = Messages("mail.reset.ask.message", url.toString)
        toMail = user.email
      case email =>
        subject = Messages("mail.change.ask.subject")
        message = Messages("mail.change.ask.message", url.toString)
        toMail = token.email
    }
    Logger.debug("sendMailResetLink: url = " + url)
    val envelop: Mail.Envelop = new Mail.Envelop(subject, message, toMail)
    Mail.sendMail(envelop)
  }
}

