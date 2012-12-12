import java.io.File
import java.sql.Connection
import java.util.Date
import scala._
import scala.io._
import scalikejdbc.ConnectionPool
import anorm._
import anorm.SqlParser._
import anorm.SqlParser._

object Main extends App {

  case class Flight(from: String, to: String, departure: Date, arrival: Date, flightNo: String)
  case class FlightHead(from: String, to: String)

  def processFile(file: File):List[Flight] = {
    println(file)
    val buf =  Source.fromFile(file)
    val lines = buf.getLines().toList
    buf.close()
    val list:List[Flight] = lines  match {
      case head :: tail => val fh = proccessHead(head); tail match {
        case head2 :: tail2 => tail2.map(processRow(fh,_)).flatten
      }
    }
    list
  }

  def proccessHead(s:String) : FlightHead = {
    val arr = s.split("[ \t]+")
    println(arr.mkString(" ^ "))
    FlightHead(arr(1),arr(3))
  }

  def processRow(head:FlightHead,s: String): List[Flight] = {
    //println(s)
    val arr = s.split(" ")
    List(Flight(head.from,head.to,null,null,arr(10)))
  }


  new File("in").listFiles filter (_.getName().equals("Berlin-Kologne2.txt")) map processFile  foreach println

}
