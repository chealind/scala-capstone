package observatory

import observatory.spark.SparkApp

//noinspection TypeAnnotation
trait ExtractionTest extends UnitSuite with SparkApp {

  lazy val stations = Extraction.rawStations(stationsFile).collect()
  lazy val temperatures = Extraction.rawTemperatures(year, temperaturesFile).collect()

  test("test stations count") {
    assert(stations.length == 930)
  }

  test("test temperatures count") {
    assert(temperatures.length == 100000)
  }

  test("test stationTemperatures size") {
    assert(stationTemperatures.size == 98531)
  }

  test("test stationYearlyTemperatures size") {
    assert(stationYearlyTemperatures.size == 281)
  }
}
