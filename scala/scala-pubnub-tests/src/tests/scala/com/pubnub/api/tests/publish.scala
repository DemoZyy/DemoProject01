
package com.pubnub.api.tests

import org.json.{JSONArray, JSONObject}
import org.scalatest.{Tag, fixture}

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


object PublishTest extends Tag("com.pubnub.api.tests.PublishTest")
object SingleTest extends Tag("com.pubnub.api.tests.SingleTest")

@RunWith(classOf[JUnitRunner])
class PublishSpec1 extends fixture.FunSpec with AwaitilitySupport {

  var PUBLISH_KEY   = ""
  var SUBSCRIBE_KEY = ""
  var SECRET_KEY    = ""
  var CIPHER_KEY    = ""
  var SSL           = false
  var RANDOM        = new Random()

  var TIMEOUT       = 30000

  type FixtureParam = PubnubTestConfig

  def getRandom(): String = {
    return RANDOM.nextInt().toString
  }

  def withFixture(test: OneArgTest) {
    var pubnubTestConfig = new PubnubTestConfig()
    PUBLISH_KEY = test.configMap.getRequired[String]("publish_key").asInstanceOf[String]
    SUBSCRIBE_KEY = test.configMap.getRequired[String]("subscribe_key").asInstanceOf[String]
    SECRET_KEY = test.configMap.getRequired[String]("secret_key").asInstanceOf[String]
    var cipher = test.configMap.getOptional[String]("cipher_key")
    if (cipher != scala.None) {
      CIPHER_KEY = test.configMap.getRequired[String]("cipher_key").asInstanceOf[String]
    }
    SSL = Try(test.configMap.getRequired[String]("ssl").asInstanceOf[String].toBoolean).getOrElse(false)


    val pubnub = new com.pubnub.api.PubnubCore.Builder()
      .setPublishKey(PUBLISH_KEY)
      .setSubscribeKey(SUBSCRIBE_KEY)
      .setSecretKey(SECRET_KEY)
      .setCipherKey(CIPHER_KEY)
      .setSsl(SSL)
      .build();

    pubnubTestConfig.pubnub = pubnub
    withFixture(test.toNoArgTest(pubnubTestConfig))
  }


  describe("Publish()") {

    def isNotEmpty(s: String): Boolean = {
      if (s == null || s.length == 0) return false;
      else return true;
    }
    def isEmpty(s: String): Boolean = {
      if (s == null || s.length == 0) return true;
      else return false;
    }

    def checkConfig(config: Config): Boolean = {
      //config.authKey
      //config.origin
      //config.TLS
      //config.uuid

      assertFalse(isEmpty(config.origin))
      assertFalse(isEmpty(config.uuid))

      return true;
    }

    def verifyResultDetails(result: Result): Boolean = {

      // CHECK STRINGS

      assertFalse(isEmpty(result.getClientRequest))
      assertFalse(isEmpty(result.getServerResponse))



      //if (isEmpty(result.getConnectionId) == true) return false;   !!!! CHECK

      if (result.getOperation == null) return false
      if (result.getCode == null || result.getCode == 0) return false

      if (result.getConfig == null) return false
      if (!checkConfig(result.getConfig)) return false


      //result.getType



      return true;
    }

    it("should be able to publish String with double quotes successfully", PublishTest) { pubnubTestConfig =>

      var pubnub  = pubnubTestConfig.pubnub
      var channel = "channel-" + getRandom()
      var message = "message-" + "\"hi\""
      var testObj = new PnTest(3)


      val slistener = new StreamListener() {
        override def streamStatus(status: StreamStatus) {

          if (status.isError()) {
            pubnub.unsubscribe(channel)
            pubnub.removeListener(channel)
            assertTrue(status.toString, false)
          }

          if (status.getCategory == StatusCategory.CONNECT) {
            testObj.test(true)

            pubnub.publish().callback(new PublishCallback(){
              override def status(status: PublishStatus){
                if (status.isError()) {
                  pubnub.unsubscribe(channel)
                  pubnub.removeListener(channel)
                  assertTrue(status.toString, false)
                }
                if (status.getCategory == StatusCategory.ACK) {
                  testObj.test(true)
                }
              }
            }).channel(channel).message(message).send()
          }


        }

        override def streamResult(result: StreamResult) {
          var message1 = result.getData.message
          pubnub.unsubscribe(channel)
          pubnub.removeListener(channel)
          testObj.test(true)

          assertTrue(message1.toString(), (message1.getClass()).equals(message.getClass()))
          assertTrue(result.getData.timetoken != null)
          assertTrue(result.toString, result.getData.channel.equals(channel))
          assertTrue(message1.equals(message))
          assertTrue(result.toString, verifyResultDetails(result) == true)
        }
      }

      pubnub.addStreamListener(channel, slistener)

      pubnub.subscribe().channel(channel).connect()

      await atMost(TIMEOUT, MILLISECONDS) until { testObj.checksRemaining() == 0 }

    }


    it("should be able to publish JSON Array successfully", PublishTest) { pubnubTestConfig =>

      var pubnub  = pubnubTestConfig.pubnub
      var channel = "channel-" + getRandom()
      var message = new JSONArray()
      message.put("a")
      message.put("b")
      var testObj = new PnTest(3)


      val slistener = new StreamListener() {
        override def streamStatus(status: StreamStatus) {

          if (status.isError()) {
            pubnub.unsubscribe(channel)
            pubnub.removeListener(channel)
            assertTrue(status.toString, false)
          }

          if (status.getCategory == StatusCategory.CONNECT) {
            testObj.test(true)

            pubnub.publish().callback(new PublishCallback(){
              override def status(status: PublishStatus){
                if (status.isError()) {
                  pubnub.unsubscribe(channel)
                  pubnub.removeListener(channel)
                  assertTrue(status.toString, false)
                }
                if (status.getCategory == StatusCategory.ACK) {
                  testObj.test(true)
                }
              }
            }).channel(channel).message(message).send()
          }


        }

        override def streamResult(result: StreamResult) {
          var message1 = result.getData.message
          pubnub.unsubscribe(channel)
          pubnub.removeListener(channel)
          testObj.test(true)

          assertTrue(message1.toString(), (message1.getClass()).equals(message.getClass()))
          assertTrue(message1.toString + " : " + message1.getClass(), (message1.getClass()).equals(message.getClass()))
          assertTrue(message1.asInstanceOf[JSONArray].getString(0).equals("a"))
          assertTrue(message1.asInstanceOf[JSONArray].getString(1).equals("b"))
          assertTrue(result.toString, verifyResultDetails(result) == true)
        }
      }

      pubnub.addStreamListener(channel, slistener)

      pubnub.subscribe().channel(channel).connect()

      await atMost(TIMEOUT, MILLISECONDS) until { testObj.checksRemaining() == 0 }

    }

    it("should be able to publish JSON Array literal string successfully", PublishTest) { pubnubTestConfig =>

      var pubnub  = pubnubTestConfig.pubnub
      var channel = "channel-" + getRandom()
      var message = "[\"a\",\"b\"]"
      var testObj = new PnTest(3)


      val slistener = new StreamListener() {
        override def streamStatus(status: StreamStatus) {

          if (status.isError()) {
            pubnub.unsubscribe(channel)
            pubnub.removeListener(channel)
            assertTrue(status.toString, false)
          }

          if (status.getCategory == StatusCategory.CONNECT) {
            testObj.test(true)

            pubnub.publish().callback(new PublishCallback(){
              override def status(status: PublishStatus){
                if (status.isError()) {
                  pubnub.unsubscribe(channel)
                  pubnub.removeListener(channel)
                  assertTrue(status.toString, false)
                }
                if (status.getCategory == StatusCategory.ACK) {
                  testObj.test(true)
                }
              }
            }).channel(channel).message(message).send()
          }


        }

        override def streamResult(result: StreamResult) {
          var message1 = result.getData.message
          pubnub.unsubscribe(channel)
          testObj.test(true)

          assertTrue(message1.toString(), (message1.getClass()).equals(message.getClass()))
          assertTrue(message1.toString + " : " + message1.getClass(), (message1.getClass()).equals(message.getClass()))
          assertTrue(message1.toString + " : " + message.toString + "[" + message1.getClass() + "]", message1.equals(message))
          assertTrue(result.toString, verifyResultDetails(result) == true)
        }
      }

      pubnub.addStreamListener(channel, slistener)

      pubnub.subscribe().channel(channel).connect()

      await atMost(TIMEOUT, MILLISECONDS) until { testObj.checksRemaining() == 0 }

    }


    it("should be able to publish JSON Object successfully", PublishTest) { pubnubTestConfig =>
      var pubnub  = pubnubTestConfig.pubnub
      var channel = "channel-" + getRandom()
      var message = new JSONObject();
      message.put("a", "b")
      var testObj = new PnTest(3)


      val slistener = new StreamListener() {
        override def streamStatus(status: StreamStatus) {

          if (status.isError()) {
            pubnub.unsubscribe(channel)
            pubnub.removeListener(channel)
            assertTrue(status.toString, false)
          }

          if (status.getCategory == StatusCategory.CONNECT) {
            testObj.test(true)

            pubnub.publish().callback(new PublishCallback(){
              override def status(status: PublishStatus){
                if (status.isError()) {
                  pubnub.unsubscribe(channel)
                  pubnub.removeListener(channel)
                  assertTrue(status.toString, false)
                }
                if (status.getCategory == StatusCategory.ACK) {
                  testObj.test(true)
                }
              }
            }).channel(channel).message(message).send()
          }


        }

        override def streamResult(result: StreamResult) {
          var message1 = result.getData.message
          pubnub.unsubscribe(channel)
          pubnub.removeListener(channel)
          testObj.test(true)

          assertTrue(message1.toString(), (message1.getClass()).equals(message.getClass()))
          assertTrue(message1.toString + " : " + message1.getClass(), (message1.getClass()).equals(message.getClass()))
          assertTrue(message1.asInstanceOf[JSONObject].getString("a").equals("b"))
          assertTrue(result.toString, verifyResultDetails(result) == true)
        }
      }

      pubnub.addStreamListener(channel, slistener)

      pubnub.subscribe().channel(channel).connect()

      await atMost(TIMEOUT, MILLISECONDS) until { testObj.checksRemaining() == 0 }


    }

    it("should be able to publish JSON Object literal string successfully", PublishTest) { pubnubTestConfig =>
      var pubnub  = pubnubTestConfig.pubnub
      var channel = "channel-" + getRandom()
      var message = "{\"a\":\"b\"}"
      var testObj = new PnTest(3)


      val slistener = new StreamListener() {
        override def streamStatus(status: StreamStatus) {

          if (status.isError()) {
            pubnub.unsubscribe(channel)
            pubnub.removeListener(channel)
            assertTrue(status.toString, false)
          }

          if (status.getCategory == StatusCategory.CONNECT) {
            testObj.test(true)

            pubnub.publish().callback(new PublishCallback(){
              override def status(status: PublishStatus){
                if (status.isError()) {
                  pubnub.unsubscribe(channel)
                  pubnub.removeListener(channel)
                  assertTrue(status.toString, false)
                }
                if (status.getCategory == StatusCategory.ACK) {
                  testObj.test(true)
                }
              }
            }).channel(channel).message(message).send()
          }


        }

        override def streamResult(result: StreamResult) {
          var message1 = result.getData.message
          pubnub.unsubscribe(channel)
          pubnub.removeListener(channel)
          testObj.test(true)

          assertTrue(message1.toString(), (message1.getClass()).equals(message.getClass()))
          assertTrue(message1.toString + " : " + message1.getClass(), (message1.getClass()).equals(message.getClass()))
          assertTrue(message1.toString + " : " + message.toString + "[" + message1.getClass() + "]", message1.equals(message))
          assertTrue(result.toString, verifyResultDetails(result) == true)
        }
      }

      pubnub.addStreamListener(channel, slistener)

      pubnub.subscribe().channel(channel).connect()

      await atMost(TIMEOUT, MILLISECONDS) until { testObj.checksRemaining() == 0 }


    }
    /*

    it("should be able to publish string with \\n successfully", PublishTest) { pubnubTestConfig =>
      var pubnub  = pubnubTestConfig.pubnub
      var channel = "channel-" + getRandom()
      var message = "[1,\n2]"
      var testObj = new PnTest(3)


      val slistener = new StreamListener() {
        override def streamStatus(status: StreamStatus) {

          if (status.isError()) {
            pubnub.unsubscribe(channel)
            pubnub.removeListener(channel)
            assertTrue(status.toString, false)
          }

          if (status.getCategory == StatusCategory.CONNECT) {
            testObj.test(true)

            pubnub.publish().callback(new PublishCallback(){
              override def status(status: PublishStatus){
                if (status.isError()) {
                  pubnub.unsubscribe(channel)
                  pubnub.removeListener(channel)
                  assertTrue(status.toString, false)
                }
                if (status.getCategory == StatusCategory.ACK) {
                  testObj.test(true)
                }
              }
            }).channel(channel).message(message).send()
          }


        }

        override def streamResult(result: StreamResult) {
          var message1 = result.getData.message
          pubnub.unsubscribe(channel)
          pubnub.removeListener(channel)
          testObj.test(true)

          assertTrue(message1.toString(), (message1.getClass()).equals(message.getClass()))
          assertTrue(message1.toString + " : " + message1.getClass(), (message1.getClass()).equals(message.getClass()))
          assertTrue(message1.toString + " : " + message.toString + "[" + message1.getClass() + "]", message1.equals(message))
          assertTrue(result.toString, verifyResultDetails(result) == true)
        }
      }

      pubnub.addStreamListener(channel, slistener)

      pubnub.subscribe().channel(channel).connect()

      await atMost(TIMEOUT, MILLISECONDS) until { testObj.checksRemaining() == 0 }

    }

    it("should be able to publish string with single double quote successfully", PublishTest) { pubnubTestConfig =>
      var pubnub  = pubnubTestConfig.pubnub
      var channel = "channel-" + getRandom()
      var message = "\""
      var testObj = new PnTest(3)


      val slistener = new StreamListener() {
        override def streamStatus(status: StreamStatus) {

          if (status.isError()) {
            pubnub.unsubscribe(channel)
            pubnub.removeListener(channel)
            assertTrue(status.toString, false)
          }

          if (status.getCategory == StatusCategory.CONNECT) {
            testObj.test(true)

            pubnub.publish().callback(new PublishCallback(){
              override def status(status: PublishStatus){
                if (status.isError()) {
                  pubnub.unsubscribe(channel)
                  pubnub.removeListener(channel)
                  assertTrue(status.toString, false)
                }
                if (status.getCategory == StatusCategory.ACK) {
                  testObj.test(true)
                }
              }
            }).channel(channel).message(message).send()
          }


        }

        override def streamResult(result: StreamResult) {
          var message1 = result.getData.message
          pubnub.unsubscribe(channel)
          pubnub.removeListener(channel)
          testObj.test(true)

          assertTrue(message1.toString(), (message1.getClass()).equals(message.getClass()))
          assertTrue(message1.toString + " : " + message1.getClass(), (message1.getClass()).equals(message.getClass()))
          assertTrue(message1.toString + " : " + message.toString + "[" + message1.getClass() + "]", message1.equals(message))
          assertTrue(result.toString, verifyResultDetails(result) == true)
        }
      }

      pubnub.addStreamListener(channel, slistener)

      pubnub.subscribe().channel(channel).connect()

      await atMost(TIMEOUT, MILLISECONDS) until { testObj.checksRemaining() == 0 }

    }


    it("should be able to publish string with quotes in start and end successfully", PublishTest) { pubnubTestConfig =>
      var pubnub  = pubnubTestConfig.pubnub
      var channel = "channel-" + getRandom()
      var message = "\"quoted string\""
      var testObj = new PnTest(3)


      val slistener = new StreamListener() {
        override def streamStatus(status: StreamStatus) {

          if (status.isError()) {
            pubnub.unsubscribe(channel)
            pubnub.removeListener(channel)
            assertTrue(status.toString, false)
          }

          if (status.getCategory == StatusCategory.CONNECT) {
            testObj.test(true)

            pubnub.publish().callback(new PublishCallback(){
              override def status(status: PublishStatus){
                if (status.isError()) {
                  pubnub.unsubscribe(channel)
                  pubnub.removeListener(channel)
                  assertTrue(status.toString, false)
                }
                if (status.getCategory == StatusCategory.ACK) {
                  testObj.test(true)
                }
              }
            }).channel(channel).message(message).send()
          }


        }

        override def streamResult(result: StreamResult) {
          var message1 = result.getData.message
          pubnub.unsubscribe(channel)
          pubnub.removeListener(channel)
          testObj.test(true)

          assertTrue(message1.toString(), (message1.getClass()).equals(message.getClass()))
          assertTrue(message1.toString + " : " + message1.getClass(), (message1.getClass()).equals(message.getClass()))
          assertTrue(message1.toString + " : " + message.toString + "[" + message1.getClass() + "]", message1.equals(message))
          assertTrue(result.toString, verifyResultDetails(result) == true)
        }
      }

      pubnub.addStreamListener(channel, slistener)

      pubnub.subscribe().channel(channel).connect()

      await atMost(TIMEOUT, MILLISECONDS) until { testObj.checksRemaining() == 0 }


    }
    */

  }
}
