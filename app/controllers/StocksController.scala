package controllers
import javax.inject._
import play.api.mvc._

/**
 * This controller creates the action to handle the base "GET" request for the stocks page.
 */
@Singleton
class StocksController @Inject()(cc: ControllerComponents) (implicit assetsFinder: AssetsFinder)
  extends AbstractController(cc) {

  /**
   * Creates the action to handle the "GET" request (defined in the StocksController we created)
   */
  def index = Action {
    Ok(views.html.stocks("Let's watch some stocks!"))
  }
}
