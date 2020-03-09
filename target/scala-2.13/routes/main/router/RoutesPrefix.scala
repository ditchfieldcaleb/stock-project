// @GENERATOR:play-routes-compiler
// @SOURCE:/home/caleb/stock-project/conf/routes
// @DATE:Mon Mar 09 00:25:02 EDT 2020


package router {
  object RoutesPrefix {
    private var _prefix: String = "/"
    def setPrefix(p: String): Unit = {
      _prefix = p
    }
    def prefix: String = _prefix
    val byNamePrefix: Function0[String] = { () => prefix }
  }
}
