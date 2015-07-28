// package default

import play.api.Application
import play.api.GlobalSettings
import me.lightspeed7.dsug._
import scala.util.Try

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    Actors.start
  }

  override def onStop(app: Application) {
    Try(Actors.stop).failed.map { t => println(t.getMessage) }
  }

}
