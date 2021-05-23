package observatory

import observatory.spark.SparkApp
import org.apache.spark.HashPartitioner
import org.apache.spark.rdd.RDD

import java.time.LocalDate
import scala.io.Source

/**
  * 1st milestone: data extraction
  */
object Extraction extends ExtractionInterface with SparkApp {

  /**
    * @param year             Year number
    * @param stationsFile     Path of the stations resource file to use (e.g. "/stations.csv")
    * @param temperaturesFile Path of the temperatures resource file to use (e.g. "/1975.csv")
    * @return A sequence containing triplets (date, location, temperature)
    */
  def locateTemperatures(year: Year, stationsFile: String, temperaturesFile: String): Iterable[(LocalDate, Location, Temperature)] = {
    sparkTemperatures(rawStations(stationsFile), rawTemperatures(year, temperaturesFile)).collect().toSeq
  }

  /**
    * @param records A sequence containing triplets (date, location, temperature)
    * @return A sequence containing, for each location, the average temperature over the year.
    */
  def locationYearlyAverageRecords(records: Iterable[(LocalDate, Location, Temperature)]): Iterable[(Location, Temperature)] = {
    sparkYearlyAverage(sc.parallelize(records.toSeq)).collect().toSeq
  }

  private def sparkYearlyAverage(records: RDD[(LocalDate, Location, Temperature)]): RDD[(Location, Temperature)] = {
    records.map(p => (p._2, p._3))
      .mapValues(temp => (temp, 1))
      .reduceByKey {
        case ((tempL, countL), (tempR, countR)) => (tempL + tempR, countL + countR)
      }
      .mapValues {
        case (total, count) => total / count
      }
      .persist()
  }

  private def sparkTemperatures(stations: RDD[(SID, Station)], temperatures: RDD[(SID, Temp)]): RDD[(LocalDate, Location, Temperature)] = {
    val partitioner = new HashPartitioner(6)
    stations.partitionBy(partitioner).persist()
    temperatures.partitionBy(partitioner).persist()

    stations.join(temperatures)
      .groupByKey()
      .flatMap(_._2)
      .map {
        case (station, temp) => (temp.date, station.location, temp.temp)
      }
      .persist()
  }

  def rawStations(stationsFile: String): RDD[(SID, Station)] = {
    val stations = sc.parallelize(asRecords(stationsFile))
      .map(line => line.split(","))
      .filter(arr => arr.length == 4)
      .map(arr => {
        Station(
          id = arr(0) + "#" + arr(1),
          location = Location(lat = arr(2).toDouble, lon = arr(3).toDouble)
        )
      })
      .filter(s => s.location.lon != 0.0 && s.location.lat != 0.0)
    stations.map(s => (s.id, s))
  }

  def rawTemperatures(year: Year, temperaturesFile: String): RDD[(SID, Temp)] = {
    val temperatures = sc.parallelize(asRecords(temperaturesFile))
      .map(line => {
        val arr = line.split(",")
        Temp(
          id = arr(0) + "#" + arr(1),
          date = LocalDate.of(year, arr(2).toInt, arr(3).toInt),
          temp = (arr(4).toDouble - 32) / 1.8
        )
      })
    temperatures.map(t => (t.id, t))
  }

  def asRecords(path: String): Seq[String] =
    Source.fromInputStream(getClass.getResourceAsStream(path), "utf-8").getLines().toSeq
}
