package observatory

import com.sksamuel.scrimage.nio.ImageWriter
import org.scalatest.funsuite.AnyFunSuite

//noinspection TypeAnnotation
abstract class UnitSuite extends AnyFunSuite {

  implicit val writer: ImageWriter = ImageWriter.default

  val year = 2015
  val stationsFile = "/test_stations.csv"
  val temperaturesFile = s"/test_$year.csv"

  lazy val stationTemperatures = Extraction.locateTemperatures(year, stationsFile, temperaturesFile)
  lazy val stationYearlyTemperatures = Extraction.locationYearlyAverageRecords(stationTemperatures)

  lazy val temperatureColors = Set(
    (60.0, Color(255, 255, 255)),
    (32.0, Color(255, 0, 0)),
    (12.0, Color(255, 255, 0)),
    (0.0, Color(0, 255, 255)),
    (-15.0, Color(0, 0, 255)),
    (-27.0, Color(255, 0, 255)),
    (-50.0, Color(33, 0, 107)),
    (-60.0, Color(0, 0, 0))
  )

}
