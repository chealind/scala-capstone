package observatory.spark

import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession

trait SparkApp {

  Logger.getLogger("org.apache.spark").setLevel(Level.WARN)

  implicit val spark: SparkSession = SparkSession.builder()
    .appName("Observatory")
    .master("local")
    .getOrCreate()

  implicit val sc: SparkContext = spark.sparkContext
}
