package models

import play.api.libs.json.OFormat

case class User(
                 age: Int,
                 firstName: String,
                 lastName: String)

object JsonFormats {
  import play.api.libs.json.Json

  implicit val userFormat: OFormat[User] = Json.format[User]
}
