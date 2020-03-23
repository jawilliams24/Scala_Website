package models

import play.api.libs.json.OFormat
import play.api.libs.json.Json

object JsonFormats {
  implicit val userDetails: OFormat[UserDetails] = Json.format[UserDetails]
  implicit val userFormat: OFormat[User] = Json.format[User]
}
