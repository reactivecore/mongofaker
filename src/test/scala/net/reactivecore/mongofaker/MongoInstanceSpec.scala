package net.reactivecore.mongofaker

import org.specs2.mutable._
import java.net.{Socket, InetAddress, ServerSocket}
import java.io.IOException

/**
 * Test MongoInstance.
 */
class MongoInstanceSpec extends Specification {

  // We use the slf4j-simple during the testbuild.
  System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug")

  "The search for Mongod" should {
    "find something" in {
      val executable = MongoInstance.findMongodExecutable()
      executable.exists() === true
      executable.canExecute === true
    }
  }

  "The search for a free port" should {
    "find something usable" in {
      val port = MongoInstance.findFreePort()
      port must beGreaterThan(0)
      port must beLessThan(655356)

      {
        // Shall not throw
        var socket : ServerSocket  = new ServerSocket(port);
        socket.close()
      } must not (throwA[IOException])
    }
  }

  "Starting up an instance" should {
    "work" in {
      {
        val conf = MongoConfiguration()
        val instance = MongoInstance.start(conf)

        {
          val s = new Socket("127.0.0.1", instance.port)
          s.close()
        } must not (throwA[IOException])

        instance.stop()
      } must not (throwA[MongoFakerException])

    }

    "work on slow configurations" in {
      {
        // Without --nojournal startup time is ~10 seconds on 64bit Linux.
        val conf = MongoConfiguration().copy(extraArguments = List())
        val instance = MongoInstance.start(conf)
      } must not (throwA[MongoFakerException])
    }
  }
}
