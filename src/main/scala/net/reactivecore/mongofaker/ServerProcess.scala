package net.reactivecore.mongofaker

import java.net.{Socket, InetAddress}
import java.io.{File, InputStreamReader, BufferedReader, IOException}
import java.util.concurrent.TimeoutException
import org.slf4j.LoggerFactory
import scala.collection.JavaConversions._
import scala.language.postfixOps

/**
 * Encapsulates a Server process with Logging into sl4f.
 */
private[reactivecore] class ServerProcess(executable : File, args : List[String], address : InetAddress, port : Int) {
  import ServerProcess._
  if (!executable.exists()) {
    throw new MongoFakerException(s"Mongod executable ${executable} does not exist")
  }

  private var stopped = false

  private val cmdArray = List (executable.getAbsolutePath) ++ args

  private val processBuilder = new ProcessBuilder(cmdArray)
  private val executableShortName = executable.getName
  processBuilder.redirectErrorStream(true)



  logger.info(s"Spawning new ${executableShortName} processs")
  val t0 = System.currentTimeMillis()
  private val process = processBuilder.start()
  private val pid = getPidOfProcess(process).getOrElse ("unknown")

  /* Stupid naming, it's not the process input stream but its stdout/stderr output stream. */
  private val inputStream = new BufferedReader(new InputStreamReader(process.getInputStream))
  private val loggerTag = executableShortName + "-" + pid
  private val processLogger = LoggerFactory.getLogger(loggerTag)

  val thread = new Thread {

    override def run() = {
      try {
        while (true) {
          val line = inputStream.readLine()
          if (processLogger.isDebugEnabled) {
            processLogger.debug("{}", line)
          }
        }
      } catch {
        case e : IOException =>
          logger.info(s"Stopped instance ${executableShortName}-${pid}")
      }
    }
  }.start()

  try {
    waitForPort(address, port)
  } catch {
    case e: TimeoutException => {
      processLogger.info("Timeout on waiting for process to listen")
      stop()
      throw new MongoFakerException("Timeout on waiting for process to listen")
    }
  }

  val t1 = System.currentTimeMillis()
  logger.info(s"Process ${executableShortName}-${pid} started up and listening, took ${t1-t0}ms")

  /**
   * Stops the mongod process
   */
  def stop () = {
    require(stopped == false, "Already stopped")
    try {
      process.destroy()
    } finally {
      stopped = true
    }
  }
}

private[reactivecore] object ServerProcess {
  private val logger = LoggerFactory.getLogger(getClass())
  /**
   * Tries to figure out the PID of a process. Since there is no
   * valid way in java this method can throw.
   * @param process
   * @return PID or None if not found
   */
  @throws[NoSuchFieldError]
  def getPidOfProcess(process : Process) : Option[Int] = {
    // Source: http://www.golesny.de/p/code/javagetpid
    val clazz = process.getClass
    if(clazz.getName().equals("java.lang.UNIXProcess")) {
      /* get the PID on unix/linux systems */
      try {
        val f = process.getClass().getDeclaredField("pid");
        f.setAccessible(true)
        Some(f.getInt(process))
      } catch {
        case e : Throwable => None
      }
    } else {
      None
    }
  }

  /**
   * Check if a given address/port responds.
   * @param address IP Address
   * @param port port
   * @return true if responding
   */
  def checkPortExistance(address : InetAddress, port : Int) : Boolean = {
    try {
      val socket = new Socket(address, port)
      socket.close()
      return true
    } catch {
      case e : IOException => {
        return false
      }
    }
  }

  import scala.concurrent.duration._

  /**
   * Waits until a address/port responds
   * @param address address
   * @param port port
   * @param timeout timeout how long to wait
   * @param step step on which to wait until next try.
   * @throws TimeoutException on timeout.
   */
  @throws[TimeoutException]
  def waitForPort(address : InetAddress, port : Int, timeout : FiniteDuration = 30 seconds, step : FiniteDuration = 50 milliseconds) : Unit = {
    val timeoutPoint = timeout.fromNow
    while (!timeoutPoint.isOverdue) {
      if (checkPortExistance(address, port)) {
        // logger.info("Port {} is listening", port)
        return
      }
      // logger.info("Port {} is not listening", port)
      Thread.sleep(step.toMillis)
    }
    // logger.info("Port {} is never listening", port)
    throw new TimeoutException(s"Timeout while waiting for port ${port}")
  }

}
