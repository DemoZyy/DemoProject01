
package com.pubnub.api.tests

import org.json.{JSONArray, JSONObject}
import org.scalatest.fixture
import org.scalatest.{BeforeAndAfterAll, fixture, Tag}
import com.jayway.awaitility.scala.AwaitilitySupport
import org.junit._
import Assert._


import java.util.concurrent.TimeUnit.MILLISECONDS

import com.jayway.awaitility.Awaitility._
import com.jayway.awaitility.core.ConditionTimeoutException


import com.pubnub.api._


import java.util.concurrent.TimeUnit
import  com.jayway.awaitility.Awaitility.await


import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import scala.util.Try



import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import scala.util.Random



object ErrorTest extends Tag("com.pubnub.api.tests.ErrorTest")

@RunWith(classOf[JUnitRunner])
class V4Spec extends fixture.FunSpec with AwaitilitySupport {

  var PUBLISH_KEY   = ""
  var SUBSCRIBE_KEY = ""
  var SECRET_KEY    = ""
  var CIPHER_KEY    = ""
  var SSL           = false
  var RANDOM        = new Random()



  type FixtureParam = PubnubTestConfig

  def getRandom(): String = {
    return RANDOM.nextInt().toString
  }

  def withFixture(test: OneArgTest) {
    var pubnubTestConfig = new PubnubTestConfig()
    PUBLISH_KEY = "demo" // test.configMap.getRequired[String]("publish_key").asInstanceOf[String]
    SUBSCRIBE_KEY = "demo" // test.configMap.getRequired[String]("subscribe_key").asInstanceOf[String]
    SECRET_KEY = "demo" // test.configMap.getRequired[String]("secret_key").asInstanceOf[String]
    var cipher = "" //test.configMap.getOptional[String]("cipher_key")
    if (cipher != scala.None) {
      //CIPHER_KEY = test.configMap.getRequired[String]("cipher_key").asInstanceOf[String]
    }
    SSL = false // Try(test.configMap.getRequired[String]("ssl").asInstanceOf[String].toBoolean).getOrElse(false)
    val pubnub = new Pubnub(PUBLISH_KEY, SUBSCRIBE_KEY, SECRET_KEY, CIPHER_KEY, SSL)
    pubnubTestConfig.pubnub = pubnub
    withFixture(test.toNoArgTest(pubnubTestConfig))
  }

  describe("Subscribe()") {

    it("should be able to receive String message successfully", ErrorTest) { pubnubTestConfig =>

      var pubnub = pubnubTestConfig.pubnub
      var channel = "channel-" + getRandom()
      var message = "message-" + getRandom()
      var testObj = new PnTest(4)


      pubnub.addStreamListener(new StreamListener() {

        override def streamStatus(status: StreamStatus) {
          println(status)

        }

        override def streamResult(result: StreamResult) {
          println(result)
        }
      })

      pubnub.subscribe("a")

      await atMost(20000, MILLISECONDS) until {
        testObj.checksRemaining() == 0
      }

    }

  }
}