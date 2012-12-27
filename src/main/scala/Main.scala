import java.io.File
import org.joda.time.format.DateTimeFormatter
import org.scala_tools.time.Imports._
import java.util.Date
import org.joda.time.DateTime
import scala._
import scala.io._
import scalikejdbc.ConnectionPool
import anorm._
import anorm.SqlParser._
import anorm.SqlParser._

object Main extends App {

  case class Flight(from: String, to: String, departure: DateTime, /*arrival: Date,*/ flightNo: String)

  case class FlightHead(from: String, to: String)

  def processFile(file: File): List[Flight] = {
    println(file)
    val buf = Source.fromFile(file)
    val lines = buf.getLines().toList
    buf.close()
    val list: List[Flight] = lines match {
      case head :: tail => val fh = proccessHead(head);
      tail match {
        case head2 :: tail2 => tail2.map(processRow(fh, _)).flatten
      }
    }
    list
  }

  def proccessHead(s: String): FlightHead = {
    val arr = s.split("[ \t]+")
    println(arr.mkString(" ^ "))
    FlightHead(arr(1), arr(3))
  }

  def processRow(head: FlightHead, s: String): List[Flight] = {
    val arr = s.split("[ \t]+").toList
    val weekMatrix = getWeekMatrix(arr)

    val dateFormat = DateTimeFormat.forPattern("hh:mm dd.MM.yyyy")

    val from = DateTime.parse(arr(7) + " " + arr(11),dateFormat )
    val to = DateTime.parse(arr(7) + " " + arr(13), DateTimeFormat.forPattern("hh:mm dd.MM.yyyy"))
    dateRange(from, to, 1.days).filter(validateDayAgainstWeekMatrix(_, weekMatrix)).map(Flight(head.from, head.to, _,  arr(9) + arr(10)))
  }

  def dateRange(from: DateTime, to: DateTime, step: Period): List[DateTime] =
    Iterator.iterate(from)(_ + step).takeWhile(_ <= to).toList

  def getWeekMatrix(arr: Seq[String]) =
    arr.take(7).map(_ match {
      case "?" => true
      case "-" => false
    })

  def validateDayAgainstWeekMatrix(day: DateTime, weekMatrix: Seq[Boolean]) =
    weekMatrix(day.dayOfWeek().get() - 1)

  new File("in").listFiles filter (_.getName().equals("Berlin-Kologne2.txt")) map processFile foreach println

}
