package stocks

import akka.NotUsed
import akka.stream.ThrottleMode
import akka.stream.scaladsl.Source

import scala.concurrent.duration._
import scalaj.http.{Http, HttpResponse}

import ujson._

/**
 * A stock is a source of stock quotes and a symbol.
 */
class Stock(val symbol: StockSymbol) {
  private val stockQuoteGenerator: StockQuoteGenerator = new RealStockQuoteGenerator(symbol)

  private val repeatSource: Source[StockQuote, NotUsed] = {
    Source.unfold(stockQuoteGenerator.seed) { (last: StockQuote) =>
      val next = stockQuoteGenerator.newQuickQuote()
      Some(next, next)
    }
  }

  private val source: Source[StockQuote, NotUsed] = {
    Source.unfold(stockQuoteGenerator.seed) { (last: StockQuote) =>
      val next = stockQuoteGenerator.newQuote(last)
      Some(next, next)
    }
  }

  /**
   * Returns a source of stock history, containing a single element.
   */
  def history(n: Int): Source[StockHistory, NotUsed] = {
    source.grouped(n).map(sq => new StockHistory(symbol, sq.map(_.price))).take(1)
  }

  /*
   * Returns fake numbers - all 0's - so that we have a source of history for the graph at the start.
   */
  def quickhistory(n: Int): Source[StockHistory, NotUsed] = {
    repeatSource.grouped(n).map(sq => new StockHistory(symbol, sq.map(_.price))).take(1)
  }

  /**
   * Provides a source that returns a stock quote every 1000 milliseconds.
   */
  def update: Source[StockUpdate, NotUsed] = {
    source
      .throttle(elements = 1, per = 5000.millis, maximumBurst = 1, ThrottleMode.shaping)
      .map(sq => new StockUpdate(sq.symbol, sq.price))
  }

  override val toString: String = s"Stock($symbol)"
}

trait StockQuoteGenerator {
  def seed: StockQuote
  def newQuote(): StockQuote
  def newQuote(lastQuote: StockQuote): StockQuote
  def newQuickQuote(): StockQuote
}

/*
class FakeStockQuoteGenerator(symbol: StockSymbol) extends StockQuoteGenerator {
  private def random: Double = scala.util.Random.nextDouble

  def seed: StockQuote = {
    StockQuote(symbol, StockPrice(random * 800))
  }

  def newQuote(lastQuote: StockQuote): StockQuote = {
    StockQuote(symbol, StockPrice(lastQuote.price.raw * (0.95 + (0.1 * random))))
  }
}
*/

// This will be our real stock quote generator!
// We're going to be using the API from AlphaVantage.co
class RealStockQuoteGenerator(symbol: StockSymbol) extends StockQuoteGenerator {
  def getRealStockPrice(symbolString:String): Double = {
    val api_key = "pk_4c75e63e0fef4a78ac364e5d9173fecb"
    val base_url = "https://cloud.iexapis.com/stable/stock/"
    val call_url = base_url + symbolString + "/quote?token=" + api_key

    //println(call_url)
    val json = ujson.read(Http(call_url).asString.body)
    //println(json)
    val result = json("latestPrice").toString().toDouble
    //println(result)

    return result
  }

  def seed: StockQuote = {
    StockQuote(symbol, StockPrice(0.00))
  }

  def newQuote(lastQuote: StockQuote): StockQuote = {
    StockQuote(symbol, StockPrice(getRealStockPrice(symbol.toString())))
  }

  def newQuote(): StockQuote = {
    StockQuote(symbol, StockPrice(getRealStockPrice(symbol.toString())))
  }

  def newQuickQuote(): StockQuote = {
    StockQuote(symbol, StockPrice(0.00))
  }
}

case class StockQuote(symbol: StockSymbol, price: StockPrice)

/** Value class for a stock symbol */
class StockSymbol private (val raw: String) extends AnyVal {
  override def toString: String = raw
}

object StockSymbol {
  import play.api.libs.json._ // Combinator syntax

  def apply(raw: String) = new StockSymbol(raw)

  implicit val stockSymbolReads: Reads[StockSymbol] = {
    JsPath.read[String].map(StockSymbol(_))
  }

  implicit val stockSymbolWrites: Writes[StockSymbol] = Writes {
    (symbol: StockSymbol) => JsString(symbol.raw)
  }
}

/** Value class for stock price */
class StockPrice private (val raw: Double) extends AnyVal {
  override def toString: String = raw.toString
}

object StockPrice {
  import play.api.libs.json._ // Combinator syntax

  def apply(raw: Double):StockPrice = new StockPrice(raw)

  implicit val stockPriceWrites: Writes[StockPrice] = Writes {
    (price: StockPrice) => JsNumber(price.raw)
  }
}

// Used for automatic JSON conversion
// https://www.playframework.com/documentation/2.8.x/ScalaJson

// JSON presentation class for stock history
case class StockHistory(symbol: StockSymbol, prices: Seq[StockPrice])

object StockHistory {
  import play.api.libs.json._ // Combinator syntax

  implicit val stockHistoryWrites: Writes[StockHistory] = new Writes[StockHistory] {
    override def writes(history: StockHistory): JsValue = Json.obj(
      "type" -> "stockhistory",
      "symbol" -> history.symbol,
      "history" -> history.prices
    )
  }
}

// JSON presentation class for stock update
case class StockUpdate(symbol: StockSymbol, price: StockPrice)

object StockUpdate {
  import play.api.libs.json._ // Combinator syntax

  implicit val stockUpdateWrites: Writes[StockUpdate] = new Writes[StockUpdate] {
    override def writes(update: StockUpdate): JsValue = Json.obj(
      "type" -> "stockupdate",
      "symbol" -> update.symbol,
      "price" -> update.price
    )
  }
}
