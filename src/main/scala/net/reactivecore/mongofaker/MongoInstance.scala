package net.reactivecore.mongofaker

import java.io.File
import org.slf4j.LoggerFactory
import java.net.{UnknownHostException, InetAddress}

/**
 * A instance of the MongoDB. The instance is usually running until stopped.
 */
class MongoInstance private[mongofaker] (dbDir : DatabaseDir, val address : String, val port : Int, executable : File, extraArguments : List[String]) {
  if (!executable.exists()) {
    throw new MongoFakerException(s"Mongod executable ${executable} does not exist")
  }

  private var stopped = false

  private val args = List (
    "--dbpath", dbDir.directory.getAbsolutePath,
    "--port", port.toString,
    "--bind_ip", address) ++ extraArguments


  val inetAddress = try {
    InetAddress.getByName(address)
  } catch {
    case e : UnknownHostException => throw new MongoFakerException (s"Unknown host ${address}")
  }
  val process = new ServerProcess(executable, args, inetAddress, port)

  /**
   * Stops the mongod process
   */
  def stop () = {
    require(stopped == false, "Already stopped")
    try {
      process.stop()
    } finally {
      dbDir.uninit
      stopped = true
    }
  }

  /**
   * Returns the database directory
    *
    * @return
    */
  def databaseDir = dbDir.directory
}

/**
 * Starts up MongoInstances.
 */
object MongoInstance {
  val mongodName = "mongod"

  val logger = LoggerFactory.getLogger(this.getClass.getSimpleName)

  /**
   * Start a new MongoDB instance.
   * @param configuration configuration
   * @return mongo DB instance.
   */
  def start(configuration : MongoConfiguration) : MongoInstance = {
    val dbDir : DatabaseDir = configuration.dataDir.map {
      s => new UnmanagedDatabaseDir(new File(s))
    } getOrElse {
      new ManagedDatabaseDir()
    }

    val port : Int = configuration.port.getOrElse { findFreePort() }
    val address : String = configuration.bindIp.getOrElse { "127.0.0.1" }

    val executable = configuration.executable.map {
      x => new File(x)
    } getOrElse {
      findMongodExecutable()
    }


    new MongoInstance(dbDir, address, port, executable, configuration.extraArguments)
  }


  /**
   * Returns a free local port.
   */
  private[reactivecore] def findFreePort() : Int = {
    import java.net.ServerSocket
    import java.io.IOException
    var port : Int = -1
    try {
      var socket : ServerSocket  = new ServerSocket(0);
      port = socket.getLocalPort
      socket.close()
      port
    } catch {
      case e : IOException => throw new MongoFakerException("Could not find a free port")
    }
  }

  /**
   * Find the mongod executable.
   * This works in Unix only.
   * @return
   */
  private[reactivecore] def findMongodExecutable() : File = {
    val path = System.getenv("PATH")
    if (path == null) throw new MongoFakerException(s"PATH is empty, cannot find '$mongodName'")
    val elements = path.split(":")
    elements.foreach { e =>
      val candidate = new File (e, mongodName)
      if (candidate.exists()) return candidate
    }
    throw new MongoFakerException(s"Could not find '$mongodName' executable")
  }

}
