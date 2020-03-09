# RealTime Stock Quotes

Hello!

This is my submission for the Clutch Scala Coding Exercise.

I started with the following websockets example from the play-framework samples repository:

<a href="https://github.com/playframework/play-samples/tree/2.8.x/play-scala-websocket-example">Play-Samples Websocket Starter</a>

The above linked example has the ability to only add stocks, and the values are generated, not real.

My project here has the ability to add and remove stocks, and the prices are fetched from an API.

## How to Build/Run

First, make sure that you have the following installed:
* [Java SE](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* [sbt](http://www.scala-sbt.org/download.html)

After cloning this project into a directory, run the following command to build and run the project:
`sbt run`

After a few seconds, navigate to <http://localhost:9000>

## Running Tests

To run all tests, execute the command `sbt test`

Test files/classes are located in `/test/controllers/`

The main test file is `FunctionalSpec.scala`

There are currently 10 tests, 2 which came default to the websocket starter example.

There are 7 tests left to implement as of this commit.

## Some Details

Stock prices are fetched from <https://iexcloud.io>. I've created an account, so the api key located in `/app/stocks/Stock.scala is my own.` You should replace this API key with yours.

## Final Notes

This was my first time using Scala, and also my first time using a functional programming language. Scala is fairly different from most programming languages I know, and was more challenging than I expected to get up and running with. The MVC layout of the Play framework, however, is pretty familiar (from my time working with Node) and I was able to navigate/edit/add to the existing tutorial project without too much effort.
