package controllers

import authentication.AuthenticationAction
import javax.inject._
import models.{LoginDetails, UserDetails}
import play.api.i18n.I18nSupport
import play.api.mvc._

@Singleton
class HomeController @Inject()(
                                cc: ControllerComponents,
                                authAction: AuthenticationAction,
                                val mongoService: MongoService)
  extends AbstractController(cc)
    with I18nSupport {

  def index: Action[AnyContent] = authAction {
    Ok(views.html.index("Welcome to the jungle"))
  }

  def create: Action[AnyContent] = authAction { implicit request: Request[AnyContent] =>
    Ok(views.html.create(UserDetails.personList))
  }

}
