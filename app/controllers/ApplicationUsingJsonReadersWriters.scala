package controllers

import javax.inject.Inject
import models.{LoginDetails, User, UserDetails}
import models.JsonFormats.userFormat
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.api.Cursor
import reactivemongo.play.json._
import reactivemongo.play.json.collection.{JSONCollection, _}

import models.JsonFormats._
import scala.concurrent.{ExecutionContext, Future}

class ApplicationUsingJsonReadersWriters @Inject()(
components: ControllerComponents,
val reactiveMongoApi: ReactiveMongoApi,
val mongoService: MongoService
) extends AbstractController(components)
with MongoController with ReactiveMongoComponents with play.api.i18n.I18nSupport {

  implicit def ec: ExecutionContext = components.executionContext

  def collection: Future[JSONCollection] = database.map(_.collection[JSONCollection]("persons"))


  def createFromJson: Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[UserDetails].map { user =>
      collection.flatMap(_.insert.one(user)).map { _ => Ok("User inserted")
      }
    }.getOrElse(Future.successful(BadRequest("invalid json")))
  }

  def create = Action.async { implicit request: Request[AnyContent] =>
    UserDetails.personList.bindFromRequest.fold({ formWithErrors =>
      Future{BadRequest(views.html.create(formWithErrors))}
    }, { userDetails: UserDetails =>

      mongoService.createUserDetails(userDetails).map{
        _ => Ok("User inserted")
      }
    })

  }

  def findByName(lastName: String): Action[AnyContent] = Action.async {
    val cursor: Future[Cursor[User]] = collection.map {
      _.find(Json.obj("lastName" -> lastName)).
        sort(Json.obj("created" -> -1)).
        cursor[User]()
    }

    val futureUsersList: Future[List[User]] =
      cursor.flatMap(
        _.collect[List](
          -1,
          Cursor.FailOnError[List[User]]()
        )
      )

    futureUsersList.map { persons =>
      Ok(persons.toString)
    }
  }

  def findAll(): Action[AnyContent] = Action.async {
    val cursor: Future[Cursor[UserDetails]] = collection.map {
      _.find(Json.obj()).cursor[UserDetails]()

    }
    val futureUserList: Future[List[UserDetails]] =
      cursor.flatMap(
        _.collect[List](
          -1,
          Cursor.FailOnError[List[UserDetails]]()

        )
      )
    futureUserList.map { persons =>
      Ok(persons.toString)
    }
  }


  //  def updateUser(firstName: String): Action[AnyContent] = Action.async {
  //val user =
  //    val futureResult =
  //  }

  def deleteOne(): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[UserDetails].map { user =>
      collection.flatMap(c => c.remove(user)).map { _ => Ok("removed")
      }
    }.getOrElse(Future.successful(BadRequest("invalid Json")))
  }

  def updateNoteFromJson(message: String): Action[JsValue] = Action.async(parse.json) {
    request =>
      request.body.validate[UserDetails].map { user =>
        collection.flatMap(c => c.update.one(user, Json.obj("firstName" -> user.firstName, "message" -> message), upsert = false)).map { _ => Ok("updated")
        }
      }.getOrElse(Future.successful(BadRequest("invalid Json")))
  }

}
