
package com.pubnub.api.tests

import com.pubnub.api.Pubnub.Builder
import org.json.{JSONArray, JSONObject}
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



import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import scala.util.Random
import scala.util.Try

object ErrorTest extends Tag("com.pubnub.api.tests.ErrorTest")


@RunWith(classOf[JUnitRunner])
class SubscribeV2Spec extends fixture.FunSpec with AwaitilitySupport  {

  var PUBLISH_KEY   = ""
  var SUBSCRIBE_KEY = ""
  var SECRET_KEY    = ""
  var CIPHER_KEY    = ""
  var SSL           = false
  var RANDOM        = new Random()
  var TIMEOUT       = 30000
  var UNICODE       = false


  type FixtureParam = PubnubTestConfig

  def getRandom(unicode:Boolean = false): String = {
    var s = RANDOM.nextInt(99999999).toString
    if (unicode) {
      s += "☺☻✌☹"
    }
    return s
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

    UNICODE = Try(test.configMap.getRequired[String]("unicode").asInstanceOf[String].toBoolean).getOrElse(false)

    //val pubnub = new Pubnub(PUBLISH_KEY, SUBSCRIBE_KEY, SECRET_KEY, CIPHER_KEY, SSL)


    val pubnub = new Pubnub.Builder()
      .setPublishKey(PUBLISH_KEY)
      .setSubscribeKey(SUBSCRIBE_KEY)
      .setSecretKey(SECRET_KEY)
      .setCipherKey(CIPHER_KEY)
      .setSsl(SSL)
      .build()


    pubnub.setResumeOnReconnect(true)
    pubnubTestConfig.pubnub = pubnub
    pubnubTestConfig.unicode = UNICODE

    var pubnub_sync = new PubnubSync(PUBLISH_KEY, SUBSCRIBE_KEY, SECRET_KEY, CIPHER_KEY, SSL)

    var response = pubnub_sync.channelGroupListGroups()
    var groups = response.get("payload").asInstanceOf[JSONObject].get("groups").asInstanceOf[JSONArray]

    for (i <- 1 to groups.length()) {
      pubnub_sync.channelGroupRemoveGroup(groups.get(0).asInstanceOf[String])
    }

    withFixture(test.toNoArgTest(pubnubTestConfig))



  }


  describe("SubscribeV2()") {


    it("should receive message when subscribed with filtering attribute foo==bar " +
      " when message published with metadata foo:bar", ErrorTest) { pubnubTestConfig =>

      var pubnub  = pubnubTestConfig.pubnub
      var channel = "channel-" + getRandom()
      var message = "message-" + getRandom(pubnubTestConfig.unicode)
      var testObj = new PnTest(4)


      pubnub.subscribeWith.channel(channel).callback( new Callback {
        override def connectCallback(channel: String, message1: Object) {
          println(channel)
          testObj.test(true)
          pubnub.publish(channel, message, new Callback {

            override def successCallback(channel: String, message: Object) {
              testObj.test(true)
            }

            override def errorCallback(channel: String, error: PubnubError) {
              assert(false)
              testObj.test(false)
            }
          })

        }

        override def successCallback(channel: String, message1: Object) {
          pubnub.unsubscribe(channel)
          testObj.test(true)
          testObj.test(message1.equals(message))

        }
      }).connect()



      pubnub.subscribe(channel, new Callback {
        override def connectCallback(channel: String, message1: Object) {
          testObj.test(true)
          pubnub.publish(channel, message, new Callback {

            override def successCallback(channel: String, message: Object) {
              testObj.test(true)
            }

            override def errorCallback(channel: String, error: PubnubError) {
              assert(false)
              testObj.test(false)
            }
          })

        }
        override def successCallback(channel: String, message1: Object) {
          pubnub.unsubscribe(channel)
          testObj.test(true)
          testObj.test(message1.equals(message))

        }
      });


      await atMost(TIMEOUT, MILLISECONDS) until { testObj.checksRemaining() == 0 }
    }


    it("should be able to receive message successfully when subscribed with no filtering attribute" +
      " when message published with no metadata") { pubnubTestConfig =>

      var pubnub  = pubnubTestConfig.pubnub
      var channel = "channel-" + getRandom()
      var message = "message-" + getRandom(pubnubTestConfig.unicode)
      var testObj = new PnTest(4)

      pubnub.subscribe(channel, new Callback {
        override def connectCallback(channel: String, message1: Object) {
          testObj.test(true)
          pubnub.publish(channel, message, new Callback {

            override def successCallback(channel: String, message: Object) {
              testObj.test(true)
            }

            override def errorCallback(channel: String, error: PubnubError) {
              assert(false)
              testObj.test(false)
            }
          })

        }
        override def successCallback(channel: String, message1: Object) {
          pubnub.unsubscribe(channel)
          testObj.test(true)
          testObj.test(message1.equals(message))

        }
      });

      await atMost(TIMEOUT, MILLISECONDS) until { testObj.checksRemaining() == 0 }

    }

    it("should be able to receive message successfully when subscribed with no filtering attribute" +
      " when message published with metadata foo:bar") { pubnubTestConfig =>

      var pubnub  = pubnubTestConfig.pubnub
      var channel = "channel-" + getRandom()
      var message = "message-" + getRandom(pubnubTestConfig.unicode)
      var testObj = new PnTest(4)

      pubnub.subscribe(channel, new Callback {
        override def connectCallback(channel: String, message1: Object) {
          testObj.test(true)
          pubnub.publish(channel, message, new Callback {

            override def successCallback(channel: String, message: Object) {
              testObj.test(true)
            }

            override def errorCallback(channel: String, error: PubnubError) {
              assert(false)
              testObj.test(false)
            }
          })

        }
        override def successCallback(channel: String, message1: Object) {
          pubnub.unsubscribe(channel)
          testObj.test(true)
          testObj.test(message1.equals(message))

        }
      });

      await atMost(TIMEOUT, MILLISECONDS) until { testObj.checksRemaining() == 0 }

    }
    ignore("should not receive message when subscribed with filtering attribute foo==bar" +
      " when message published with no metadata") { pubnubTestConfig =>

      var pubnub  = pubnubTestConfig.pubnub
      var channel = "channel-" + getRandom()
      var message = "message-" + getRandom(pubnubTestConfig.unicode)
      var testObj = new PnTest(2)

      pubnub.subscribe(channel, new Callback {
        override def connectCallback(channel: String, message1: Object) {
          testObj.test(true)
          pubnub.publish(channel, message, new Callback {

            override def successCallback(channel: String, message: Object) {
              testObj.test(true)
            }

            override def errorCallback(channel: String, error: PubnubError) {
              assert(false)
              testObj.test(false)
            }
          })

        }
        override def successCallback(channel: String, message1: Object) {
          pubnub.unsubscribe(channel)
          assertTrue(false)
        }
      });

      await atMost(TIMEOUT, MILLISECONDS) until { testObj.checksRemaining() == 0 }


    }
    ignore("should not receive message when subscribed with filtering attribute a==b" +
      " when message published with metadata foo:bar") { pubnubTestConfig =>

      var pubnub  = pubnubTestConfig.pubnub
      var channel = "channel-" + getRandom()
      var message = "message-" + getRandom(pubnubTestConfig.unicode)
      var testObj = new PnTest(2)

      pubnub.subscribe(channel, new Callback {
        override def connectCallback(channel: String, message1: Object) {
          testObj.test(true)
          pubnub.publish(channel, message, new Callback {

            override def successCallback(channel: String, message: Object) {
              testObj.test(true)
            }

            override def errorCallback(channel: String, error: PubnubError) {
              assert(false)
              testObj.test(false)
            }
          })

        }
        override def successCallback(channel: String, message1: Object) {
          pubnub.unsubscribe(channel)
          assertTrue(false)
        }
      });

      await atMost(TIMEOUT, MILLISECONDS) until { testObj.checksRemaining() == 0 }


    }
    ignore("should not receive message when subscribed with filtering attribute foo==b" +
      " when message published with metadata foo:bar") { pubnubTestConfig =>

      var pubnub  = pubnubTestConfig.pubnub
      var channel = "channel-" + getRandom()
      var message = "message-" + getRandom(pubnubTestConfig.unicode)
      var testObj = new PnTest(2)

      pubnub.subscribe(channel, new Callback {
        override def connectCallback(channel: String, message1: Object) {
          testObj.test(true)
          pubnub.publish(channel, message, new Callback {

            override def successCallback(channel: String, message: Object) {
              testObj.test(true)
            }

            override def errorCallback(channel: String, error: PubnubError) {
              assert(false)
              testObj.test(false)
            }
          })

        }
        override def successCallback(channel: String, message1: Object) {
          pubnub.unsubscribe(channel)
          assertTrue(false)
        }
      });

      await atMost(TIMEOUT, MILLISECONDS) until { testObj.checksRemaining() == 0 }


    }
    ignore("should not receive message when subscribed with filtering attribute bar==foo" +
      " when message published with metadata foo:bar") { pubnubTestConfig =>

      var pubnub  = pubnubTestConfig.pubnub
      var channel = "channel-" + getRandom()
      var message = "message-" + getRandom(pubnubTestConfig.unicode)
      var testObj = new PnTest(4)

      pubnub.subscribe(channel, new Callback {
        override def connectCallback(channel: String, message1: Object) {
          testObj.test(true)
          pubnub.publish(channel, message, new Callback {

            override def successCallback(channel: String, message: Object) {
              testObj.test(true)
            }

            override def errorCallback(channel: String, error: PubnubError) {
              assert(false)
              testObj.test(false)
            }
          })

        }
        override def successCallback(channel: String, message1: Object) {
          pubnub.unsubscribe(channel)
          assertTrue(false)
        }
      });

      await atMost(TIMEOUT, MILLISECONDS) until { testObj.checksRemaining() == 0 }


    }


    it("should receive message when subscribed to wildcard channel with filtering attribute foo==bar " +
      " when message published with metadata foo:bar") { pubnubTestConfig =>

      var pubnub  = pubnubTestConfig.pubnub
      var channel = "channel-" + getRandom()
      var channel_wildcard = channel + ".*"
      var channel_wildcard_c = channel + ".a"
      var message = "message-" + getRandom(pubnubTestConfig.unicode)
      var testObj = new PnTest(4)

      pubnub.subscribe(channel_wildcard, new Callback {
        override def connectCallback(channel: String, message1: Object) {
          testObj.test(true)
          pubnub.publish(channel_wildcard_c, message, new Callback {

            override def successCallback(channel: String, message: Object) {
              testObj.test(true)
            }

            override def errorCallback(channel: String, error: PubnubError) {
              assert(false)
              testObj.test(false)
            }
          })

        }
        override def successCallback(channel: String, message1: Object) {
          pubnub.unsubscribe(channel)
          testObj.test(true)
          testObj.test(message1.equals(message))

        }
      });

      await atMost(TIMEOUT, MILLISECONDS) until { testObj.checksRemaining() == 0 }
    }


    it("should be able to receive message successfully when subscribed to wildcard channel with no filtering attribute" +
      " when message published with no metadata") { pubnubTestConfig =>

      var pubnub  = pubnubTestConfig.pubnub
      var channel = "channel-" + getRandom()
      var channel_wildcard = channel + ".*"
      var channel_wildcard_c = channel + ".a"
      var message = "message-" + getRandom(pubnubTestConfig.unicode)
      var testObj = new PnTest(4)

      pubnub.subscribe(channel_wildcard, new Callback {
        override def connectCallback(channel: String, message1: Object) {
          testObj.test(true)
          pubnub.publish(channel_wildcard_c, message, new Callback {

            override def successCallback(channel: String, message: Object) {
              testObj.test(true)
            }

            override def errorCallback(channel: String, error: PubnubError) {
              assert(false)
              testObj.test(false)
            }
          })

        }
        override def successCallback(channel: String, message1: Object) {
          pubnub.unsubscribe(channel)
          testObj.test(true)
          testObj.test(message1.equals(message))

        }
      });

      await atMost(TIMEOUT, MILLISECONDS) until { testObj.checksRemaining() == 0 }

    }

    it("should be able to receive message successfully when subscribed to wildcard channel with no filtering attribute" +
      " when message published with metadata foo:bar") { pubnubTestConfig =>

      var pubnub  = pubnubTestConfig.pubnub
      var channel = "channel-" + getRandom()
      var channel_wildcard = channel + ".*"
      var channel_wildcard_c = channel + ".a"
      var message = "message-" + getRandom(pubnubTestConfig.unicode)
      var testObj = new PnTest(4)

      pubnub.subscribe(channel_wildcard, new Callback {
        override def connectCallback(channel: String, message1: Object) {
          testObj.test(true)
          pubnub.publish(channel_wildcard_c, message, new Callback {

            override def successCallback(channel: String, message: Object) {
              testObj.test(true)
            }

            override def errorCallback(channel: String, error: PubnubError) {
              assert(false)
              testObj.test(false)
            }
          })

        }
        override def successCallback(channel: String, message1: Object) {
          pubnub.unsubscribe(channel)
          testObj.test(true)
          testObj.test(message1.equals(message))

        }
      });

      await atMost(TIMEOUT, MILLISECONDS) until { testObj.checksRemaining() == 0 }

    }
    ignore("should not receive message when subscribed to wildcard channel with filtering attribute foo==bar" +
      " when message published with no metadata") { pubnubTestConfig =>

      var pubnub  = pubnubTestConfig.pubnub
      var channel = "channel-" + getRandom()
      var channel_wildcard = channel + ".*"
      var channel_wildcard_c = channel + ".a"
      var message = "message-" + getRandom(pubnubTestConfig.unicode)
      var testObj = new PnTest(2)

      pubnub.subscribe(channel_wildcard, new Callback {
        override def connectCallback(channel: String, message1: Object) {
          testObj.test(true)
          pubnub.publish(channel_wildcard_c, message, new Callback {

            override def successCallback(channel: String, message: Object) {
              testObj.test(true)
            }

            override def errorCallback(channel: String, error: PubnubError) {
              assert(false)
              testObj.test(false)
            }
          })

        }
        override def successCallback(channel: String, message1: Object) {
          pubnub.unsubscribe(channel)
          assertTrue(false)
        }
      });

      await atMost(TIMEOUT, MILLISECONDS) until { testObj.checksRemaining() == 0 }


    }
    ignore("should not receive message when subscribed to wildcard channel with filtering attribute a==b" +
      " when message published with metadata foo:bar") { pubnubTestConfig =>

      var pubnub  = pubnubTestConfig.pubnub
      var channel = "channel-" + getRandom()
      var channel_wildcard = channel + ".*"
      var channel_wildcard_c = channel + ".a"
      var message = "message-" + getRandom(pubnubTestConfig.unicode)
      var testObj = new PnTest(2)

      pubnub.subscribe(channel_wildcard, new Callback {
        override def connectCallback(channel: String, message1: Object) {
          testObj.test(true)
          pubnub.publish(channel_wildcard_c, message, new Callback {

            override def successCallback(channel: String, message: Object) {
              testObj.test(true)
            }

            override def errorCallback(channel: String, error: PubnubError) {
              assert(false)
              testObj.test(false)
            }
          })

        }
        override def successCallback(channel: String, message1: Object) {
          pubnub.unsubscribe(channel)
          assertTrue(false)
        }
      });

      await atMost(TIMEOUT, MILLISECONDS) until { testObj.checksRemaining() == 0 }


    }
    ignore("should not receive message when subscribed to wildcard channel with filtering attribute foo==b" +
      " when message published with metadata foo:bar") { pubnubTestConfig =>

      var pubnub  = pubnubTestConfig.pubnub
      var channel = "channel-" + getRandom()
      var channel_wildcard = channel + ".*"
      var channel_wildcard_c = channel + ".a"
      var message = "message-" + getRandom(pubnubTestConfig.unicode)
      var testObj = new PnTest(2)

      pubnub.subscribe(channel_wildcard, new Callback {
        override def connectCallback(channel: String, message1: Object) {
          testObj.test(true)
          pubnub.publish(channel_wildcard_c, message, new Callback {

            override def successCallback(channel: String, message: Object) {
              testObj.test(true)
            }

            override def errorCallback(channel: String, error: PubnubError) {
              assert(false)
              testObj.test(false)
            }
          })

        }
        override def successCallback(channel: String, message1: Object) {
          pubnub.unsubscribe(channel)
          assertTrue(false)
        }
      });

      await atMost(TIMEOUT, MILLISECONDS) until { testObj.checksRemaining() == 0 }


    }
    ignore("should not receive message when subscribed to wildcard channel with filtering attribute bar==foo" +
      " when message published with metadata foo:bar") { pubnubTestConfig =>

      var pubnub  = pubnubTestConfig.pubnub
      var channel = "channel-" + getRandom()
      var channel_wildcard = channel + ".*"
      var channel_wildcard_c = channel + ".a"
      var message = "message-" + getRandom(pubnubTestConfig.unicode)
      var testObj = new PnTest(4)

      pubnub.subscribe(channel_wildcard, new Callback {
        override def connectCallback(channel: String, message1: Object) {
          testObj.test(true)
          pubnub.publish(channel_wildcard_c, message, new Callback {

            override def successCallback(channel: String, message: Object) {
              testObj.test(true)
            }

            override def errorCallback(channel: String, error: PubnubError) {
              assert(false)
              testObj.test(false)
            }
          })

        }
        override def successCallback(channel: String, message1: Object) {
          pubnub.unsubscribe(channel)
          assertTrue(false)
        }
      });

      await atMost(TIMEOUT, MILLISECONDS) until { testObj.checksRemaining() == 0 }


    }

    /*********/

    it("should receive message when subscribed to channel and wildcard channel with filtering attribute foo==bar " +
      " when message published with metadata foo:bar") { pubnubTestConfig =>

      var pubnub  = pubnubTestConfig.pubnub
      var channel = "channel-" + getRandom()
      var channel_wildcard = channel + ".*"
      var channel_wildcard_c = channel + ".a"
      var message = "message-" + getRandom(pubnubTestConfig.unicode)
      var testObj = new PnTest(8)

      pubnub.subscribe(Array(channel, channel_wildcard), new Callback {
        override def connectCallback(channel: String, message1: Object) {
          testObj.test(true)
          pubnub.publish(channel_wildcard_c, message, new Callback {
            override def successCallback(channel: String, message: Object) {
              testObj.test(true)
            }

            override def errorCallback(channel: String, error: PubnubError) {
              assert(false)
              testObj.test(false)
            }
          })

        }
        override def successCallback(channel: String, message1: Object) {
          testObj.test(true)
          testObj.test(message1.equals(message))
        }
      });

      await atMost(TIMEOUT, MILLISECONDS) until { testObj.checksRemaining() == 0 }
    }


    it("should be able to receive message successfully when subscribed to channel and wildcard channel with no filtering attribute" +
      " when message published with no metadata") { pubnubTestConfig =>

      var pubnub  = pubnubTestConfig.pubnub
      var channel = "channel-" + getRandom()
      var channel_wildcard = channel + ".*"
      var channel_wildcard_c = channel + ".a"
      var message = "message-" + getRandom(pubnubTestConfig.unicode)
      var testObj = new PnTest(8)

      pubnub.subscribe(Array(channel, channel_wildcard), new Callback {
        override def connectCallback(channel: String, message1: Object) {
          testObj.test(true)
          pubnub.publish(channel_wildcard_c, message, new Callback {

            override def successCallback(channel: String, message: Object) {
              testObj.test(true)
            }

            override def errorCallback(channel: String, error: PubnubError) {
              assert(false)
              testObj.test(false)
            }
          })

        }
        override def successCallback(channel: String, message1: Object) {
          testObj.test(true)
          testObj.test(message1.equals(message))

        }
      });

      await atMost(TIMEOUT, MILLISECONDS) until { testObj.checksRemaining() == 0 }

    }

    it("should be able to receive message successfully when subscribed to channel and wildcard channel with no filtering attribute" +
      " when message published with metadata foo:bar") { pubnubTestConfig =>

      var pubnub  = pubnubTestConfig.pubnub
      var channel = "channel-" + getRandom()
      var channel_wildcard = channel + ".*"
      var channel_wildcard_c = channel + ".a"
      var message = "message-" + getRandom(pubnubTestConfig.unicode)
      var testObj = new PnTest(8)

      pubnub.subscribe(Array(channel, channel_wildcard), new Callback {
        override def connectCallback(channel: String, message1: Object) {
          testObj.test(true)
          pubnub.publish(channel_wildcard_c, message, new Callback {

            override def successCallback(channel: String, message: Object) {
              testObj.test(true)
            }

            override def errorCallback(channel: String, error: PubnubError) {
              assert(false)
              testObj.test(false)
            }
          })

        }
        override def successCallback(channel: String, message1: Object) {
          testObj.test(true)
          testObj.test(message1.equals(message))

        }
      });

      await atMost(TIMEOUT, MILLISECONDS) until { testObj.checksRemaining() == 0 }

    }
    ignore("should not receive message when subscribed to channel and wildcard channel with filtering attribute foo==bar" +
      " when message published with no metadata") { pubnubTestConfig =>

      var pubnub  = pubnubTestConfig.pubnub
      var channel = "channel-" + getRandom()
      var channel_wildcard = channel + ".*"
      var channel_wildcard_c = channel + ".a"
      var message = "message-" + getRandom(pubnubTestConfig.unicode)
      var testObj = new PnTest(4)

      pubnub.subscribe(Array(channel, channel_wildcard), new Callback {
        override def connectCallback(channel: String, message1: Object) {
          testObj.test(true)
          pubnub.publish(channel_wildcard_c, message, new Callback {

            override def successCallback(channel: String, message: Object) {
              testObj.test(true)
            }

            override def errorCallback(channel: String, error: PubnubError) {
              assert(false)
              testObj.test(false)
            }
          })

        }
        override def successCallback(channel: String, message1: Object) {
          pubnub.unsubscribe(channel)
          assertTrue(false)
        }
      });

      await atMost(TIMEOUT, MILLISECONDS) until { testObj.checksRemaining() == 0 }


    }
    ignore("should not receive message when subscribed to channel and wildcard channel with filtering attribute a==b" +
      " when message published with metadata foo:bar") { pubnubTestConfig =>

      var pubnub  = pubnubTestConfig.pubnub
      var channel = "channel-" + getRandom()
      var channel_wildcard = channel + ".*"
      var channel_wildcard_c = channel + ".a"
      var message = "message-" + getRandom(pubnubTestConfig.unicode)
      var testObj = new PnTest(4)

      pubnub.subscribe(Array(channel, channel_wildcard), new Callback {
        override def connectCallback(channel: String, message1: Object) {
          testObj.test(true)
          pubnub.publish(channel_wildcard_c, message, new Callback {

            override def successCallback(channel: String, message: Object) {
              testObj.test(true)
            }

            override def errorCallback(channel: String, error: PubnubError) {
              assert(false)
              testObj.test(false)
            }
          })

        }
        override def successCallback(channel: String, message1: Object) {
          pubnub.unsubscribe(channel)
          assertTrue(false)
        }
      });

      await atMost(TIMEOUT, MILLISECONDS) until { testObj.checksRemaining() == 0 }


    }
    ignore("should not receive message when subscribed to channel and wildcard channel with filtering attribute foo==b" +
      " when message published with metadata foo:bar") { pubnubTestConfig =>

      var pubnub  = pubnubTestConfig.pubnub
      var channel = "channel-" + getRandom()
      var channel_wildcard = channel + ".*"
      var channel_wildcard_c = channel + ".a"
      var message = "message-" + getRandom(pubnubTestConfig.unicode)
      var testObj = new PnTest(4)

      pubnub.subscribe(Array(channel, channel_wildcard), new Callback {
        override def connectCallback(channel: String, message1: Object) {
          testObj.test(true)
          pubnub.publish(channel_wildcard_c, message, new Callback {

            override def successCallback(channel: String, message: Object) {
              testObj.test(true)
            }

            override def errorCallback(channel: String, error: PubnubError) {
              assert(false)
              testObj.test(false)
            }
          })

        }
        override def successCallback(channel: String, message1: Object) {
          pubnub.unsubscribe(channel)
          assertTrue(false)
        }
      });

      await atMost(TIMEOUT, MILLISECONDS) until { testObj.checksRemaining() == 0 }


    }
    ignore("should not receive message when subscribed to channel and wildcard channel with filtering attribute bar==foo" +
      " when message published with metadata foo:bar") { pubnubTestConfig =>

      var pubnub  = pubnubTestConfig.pubnub
      var channel = "channel-" + getRandom()
      var channel_wildcard = channel + ".*"
      var channel_wildcard_c = channel + ".a"
      var message = "message-" + getRandom(pubnubTestConfig.unicode)
      var testObj = new PnTest(4)

      pubnub.subscribe(Array(channel, channel_wildcard), new Callback {
        override def connectCallback(channel: String, message1: Object) {
          testObj.test(true)
          pubnub.publish(channel_wildcard_c, message, new Callback {

            override def successCallback(channel: String, message: Object) {
              testObj.test(true)
            }

            override def errorCallback(channel: String, error: PubnubError) {
              assert(false)
              testObj.test(false)
            }
          })

        }
        override def successCallback(channel: String, message1: Object) {
          pubnub.unsubscribe(channel)
          assertTrue(false)
        }
      });

      await atMost(TIMEOUT, MILLISECONDS) until { testObj.checksRemaining() == 0 }


    }


    /*********/


    ignore("should receive message when subscribed to channel group with filtering attribute foo==bar " +
      " when message published with metadata foo:bar") { pubnubTestConfig =>

      var pubnub  = pubnubTestConfig.pubnub
      var channel = "channel-" + getRandom()
      var channel_group = channel + "-group"
      var channel_group_c = channel + "-channel"
      var message = "message-" + getRandom(pubnubTestConfig.unicode)
      var testObj = new PnTest(4)

      /*
      pubnub.subscribe(channel_wildcard, new Callback {
        override def connectCallback(channel: String, message1: Object) {
          testObj.test(true)
          pubnub.publish(channel_wildcard_c, message, new Callback {

            override def successCallback(channel: String, message: Object) {
              testObj.test(true)
            }

            override def errorCallback(channel: String, error: PubnubError) {
              assert(false)
              testObj.test(false)
            }
          })

        }
        override def successCallback(channel: String, message1: Object) {
          pubnub.unsubscribe(channel)
          testObj.test(true)
          testObj.test(message1.equals(message))

        }
      });
      */
      await atMost(TIMEOUT, MILLISECONDS) until { testObj.checksRemaining() == 0 }
    }


    ignore("should be able to receive message successfully when subscribed to channel group with no filtering attribute" +
      " when message published with no metadata") { pubnubTestConfig =>

      var pubnub  = pubnubTestConfig.pubnub
      var channel = "channel-" + getRandom()
      var channel_group = channel + "-group"
      var channel_group_c = channel + "-channel"
      var message = "message-" + getRandom(pubnubTestConfig.unicode)
      var testObj = new PnTest(4)

      /*
      pubnub.subscribe(channel_wildcard, new Callback {
        override def connectCallback(channel: String, message1: Object) {
          testObj.test(true)
          pubnub.publish(channel_wildcard_c, message, new Callback {

            override def successCallback(channel: String, message: Object) {
              testObj.test(true)
            }

            override def errorCallback(channel: String, error: PubnubError) {
              assert(false)
              testObj.test(false)
            }
          })

        }
        override def successCallback(channel: String, message1: Object) {
          pubnub.unsubscribe(channel)
          testObj.test(true)
          testObj.test(message1.equals(message))

        }
      });*/

      await atMost(TIMEOUT, MILLISECONDS) until { testObj.checksRemaining() == 0 }

    }

    ignore("should be able to receive message successfully when subscribed to channel group with no filtering attribute" +
      " when message published with metadata foo:bar") { pubnubTestConfig =>

      var pubnub  = pubnubTestConfig.pubnub
      var channel = "channel-" + getRandom()
      var channel_group = channel + "-group"
      var channel_group_c = channel + "-channel"
      var message = "message-" + getRandom(pubnubTestConfig.unicode)
      var testObj = new PnTest(4)

      /*
      pubnub.subscribe(channel_wildcard, new Callback {
        override def connectCallback(channel: String, message1: Object) {
          testObj.test(true)
          pubnub.publish(channel_wildcard_c, message, new Callback {

            override def successCallback(channel: String, message: Object) {
              testObj.test(true)
            }

            override def errorCallback(channel: String, error: PubnubError) {
              assert(false)
              testObj.test(false)
            }
          })

        }
        override def successCallback(channel: String, message1: Object) {
          pubnub.unsubscribe(channel)
          testObj.test(true)
          testObj.test(message1.equals(message))

        }
      });
      */
      await atMost(TIMEOUT, MILLISECONDS) until { testObj.checksRemaining() == 0 }

    }
    ignore("should not receive message when subscribed to channel group  with filtering attribute foo==bar" +
      " when message published with no metadata") { pubnubTestConfig =>

      var pubnub  = pubnubTestConfig.pubnub
      var channel = "channel-" + getRandom()
      var channel_group = channel + "-group"
      var channel_group_c = channel + "-channel"
      var message = "message-" + getRandom(pubnubTestConfig.unicode)
      var testObj = new PnTest(2)
      /*
      pubnub.subscribe(channel_wildcard, new Callback {
        override def connectCallback(channel: String, message1: Object) {
          testObj.test(true)
          pubnub.publish(channel_wildcard_c, message, new Callback {

            override def successCallback(channel: String, message: Object) {
              testObj.test(true)
            }

            override def errorCallback(channel: String, error: PubnubError) {
              assert(false)
              testObj.test(false)
            }
          })

        }
        override def successCallback(channel: String, message1: Object) {
          pubnub.unsubscribe(channel)
          assertTrue(false)
        }
      });
      */

      await atMost(TIMEOUT, MILLISECONDS) until { testObj.checksRemaining() == 0 }


    }
    ignore("should not receive message when subscribed to channel group with filtering attribute a==b" +
      " when message published with metadata foo:bar") { pubnubTestConfig =>

      var pubnub  = pubnubTestConfig.pubnub
      var channel = "channel-" + getRandom()
      var channel_group = channel + "-group"
      var channel_group_c = channel + "-channel"
      var message = "message-" + getRandom(pubnubTestConfig.unicode)
      var testObj = new PnTest(2)

      /*
      pubnub.subscribe(channel_wildcard, new Callback {
        override def connectCallback(channel: String, message1: Object) {
          testObj.test(true)
          pubnub.publish(channel_wildcard_c, message, new Callback {

            override def successCallback(channel: String, message: Object) {
              testObj.test(true)
            }

            override def errorCallback(channel: String, error: PubnubError) {
              assert(false)
              testObj.test(false)
            }
          })

        }
        override def successCallback(channel: String, message1: Object) {
          pubnub.unsubscribe(channel)
          assertTrue(false)
        }
      });
      */
      await atMost(TIMEOUT, MILLISECONDS) until { testObj.checksRemaining() == 0 }


    }
    ignore("should not receive message when subscribed to channel group with filtering attribute foo==b" +
      " when message published with metadata foo:bar") { pubnubTestConfig =>

      var pubnub  = pubnubTestConfig.pubnub
      var channel = "channel-" + getRandom()
      var channel_group = channel + "-group"
      var channel_group_c = channel + "-channel"
      var message = "message-" + getRandom(pubnubTestConfig.unicode)
      var testObj = new PnTest(2)
      /*
      pubnub.subscribe(channel_wildcard, new Callback {
        override def connectCallback(channel: String, message1: Object) {
          testObj.test(true)
          pubnub.publish(channel_wildcard_c, message, new Callback {

            override def successCallback(channel: String, message: Object) {
              testObj.test(true)
            }

            override def errorCallback(channel: String, error: PubnubError) {
              assert(false)
              testObj.test(false)
            }
          })

        }
        override def successCallback(channel: String, message1: Object) {
          pubnub.unsubscribe(channel)
          assertTrue(false)
        }
      });
      */

      await atMost(TIMEOUT, MILLISECONDS) until { testObj.checksRemaining() == 0 }


    }
    ignore("should not receive message when subscribed to channel group with filtering attribute bar==foo" +
      " when message published with metadata foo:bar") { pubnubTestConfig =>

      var pubnub  = pubnubTestConfig.pubnub
      var channel = "channel-" + getRandom()
      var channel_group = channel + "-group"
      var channel_group_c = channel + "-channel"
      var message = "message-" + getRandom(pubnubTestConfig.unicode)
      var testObj = new PnTest(4)
      /*
      pubnub.subscribe(channel_wildcard, new Callback {
        override def connectCallback(channel: String, message1: Object) {
          testObj.test(true)
          pubnub.publish(channel_wildcard_c, message, new Callback {

            override def successCallback(channel: String, message: Object) {
              testObj.test(true)
            }

            override def errorCallback(channel: String, error: PubnubError) {
              assert(false)
              testObj.test(false)
            }
          })

        }
        override def successCallback(channel: String, message1: Object) {
          pubnub.unsubscribe(channel)
          assertTrue(false)
        }
      });
      */

      await atMost(TIMEOUT, MILLISECONDS) until { testObj.checksRemaining() == 0 }


    }

    /**************/



    it("should receive message when subscribed to channel and channel group with filtering attribute foo==bar " +
      " when message published with metadata foo:bar", ErrorTest) { pubnubTestConfig =>

      var pubnub  = pubnubTestConfig.pubnub
      var channel = "channel-" + getRandom()
      var channel_group = channel + "-group"
      var channel_group_c = channel + "-channel"
      var message = "message-" + getRandom(pubnubTestConfig.unicode)
      var message_group_c = message + "-group-channel"

      var testObj = new PnTest(1 + 1 + 1 + 1 + 1 + 1 + 1)

      pubnub.channelGroupAddChannel(channel_group, channel_group_c, new Callback {

        override def successCallback(channel1: String, message1: Object) {

          testObj.test(true)

          pubnub.subscribe(channel, new Callback {

            override def connectCallback(channel1: String, message1: Object) {
              testObj.test(true)

              pubnub.channelGroupSubscribe(Array(channel_group), new Callback {

                override def connectCallback(channel1: String, message1: Object) {

                  testObj.test(true)
                  pubnub.publish(channel, message, new Callback {

                    override def successCallback(channel1: String, message1: Object) {

                      testObj.test(true)
                      pubnub.publish(channel_group_c, message_group_c, new Callback {

                        override def successCallback(channel1: String, message1: Object) {
                          testObj.test(true)
                        }

                        override def errorCallback(channel1: String, error: PubnubError) {
                          assertTrue(false)
                        }

                      })
                    }

                    override def errorCallback(channel1: String, error: PubnubError) {
                      assertTrue(false)
                    }
                  })
                }

                override def successCallback(channel1: String, message1: Object) {
                  //println(message1)
                  testObj.test(true)
                  //pubnub.channelGroupUnsubscribe(channel_group)
                  assertTrue(message1.equals(message_group_c))
                }
              })
            }

            override def successCallback(channel1: String, message1: Object) {
              //println(message1)
              testObj.test(true)
              //pubnub.unsubscribe(channel)
              assertTrue(message1.equals(message))
            }
          })

        }

        override def errorCallback(channel: String, error: PubnubError) {
          assert(false)
          testObj.test(false)
        }
      })


      await atMost(TIMEOUT, MILLISECONDS) until { testObj.checksRemaining() == 0 }
    }


    it("should receive message when subscribed to channel and channel group with filtering attribute foo==bar " +
      " when message published with metadata foo:bar" +
      ", and when unsubscribed on receiving message", ErrorTest) { pubnubTestConfig =>

      var pubnub  = pubnubTestConfig.pubnub
      var channel = "channel-" + getRandom()
      var channel_group = channel + "-group"
      var channel_group_c = channel + "-channel"
      var message = "message-" + getRandom(pubnubTestConfig.unicode)
      var message_group_c = message + "-group-channel"

      var testObj = new PnTest(1 + 1 + 1 + 1 + 1 + 1 + 1)

      pubnub.channelGroupAddChannel(channel_group, channel_group_c, new Callback {

        override def successCallback(channel1: String, message1: Object) {

          testObj.test(true)

          pubnub.subscribe(channel, new Callback {

            override def connectCallback(channel1: String, message1: Object) {
              testObj.test(true)

              pubnub.channelGroupSubscribe(Array(channel_group), new Callback {

                override def connectCallback(channel1: String, message1: Object) {

                  testObj.test(true)
                  pubnub.publish(channel, message, new Callback {

                    override def successCallback(channel1: String, message1: Object) {

                      testObj.test(true)
                      pubnub.publish(channel_group_c, message_group_c, new Callback {

                        override def successCallback(channel1: String, message1: Object) {
                          testObj.test(true)
                        }

                        override def errorCallback(channel1: String, error: PubnubError) {
                          assertTrue(false)
                        }

                      })
                    }

                    override def errorCallback(channel1: String, error: PubnubError) {
                      assertTrue(false)
                    }
                  })
                }

                override def successCallback(channel1: String, message1: Object) {
                  //println(message1)
                  testObj.test(true)
                  pubnub.channelGroupUnsubscribe(channel_group)
                  assertTrue(message1.equals(message_group_c))
                }
              })
            }

            override def successCallback(channel1: String, message1: Object) {
              //println(message1)
              testObj.test(true)
              pubnub.unsubscribe(channel)
              assertTrue(message1.equals(message))
            }
          })

        }

        override def errorCallback(channel: String, error: PubnubError) {
          assert(false)
          testObj.test(false)
        }
      })


      await atMost(TIMEOUT, MILLISECONDS) until { testObj.checksRemaining() == 0 }
    }

    ignore("should be able to receive message successfully when subscribed to channel and channel group with no filtering attribute" +
      " when message published with no metadata") { pubnubTestConfig =>

      var pubnub  = pubnubTestConfig.pubnub
      var channel = "channel-" + getRandom()
      var channel_group = channel + "-group"
      var channel_group_c = channel + "-channel"
      var message = "message-" + getRandom(pubnubTestConfig.unicode)
      var testObj = new PnTest(4)

      /*
      pubnub.subscribe(channel_wildcard, new Callback {
        override def connectCallback(channel: String, message1: Object) {
          testObj.test(true)
          pubnub.publish(channel_wildcard_c, message, new Callback {

            override def successCallback(channel: String, message: Object) {
              testObj.test(true)
            }

            override def errorCallback(channel: String, error: PubnubError) {
              assert(false)
              testObj.test(false)
            }
          })

        }
        override def successCallback(channel: String, message1: Object) {
          pubnub.unsubscribe(channel)
          testObj.test(true)
          testObj.test(message1.equals(message))

        }
      });
      */

      await atMost(TIMEOUT, MILLISECONDS) until { testObj.checksRemaining() == 0 }

    }

    ignore("should be able to receive message successfully when subscribed to channel and channel group with no filtering attribute" +
      " when message published with metadata foo:bar") { pubnubTestConfig =>

      var pubnub  = pubnubTestConfig.pubnub
      var channel = "channel-" + getRandom()
      var channel_group = channel + "-group"
      var channel_group_c = channel + "-channel"
      var message = "message-" + getRandom(pubnubTestConfig.unicode)
      var testObj = new PnTest(4)

      /*
      pubnub.channelGroupAddChannel(channel_group, channel_group_c, new Callback {
        override def successCallback(channel: String, message1: Object) {
          testObj.test(true)

          pubnub.subscribe(channel, new Callback {
            override def connectCallback(channel: String, message1: Object) {
              testObj.test(true)
              pubnub.subscribe(channel_group, new Callback {
                override def connectCallback(channel: String, message1: Object) {
                  testObj.test(true)
                  pubnub.subscribe(channel_wildcard_c, new Callback {
                    override def connectCallback(channel: String, message1: Object) {
                      testObj.test(true)

                    }
                    override def successCallback(channel: String, message1: Object) {
                      pubnub.unsubscribe(channel)
                      assertTrue(false)
                    }
                  })
                }
                override def successCallback(channel: String, message1: Object) {
                  pubnub.unsubscribe(channel)
                  assertTrue(false)
                }
              })
            }
            override def successCallback(channel: String, message1: Object) {
              pubnub.unsubscribe(channel)
              assertTrue(false)
            }
          })

        }

        override def errorCallback(channel: String, error: PubnubError) {
          assert(false)
          testObj.test(false)
        }
      })
      */

      await atMost(TIMEOUT, MILLISECONDS) until { testObj.checksRemaining() == 0 }

    }
    ignore("should not receive message when subscribed to channel and channel group  with filtering attribute foo==bar" +
      " when message published with no metadata") { pubnubTestConfig =>

      var pubnub  = pubnubTestConfig.pubnub
      var channel = "channel-" + getRandom()
      var channel_group = channel + "-group"
      var channel_group_c = channel + "-channel"
      var message = "message-" + getRandom(pubnubTestConfig.unicode)
      var testObj = new PnTest(2)

      /*
      pubnub.subscribe(channel_wildcard, new Callback {
        override def connectCallback(channel: String, message1: Object) {
          testObj.test(true)
          pubnub.publish(channel_wildcard_c, message, new Callback {

            override def successCallback(channel: String, message: Object) {
              testObj.test(true)
            }

            override def errorCallback(channel: String, error: PubnubError) {
              assert(false)
              testObj.test(false)
            }
          })

        }
        override def successCallback(channel: String, message1: Object) {
          pubnub.unsubscribe(channel)
          assertTrue(false)
        }
      });
      */
      await atMost(TIMEOUT, MILLISECONDS) until { testObj.checksRemaining() == 0 }


    }
    ignore("should not receive message when subscribed to channel and channel group with filtering attribute a==b" +
      " when message published with metadata foo:bar") { pubnubTestConfig =>

      var pubnub  = pubnubTestConfig.pubnub
      var channel = "channel-" + getRandom()
      var channel_group = channel + "-group"
      var channel_group_c = channel + "-channel"
      var message = "message-" + getRandom(pubnubTestConfig.unicode)
      var testObj = new PnTest(2)

      /*
      pubnub.subscribe(channel_wildcard, new Callback {
        override def connectCallback(channel: String, message1: Object) {
          testObj.test(true)
          pubnub.publish(channel_wildcard_c, message, new Callback {

            override def successCallback(channel: String, message: Object) {
              testObj.test(true)
            }

            override def errorCallback(channel: String, error: PubnubError) {
              assert(false)
              testObj.test(false)
            }
          })

        }
        override def successCallback(channel: String, message1: Object) {
          pubnub.unsubscribe(channel)
          assertTrue(false)
        }
      });
      */
      await atMost(TIMEOUT, MILLISECONDS) until { testObj.checksRemaining() == 0 }


    }
    ignore("should not receive message when subscribed to channel and channel group with filtering attribute foo==b" +
      " when message published with metadata foo:bar") { pubnubTestConfig =>

      var pubnub  = pubnubTestConfig.pubnub
      var channel = "channel-" + getRandom()
      var channel_group = channel + "-group"
      var channel_group_c = channel + "-channel"
      var message = "message-" + getRandom(pubnubTestConfig.unicode)
      var testObj = new PnTest(2)
      /*
      pubnub.subscribe(channel_wildcard, new Callback {
        override def connectCallback(channel: String, message1: Object) {
          testObj.test(true)
          pubnub.publish(channel_wildcard_c, message, new Callback {

            override def successCallback(channel: String, message: Object) {
              testObj.test(true)
            }

            override def errorCallback(channel: String, error: PubnubError) {
              assert(false)
              testObj.test(false)
            }
          })

        }
        override def successCallback(channel: String, message1: Object) {
          pubnub.unsubscribe(channel)
          assertTrue(false)
        }
      });
      */

      await atMost(TIMEOUT, MILLISECONDS) until { testObj.checksRemaining() == 0 }


    }
    ignore("should not receive message when subscribed to channel and channel group with filtering attribute bar==foo" +
      " when message published with metadata foo:bar") { pubnubTestConfig =>

      var pubnub  = pubnubTestConfig.pubnub
      var channel = "channel-" + getRandom()
      var channel_group = channel + "-group"
      var channel_group_c = channel + "-channel"
      var message = "message-" + getRandom(pubnubTestConfig.unicode)
      var testObj = new PnTest(4)

      /*
      pubnub.subscribe(channel_wildcard, new Callback {
        override def connectCallback(channel: String, message1: Object) {
          testObj.test(true)
          pubnub.publish(channel_wildcard_c, message, new Callback {

            override def successCallback(channel: String, message: Object) {
              testObj.test(true)
            }

            override def errorCallback(channel: String, error: PubnubError) {
              assert(false)
              testObj.test(false)
            }
          })

        }
        override def successCallback(channel: String, message1: Object) {
          pubnub.unsubscribe(channel)
          assertTrue(false)
        }
      });
      */
      await atMost(TIMEOUT, MILLISECONDS) until { testObj.checksRemaining() == 0 }


    }



    /**************/



    it("should receive message when subscribed to channel, wildcard channel" +
      " and channel group with filtering attribute foo==bar " +
      " when message published with metadata foo:bar", ErrorTest) { pubnubTestConfig =>

      var pubnub  = pubnubTestConfig.pubnub
      var channel = "channel-" + getRandom()
      var channel_wildcard = channel + ".*"
      var channel_wildcard_c = channel + ".a"
      var channel_group = channel + "-group"
      var channel_group_c = channel + "-channel"
      var message = "message-" + getRandom(pubnubTestConfig.unicode)
      var message_group_c = message + "-group-channel"
      var message_wildcard_c = message + "-wildcard-channel"

      var testObj = new PnTest(1 + 1 + 1 + 1 + 1 + 2 + 3)

      pubnub.channelGroupAddChannel(channel_group, channel_group_c, new Callback {

        override def successCallback(channel1: String, message1: Object) {

          testObj.test(true)

          pubnub.subscribe(channel, new Callback {

            override def connectCallback(channel1: String, message1: Object) {
              testObj.test(true)

              pubnub.channelGroupSubscribe(Array(channel_group), new Callback {

                override def connectCallback(channel1: String, message1: Object) {

                  testObj.test(true)
                  pubnub.subscribe(channel_wildcard, new Callback {

                    override def connectCallback(channel1: String, message1: Object) {

                      testObj.test(true)
                      pubnub.publish(channel, message, new Callback {

                        override def successCallback(channel1: String, message1: Object) {

                          testObj.test(true)
                          pubnub.publish(channel_group_c, message_group_c, new Callback {

                            override def successCallback(channel1: String, message1: Object) {
                              testObj.test(true)
                              pubnub.publish(channel_wildcard_c, message_wildcard_c, new Callback {

                                override def successCallback(channel1: String, message1: Object) {
                                  testObj.test(true)
                                }

                                override def errorCallback(channel1: String, error: PubnubError) {
                                  assertTrue(false)
                                }

                              })
                            }

                            override def errorCallback(channel1: String, error: PubnubError) {
                              assertTrue(false)
                            }

                          })
                        }

                        override def errorCallback(channel1: String, error: PubnubError) {
                          assertTrue(false)
                        }
                      })

                    }
                    override def successCallback(channel1: String, message1: Object) {
                      //println(message1)
                      testObj.test(true)
                      //pubnub.unsubscribe(channel_wildcard)
                      assertTrue(message1.equals(message_wildcard_c))
                    }
                  })
                }
                override def successCallback(channel1: String, message1: Object) {
                  //println(message1)
                  testObj.test(true)
                  //pubnub.channelGroupUnsubscribe(channel_group)
                  assertTrue(message1.equals(message_group_c))
                }
              })
            }
            override def successCallback(channel1: String, message1: Object) {
              //println(message1)
              testObj.test(true)
              //pubnub.unsubscribe(channel)
              assertTrue(message1.equals(message))
            }
          })

        }

        override def errorCallback(channel: String, error: PubnubError) {
          assert(false, error.toString)
          testObj.test(false)
        }
      })


      await atMost(TIMEOUT, MILLISECONDS) until { testObj.checksRemaining() == 0 }
    }

    it("should receive message when subscribed to channel, wildcard channel" +
      " and channel group with filtering attribute foo==bar " +
      " when message published with metadata foo:bar," +
      " and when unsubscribed from on receiving messages in callback") { pubnubTestConfig =>

      var pubnub  = pubnubTestConfig.pubnub
      var channel = "channel-" + getRandom()
      var channel_wildcard = channel + ".*"
      var channel_wildcard_c = channel + ".a"
      var channel_group = channel + "-group"
      var channel_group_c = channel + "-channel"
      var message = "message-" + getRandom(pubnubTestConfig.unicode)
      var message_group_c = message + "-group-channel"
      var message_wildcard_c = message + "-wildcard-channel"

      var testObj = new PnTest(1 + 1 + 1 + 1 + 1 + 2 + 3)

      pubnub.channelGroupAddChannel(channel_group, channel_group_c, new Callback {

        override def successCallback(channel1: String, message1: Object) {

          testObj.test(true)

          pubnub.subscribe(channel, new Callback {

            override def connectCallback(channel1: String, message1: Object) {
              testObj.test(true)

              pubnub.channelGroupSubscribe(Array(channel_group), new Callback {

                override def connectCallback(channel1: String, message1: Object) {

                  testObj.test(true)
                  pubnub.subscribe(channel_wildcard, new Callback {

                    override def connectCallback(channel1: String, message1: Object) {

                      testObj.test(true)
                      pubnub.publish(channel, message, new Callback {

                        override def successCallback(channel1: String, message1: Object) {

                          testObj.test(true)
                          pubnub.publish(channel_group_c, message_group_c, new Callback {

                            override def successCallback(channel1: String, message1: Object) {
                              testObj.test(true)
                              pubnub.publish(channel_wildcard_c, message_wildcard_c, new Callback {

                                override def successCallback(channel1: String, message1: Object) {
                                  testObj.test(true)
                                }

                                override def errorCallback(channel1: String, error: PubnubError) {
                                  assertTrue(false)
                                }

                              })
                            }

                            override def errorCallback(channel1: String, error: PubnubError) {
                              assertTrue(false)
                            }

                          })
                        }

                        override def errorCallback(channel1: String, error: PubnubError) {
                          assertTrue(false)
                        }
                      })

                    }
                    override def successCallback(channel1: String, message1: Object) {
                      //println(message1)
                      testObj.test(true)
                      pubnub.unsubscribe(channel_wildcard)
                      assertTrue(message1.equals(message_wildcard_c))
                    }
                  })
                }
                override def successCallback(channel1: String, message1: Object) {
                  //println(message1)
                  testObj.test(true)
                  pubnub.channelGroupUnsubscribe(channel_group)
                  assertTrue(message1.equals(message_group_c))
                }
              })
            }
            override def successCallback(channel1: String, message1: Object) {
              //println(message1)
              testObj.test(true)
              pubnub.unsubscribe(channel)
              assertTrue(message1.equals(message))
            }
          })

        }

        override def errorCallback(channel: String, error: PubnubError) {
          assert(false)
          testObj.test(false)
        }
      })


      await atMost(TIMEOUT, MILLISECONDS) until { testObj.checksRemaining() == 0 }
    }



    ignore("should be able to receive message successfully when subscribed to channel, wildcard channel" +
      " and channel group with no filtering attribute" +
      " when message published with no metadata") { pubnubTestConfig =>

      var pubnub  = pubnubTestConfig.pubnub
      var channel = "channel-" + getRandom()
      var channel_wildcard = channel + ".*"
      var channel_wildcard_c = channel + ".a"
      var channel_group = channel + "-group"
      var channel_group_c = channel + "-channel"
      var message = "message-" + getRandom(pubnubTestConfig.unicode)
      var message_group_c = message + "-group-channel"
      var message_wildcard_c = message + "-wildcard-channel"

      var testObj = new PnTest(1 + 1 + 1 + 1 + 1 + 2 + 3)

      pubnub.channelGroupAddChannel(channel_group, channel_group_c, new Callback {

        override def successCallback(channel1: String, message1: Object) {

          testObj.test(true)

          pubnub.subscribe(channel, new Callback {

            override def connectCallback(channel1: String, message1: Object) {
              testObj.test(true)

              pubnub.channelGroupSubscribe(Array(channel_group), new Callback {

                override def connectCallback(channel1: String, message1: Object) {

                  testObj.test(true)
                  pubnub.subscribe(channel_wildcard, new Callback {

                    override def connectCallback(channel1: String, message1: Object) {

                      testObj.test(true)
                      pubnub.publish(channel, message, new Callback {

                        override def successCallback(channel1: String, message1: Object) {

                          testObj.test(true)
                          pubnub.publish(channel_group_c, message_group_c, new Callback {

                            override def successCallback(channel1: String, message1: Object) {
                              testObj.test(true)
                              pubnub.publish(channel_wildcard_c, message_wildcard_c, new Callback {

                                override def successCallback(channel1: String, message1: Object) {
                                  testObj.test(true)
                                }

                                override def errorCallback(channel1: String, error: PubnubError) {
                                  assertTrue(false)
                                }

                              })
                            }

                            override def errorCallback(channel1: String, error: PubnubError) {
                              assertTrue(false)
                            }

                          })
                        }

                        override def errorCallback(channel1: String, error: PubnubError) {
                          assertTrue(false)
                        }
                      })

                    }
                    override def successCallback(channel1: String, message1: Object) {
                      //println(message1)
                      testObj.test(true)
                      //pubnub.unsubscribe(channel_wildcard)
                      assertTrue(message1.equals(message_wildcard_c))
                    }
                  })
                }
                override def successCallback(channel1: String, message1: Object) {
                  //println(message1)
                  testObj.test(true)
                  //pubnub.channelGroupUnsubscribe(channel_group)
                  assertTrue(message1.equals(message_group_c))
                }
              })
            }
            override def successCallback(channel1: String, message1: Object) {
              //println(message1)
              testObj.test(true)
              //pubnub.unsubscribe(channel)
              assertTrue(message1.equals(message))
            }
          })

        }

        override def errorCallback(channel: String, error: PubnubError) {
          assert(false)
          testObj.test(false)
        }
      })


      await atMost(TIMEOUT, MILLISECONDS) until { testObj.checksRemaining() == 0 }

    }

    ignore("should be able to receive message successfully when subscribed to channel, wildcard channel" +
      " and channel group with no filtering attribute" +
      " when message published with no metadata," +
      " when unsubscribed after receiving message") { pubnubTestConfig =>

      var pubnub  = pubnubTestConfig.pubnub
      var channel = "channel-" + getRandom()
      var channel_wildcard = channel + ".*"
      var channel_wildcard_c = channel + ".a"
      var channel_group = channel + "-group"
      var channel_group_c = channel + "-channel"
      var message = "message-" + getRandom(pubnubTestConfig.unicode)
      var message_group_c = message + "-group-channel"
      var message_wildcard_c = message + "-wildcard-channel"

      var testObj = new PnTest(1 + 1 + 1 + 1 + 1 + 2 + 3)

      pubnub.channelGroupAddChannel(channel_group, channel_group_c, new Callback {

        override def successCallback(channel1: String, message1: Object) {

          testObj.test(true)

          pubnub.subscribe(channel, new Callback {

            override def connectCallback(channel1: String, message1: Object) {
              testObj.test(true)

              pubnub.channelGroupSubscribe(Array(channel_group), new Callback {

                override def connectCallback(channel1: String, message1: Object) {

                  testObj.test(true)
                  pubnub.subscribe(channel_wildcard, new Callback {

                    override def connectCallback(channel1: String, message1: Object) {

                      testObj.test(true)
                      pubnub.publish(channel, message, new Callback {

                        override def successCallback(channel1: String, message1: Object) {

                          testObj.test(true)
                          pubnub.publish(channel_group_c, message_group_c, new Callback {

                            override def successCallback(channel1: String, message1: Object) {
                              testObj.test(true)
                              pubnub.publish(channel_wildcard_c, message_wildcard_c, new Callback {

                                override def successCallback(channel1: String, message1: Object) {
                                  testObj.test(true)
                                }

                                override def errorCallback(channel1: String, error: PubnubError) {
                                  assertTrue(false)
                                }

                              })
                            }

                            override def errorCallback(channel1: String, error: PubnubError) {
                              assertTrue(false)
                            }

                          })
                        }

                        override def errorCallback(channel1: String, error: PubnubError) {
                          assertTrue(false)
                        }
                      })

                    }
                    override def successCallback(channel1: String, message1: Object) {
                      //println(message1)
                      testObj.test(true)
                      pubnub.unsubscribe(channel_wildcard)
                      assertTrue(message1.equals(message_wildcard_c))
                    }
                  })
                }
                override def successCallback(channel1: String, message1: Object) {
                  //println(message1)
                  testObj.test(true)
                  pubnub.channelGroupUnsubscribe(channel_group)
                  assertTrue(message1.equals(message_group_c))
                }
              })
            }
            override def successCallback(channel1: String, message1: Object) {
              //println(message1)
              testObj.test(true)
              pubnub.unsubscribe(channel)
              assertTrue(message1.equals(message))
            }
          })

        }

        override def errorCallback(channel: String, error: PubnubError) {
          assert(false)
          testObj.test(false)
        }
      })


      await atMost(TIMEOUT, MILLISECONDS) until { testObj.checksRemaining() == 0 }

    }


    ignore("should be able to receive message successfully when subscribed to channel, wildcard channel" +
      " and channel group with no filtering attribute" +
      " when message published with metadata foo:bar") { pubnubTestConfig =>

      var pubnub  = pubnubTestConfig.pubnub
      var channel = "channel-" + getRandom()
      var channel_wildcard = channel + ".*"
      var channel_wildcard_c = channel + ".a"
      var channel_group = channel + "-group"
      var channel_group_c = channel + "-channel"
      var message = "message-" + getRandom(pubnubTestConfig.unicode)
      var message_group_c = message + "-group-channel"
      var message_wildcard_c = message + "-wildcard-channel"

      var testObj = new PnTest(1 + 1 + 1 + 1 + 1 + 2 + 3)

      pubnub.channelGroupAddChannel(channel_group, channel_group_c, new Callback {

        override def successCallback(channel1: String, message1: Object) {

          testObj.test(true)

          pubnub.subscribe(channel, new Callback {

            override def connectCallback(channel1: String, message1: Object) {
              testObj.test(true)

              pubnub.channelGroupSubscribe(Array(channel_group), new Callback {

                override def connectCallback(channel1: String, message1: Object) {

                  testObj.test(true)
                  pubnub.subscribe(channel_wildcard, new Callback {

                    override def connectCallback(channel1: String, message1: Object) {

                      testObj.test(true)
                      pubnub.publish(channel, message, new Callback {

                        override def successCallback(channel1: String, message1: Object) {

                          testObj.test(true)
                          pubnub.publish(channel_group_c, message_group_c, new Callback {

                            override def successCallback(channel1: String, message1: Object) {
                              testObj.test(true)
                              pubnub.publish(channel_wildcard_c, message_wildcard_c, new Callback {

                                override def successCallback(channel1: String, message1: Object) {
                                  testObj.test(true)
                                }

                                override def errorCallback(channel1: String, error: PubnubError) {
                                  assertTrue(false)
                                }

                              })
                            }

                            override def errorCallback(channel1: String, error: PubnubError) {
                              assertTrue(false)
                            }

                          })
                        }

                        override def errorCallback(channel1: String, error: PubnubError) {
                          assertTrue(false)
                        }
                      })

                    }
                    override def successCallback(channel1: String, message1: Object) {
                      //println(message1)
                      testObj.test(true)
                      //pubnub.unsubscribe(channel_wildcard)
                      assertTrue(message1.equals(message_wildcard_c))
                    }
                  })
                }
                override def successCallback(channel1: String, message1: Object) {
                  //println(message1)
                  testObj.test(true)
                  //pubnub.channelGroupUnsubscribe(channel_group)
                  assertTrue(message1.equals(message_group_c))
                }
              })
            }
            override def successCallback(channel1: String, message1: Object) {
              //println(message1)
              testObj.test(true)
              //pubnub.unsubscribe(channel)
              assertTrue(message1.equals(message))
            }
          })

        }

        override def errorCallback(channel: String, error: PubnubError) {
          assert(false)
          testObj.test(false)
        }
      })


      await atMost(TIMEOUT, MILLISECONDS) until { testObj.checksRemaining() == 0 }

    }

    ignore("should be able to receive message successfully when subscribed to channel, wildcard channel" +
      " and channel group with no filtering attribute" +
      " when message published with metadata foo:bar," +
      " when unsubscribed after receiving messages") { pubnubTestConfig =>

      var pubnub  = pubnubTestConfig.pubnub
      var channel = "channel-" + getRandom()
      var channel_wildcard = channel + ".*"
      var channel_wildcard_c = channel + ".a"
      var channel_group = channel + "-group"
      var channel_group_c = channel + "-channel"
      var message = "message-" + getRandom(pubnubTestConfig.unicode)
      var message_group_c = message + "-group-channel"
      var message_wildcard_c = message + "-wildcard-channel"

      var testObj = new PnTest(1 + 1 + 1 + 1 + 1 + 2 + 3)

      pubnub.channelGroupAddChannel(channel_group, channel_group_c, new Callback {

        override def successCallback(channel1: String, message1: Object) {

          testObj.test(true)

          pubnub.subscribe(channel, new Callback {

            override def connectCallback(channel1: String, message1: Object) {
              testObj.test(true)

              pubnub.channelGroupSubscribe(Array(channel_group), new Callback {

                override def connectCallback(channel1: String, message1: Object) {

                  testObj.test(true)
                  pubnub.subscribe(channel_wildcard, new Callback {

                    override def connectCallback(channel1: String, message1: Object) {

                      testObj.test(true)
                      pubnub.publish(channel, message, new Callback {

                        override def successCallback(channel1: String, message1: Object) {

                          testObj.test(true)
                          pubnub.publish(channel_group_c, message_group_c, new Callback {

                            override def successCallback(channel1: String, message1: Object) {
                              testObj.test(true)
                              pubnub.publish(channel_wildcard_c, message_wildcard_c, new Callback {

                                override def successCallback(channel1: String, message1: Object) {
                                  testObj.test(true)
                                }

                                override def errorCallback(channel1: String, error: PubnubError) {
                                  assertTrue(false)
                                }

                              })
                            }

                            override def errorCallback(channel1: String, error: PubnubError) {
                              assertTrue(false)
                            }

                          })
                        }

                        override def errorCallback(channel1: String, error: PubnubError) {
                          assertTrue(false)
                        }
                      })

                    }
                    override def successCallback(channel1: String, message1: Object) {
                      //println(message1)
                      testObj.test(true)
                      pubnub.unsubscribe(channel_wildcard)
                      assertTrue(message1.equals(message_wildcard_c))
                    }
                  })
                }
                override def successCallback(channel1: String, message1: Object) {
                  //println(message1)
                  testObj.test(true)
                  pubnub.channelGroupUnsubscribe(channel_group)
                  assertTrue(message1.equals(message_group_c))
                }
              })
            }
            override def successCallback(channel1: String, message1: Object) {
              //println(message1)
              testObj.test(true)
              pubnub.unsubscribe(channel)
              assertTrue(message1.equals(message))
            }
          })

        }

        override def errorCallback(channel: String, error: PubnubError) {
          assert(false)
          testObj.test(false)
        }
      })


      await atMost(TIMEOUT, MILLISECONDS) until { testObj.checksRemaining() == 0 }

    }


    ignore("should not receive message when subscribed to channel, wildcard channel" +
      " and channel group  with filtering attribute foo==bar" +
      " when message published with no metadata") { pubnubTestConfig =>

      var pubnub  = pubnubTestConfig.pubnub
      var channel = "channel-" + getRandom()
      var channel_wildcard = channel + ".*"
      var channel_wildcard_c = channel + ".a"
      var channel_group = channel + "-group"
      var channel_group_c = channel + "-channel"
      var message = "message-" + getRandom(pubnubTestConfig.unicode)
      var message_group_c = message + "-group-channel"
      var message_wildcard_c = message + "-wildcard-channel"

      var testObj = new PnTest(1 + 1 + 1 + 1 + 1 + 2 + 3)

      pubnub.channelGroupAddChannel(channel_group, channel_group_c, new Callback {

        override def successCallback(channel1: String, message1: Object) {

          testObj.test(true)

          pubnub.subscribe(channel, new Callback {

            override def connectCallback(channel1: String, message1: Object) {
              testObj.test(true)

              pubnub.channelGroupSubscribe(Array(channel_group), new Callback {

                override def connectCallback(channel1: String, message1: Object) {

                  testObj.test(true)
                  pubnub.subscribe(channel_wildcard, new Callback {

                    override def connectCallback(channel1: String, message1: Object) {

                      testObj.test(true)
                      pubnub.publish(channel, message, new Callback {

                        override def successCallback(channel1: String, message1: Object) {

                          testObj.test(true)
                          pubnub.publish(channel_group_c, message_group_c, new Callback {

                            override def successCallback(channel1: String, message1: Object) {
                              testObj.test(true)
                              pubnub.publish(channel_wildcard_c, message_wildcard_c, new Callback {

                                override def successCallback(channel1: String, message1: Object) {
                                  testObj.test(true)
                                }

                                override def errorCallback(channel1: String, error: PubnubError) {
                                  assertTrue(false)
                                }

                              })
                            }

                            override def errorCallback(channel1: String, error: PubnubError) {
                              assertTrue(false)
                            }

                          })
                        }

                        override def errorCallback(channel1: String, error: PubnubError) {
                          assertTrue(false)
                        }
                      })

                    }
                    override def successCallback(channel1: String, message1: Object) {
                      //println(message1)
                      testObj.test(true)
                      //pubnub.unsubscribe(channel_wildcard)
                      assertTrue(message1.equals(message_wildcard_c))
                    }
                  })
                }
                override def successCallback(channel1: String, message1: Object) {
                  //println(message1)
                  testObj.test(true)
                  //pubnub.channelGroupUnsubscribe(channel_group)
                  assertTrue(message1.equals(message_group_c))
                }
              })
            }
            override def successCallback(channel1: String, message1: Object) {
              //println(message1)
              testObj.test(true)
              //pubnub.unsubscribe(channel)
              assertTrue(message1.equals(message))
            }
          })

        }

        override def errorCallback(channel: String, error: PubnubError) {
          assert(false)
          testObj.test(false)
        }
      })


      await atMost(TIMEOUT, MILLISECONDS) until { testObj.checksRemaining() == 0 }



    }
    ignore("should not receive message when subscribed to channel, wildcard channel" +
      " and channel group with filtering attribute a==b" +
      " when message published with metadata foo:bar") { pubnubTestConfig =>

      var pubnub  = pubnubTestConfig.pubnub
      var channel = "channel-" + getRandom()
      var channel_wildcard = channel + ".*"
      var channel_wildcard_c = channel + ".a"
      var channel_group = channel + "-group"
      var channel_group_c = channel + "-channel"
      var message = "message-" + getRandom(pubnubTestConfig.unicode)
      var message_group_c = message + "-group-channel"
      var message_wildcard_c = message + "-wildcard-channel"

      var testObj = new PnTest(1 + 1 + 1 + 1 + 1 + 2 + 3)

      pubnub.channelGroupAddChannel(channel_group, channel_group_c, new Callback {

        override def successCallback(channel1: String, message1: Object) {

          testObj.test(true)

          pubnub.subscribe(channel, new Callback {

            override def connectCallback(channel1: String, message1: Object) {
              testObj.test(true)

              pubnub.channelGroupSubscribe(Array(channel_group), new Callback {

                override def connectCallback(channel1: String, message1: Object) {

                  testObj.test(true)
                  pubnub.subscribe(channel_wildcard, new Callback {

                    override def connectCallback(channel1: String, message1: Object) {

                      testObj.test(true)
                      pubnub.publish(channel, message, new Callback {

                        override def successCallback(channel1: String, message1: Object) {

                          testObj.test(true)
                          pubnub.publish(channel_group_c, message_group_c, new Callback {

                            override def successCallback(channel1: String, message1: Object) {
                              testObj.test(true)
                              pubnub.publish(channel_wildcard_c, message_wildcard_c, new Callback {

                                override def successCallback(channel1: String, message1: Object) {
                                  testObj.test(true)
                                }

                                override def errorCallback(channel1: String, error: PubnubError) {
                                  assertTrue(false)
                                }

                              })
                            }

                            override def errorCallback(channel1: String, error: PubnubError) {
                              assertTrue(false)
                            }

                          })
                        }

                        override def errorCallback(channel1: String, error: PubnubError) {
                          assertTrue(false)
                        }
                      })

                    }
                    override def successCallback(channel1: String, message1: Object) {
                      //println(message1)
                      testObj.test(true)
                      //pubnub.unsubscribe(channel_wildcard)
                      assertTrue(message1.equals(message_wildcard_c))
                    }
                  })
                }
                override def successCallback(channel1: String, message1: Object) {
                  //println(message1)
                  testObj.test(true)
                  //pubnub.channelGroupUnsubscribe(channel_group)
                  assertTrue(message1.equals(message_group_c))
                }
              })
            }
            override def successCallback(channel1: String, message1: Object) {
              //println(message1)
              testObj.test(true)
              //pubnub.unsubscribe(channel)
              assertTrue(message1.equals(message))
            }
          })

        }

        override def errorCallback(channel: String, error: PubnubError) {
          assert(false)
          testObj.test(false)
        }
      })


      await atMost(TIMEOUT, MILLISECONDS) until { testObj.checksRemaining() == 0 }



    }
    ignore("should not receive message when subscribed to channel, wildcard channel" +
      " and channel group with filtering attribute foo==b" +
      " when message published with metadata foo:bar") { pubnubTestConfig =>

      var pubnub  = pubnubTestConfig.pubnub
      var channel = "channel-" + getRandom()
      var channel_wildcard = channel + ".*"
      var channel_wildcard_c = channel + ".a"
      var channel_group = channel + "-group"
      var channel_group_c = channel + "-channel"
      var message = "message-" + getRandom(pubnubTestConfig.unicode)
      var message_group_c = message + "-group-channel"
      var message_wildcard_c = message + "-wildcard-channel"

      var testObj = new PnTest(1 + 1 + 1 + 1 + 1 + 2 + 3)

      pubnub.channelGroupAddChannel(channel_group, channel_group_c, new Callback {

        override def successCallback(channel1: String, message1: Object) {

          testObj.test(true)

          pubnub.subscribe(channel, new Callback {

            override def connectCallback(channel1: String, message1: Object) {
              testObj.test(true)

              pubnub.channelGroupSubscribe(Array(channel_group), new Callback {

                override def connectCallback(channel1: String, message1: Object) {

                  testObj.test(true)
                  pubnub.subscribe(channel_wildcard, new Callback {

                    override def connectCallback(channel1: String, message1: Object) {

                      testObj.test(true)
                      pubnub.publish(channel, message, new Callback {

                        override def successCallback(channel1: String, message1: Object) {

                          testObj.test(true)
                          pubnub.publish(channel_group_c, message_group_c, new Callback {

                            override def successCallback(channel1: String, message1: Object) {
                              testObj.test(true)
                              pubnub.publish(channel_wildcard_c, message_wildcard_c, new Callback {

                                override def successCallback(channel1: String, message1: Object) {
                                  testObj.test(true)
                                }

                                override def errorCallback(channel1: String, error: PubnubError) {
                                  assertTrue(false)
                                }

                              })
                            }

                            override def errorCallback(channel1: String, error: PubnubError) {
                              assertTrue(false)
                            }

                          })
                        }

                        override def errorCallback(channel1: String, error: PubnubError) {
                          assertTrue(false)
                        }
                      })

                    }
                    override def successCallback(channel1: String, message1: Object) {
                      //println(message1)
                      testObj.test(true)
                      //pubnub.unsubscribe(channel_wildcard)
                      assertTrue(message1.equals(message_wildcard_c))
                    }
                  })
                }
                override def successCallback(channel1: String, message1: Object) {
                  //println(message1)
                  testObj.test(true)
                  //pubnub.channelGroupUnsubscribe(channel_group)
                  assertTrue(message1.equals(message_group_c))
                }
              })
            }
            override def successCallback(channel1: String, message1: Object) {
              //println(message1)
              testObj.test(true)
              //pubnub.unsubscribe(channel)
              assertTrue(message1.equals(message))
            }
          })

        }

        override def errorCallback(channel: String, error: PubnubError) {
          assert(false)
          testObj.test(false)
        }
      })


      await atMost(TIMEOUT, MILLISECONDS) until { testObj.checksRemaining() == 0 }



    }
    ignore("should not receive message when subscribed to channel, wildcard channel " +
      "and channel group with filtering attribute bar==foo" +
      " when message published with metadata foo:bar") { pubnubTestConfig =>

      var pubnub  = pubnubTestConfig.pubnub
      var channel = "channel-" + getRandom()
      var channel_wildcard = channel + ".*"
      var channel_wildcard_c = channel + ".a"
      var channel_group = channel + "-group"
      var channel_group_c = channel + "-channel"
      var message = "message-" + getRandom(pubnubTestConfig.unicode)
      var message_group_c = message + "-group-channel"
      var message_wildcard_c = message + "-wildcard-channel"

      var testObj = new PnTest(1 + 1 + 1 + 1 + 1 + 2 + 3)

      pubnub.channelGroupAddChannel(channel_group, channel_group_c, new Callback {

        override def successCallback(channel1: String, message1: Object) {

          testObj.test(true)

          pubnub.subscribe(channel, new Callback {

            override def connectCallback(channel1: String, message1: Object) {
              testObj.test(true)

              pubnub.channelGroupSubscribe(Array(channel_group), new Callback {

                override def connectCallback(channel1: String, message1: Object) {

                  testObj.test(true)
                  pubnub.subscribe(channel_wildcard, new Callback {

                    override def connectCallback(channel1: String, message1: Object) {

                      testObj.test(true)
                      pubnub.publish(channel, message, new Callback {

                        override def successCallback(channel1: String, message1: Object) {

                          testObj.test(true)
                          pubnub.publish(channel_group_c, message_group_c, new Callback {

                            override def successCallback(channel1: String, message1: Object) {
                              testObj.test(true)
                              pubnub.publish(channel_wildcard_c, message_wildcard_c, new Callback {

                                override def successCallback(channel1: String, message1: Object) {
                                  testObj.test(true)
                                }

                                override def errorCallback(channel1: String, error: PubnubError) {
                                  assertTrue(false)
                                }

                              })
                            }

                            override def errorCallback(channel1: String, error: PubnubError) {
                              assertTrue(false)
                            }

                          })
                        }

                        override def errorCallback(channel1: String, error: PubnubError) {
                          assertTrue(false)
                        }
                      })

                    }
                    override def successCallback(channel1: String, message1: Object) {
                      //println(message1)
                      testObj.test(true)
                      //pubnub.unsubscribe(channel_wildcard)
                      assertTrue(message1.equals(message_wildcard_c))
                    }
                  })
                }
                override def successCallback(channel1: String, message1: Object) {
                  //println(message1)
                  testObj.test(true)
                  //pubnub.channelGroupUnsubscribe(channel_group)
                  assertTrue(message1.equals(message_group_c))
                }
              })
            }
            override def successCallback(channel1: String, message1: Object) {
              //println(message1)
              testObj.test(true)
              //pubnub.unsubscribe(channel)
              assertTrue(message1.equals(message))
            }
          })

        }

        override def errorCallback(channel: String, error: PubnubError) {
          assert(false)
          testObj.test(false)
        }
      })


      await atMost(TIMEOUT, MILLISECONDS) until { testObj.checksRemaining() == 0 }



    }


    /**************/
  }
}
