package observatory

import com.sksamuel.scrimage.Image

import scala.annotation.tailrec

/**
  * 2nd milestone: basic visualization
  */
//noinspection SameParameterValue
object Visualization extends VisualizationInterface {

  /**
    * @param temperatures Known temperatures: pairs containing a location and the temperature at this location
    * @param location     Location where to predict the temperature
    * @return The predicted temperature at `location`
    */
  def predictTemperature(temperatures: Iterable[(Location, Temperature)], location: Location): Temperature = {
    val distanceTemperatures = temperatures.map(p => (distance(p._1, location), p._2))

    distanceTemperatures.find(_._1 <= 1) match {
      case Some((_, temperature)) => temperature
      case _ => predictTemperature(distanceTemperatures, 3)
    }
  }

  /**
    * @param points      Pairs containing a value and its associated color
    * @param temperature The value to interpolate
    * @return The color that corresponds to `value`, according to the color scale defined by `points`
    */
  def interpolateColor(points: Iterable[Point], temperature: Temperature): Color = {
    points.find(_._1 == temperature) match {
      case Some((_, color)) => color
      case _ => thresholds(points.toList.sortBy(_._1), temperature, Option.empty) match {
        case (pointMin, None) => pointMin.get._2
        case (None, pointMax) => pointMax.get._2
        case (pointMin, pointMax) => interpolate(pointMin.get, pointMax.get, temperature)
      }
    }
  }

  /**
    * @param temperatures Known temperatures
    * @param colors       Color scale
    * @return A 360×180 image where each pixel shows the predicted temperature at its location
    */
  def visualize(temperatures: Iterable[(Location, Temperature)], colors: Iterable[Point]): Image = {
    val width = 360
    val height = 180

    val pixels = (0 until width * height).par
      .map(pos => toLocation(pos))
      .map(location => predictTemperature(temperatures, location))
      .map(temperature => interpolateColor(colors, temperature))
      .map(color => color.toPixel)
      .toArray

    Image(width, height, pixels)
  }

  /**
    * @param pos image pixel in array -> y * width + x
    * @return Location
    */
  private def toLocation(pos: Int): Location = {
    val x = pos % 360
    val y = pos / 360

    Location(90 - y, x - 180)
  }

  private def predictTemperature(temperatures: Iterable[(Double, Temperature)], power: Int): Double = {
    val (ws, norm) = temperatures.aggregate((0.0, 0.0))(
      {
        case ((ws, norm), (dist, temp)) =>
          val w = 1 / Math.pow(dist, power)
          (ws + w * temp, norm + w)
      },
      {
        case ((ws1, norm1), (ws2, norm2)) => (ws1 + ws2, norm1 + norm2)
      }
    )
    ws / norm
  }

  def distance(l1: Location, l2: Location): Double = {
    def x1 = Math.toRadians(l1.lat)

    def y1 = Math.toRadians(l1.lon)

    def x2 = Math.toRadians(l2.lat)

    def y2 = Math.toRadians(l2.lon)

    def R = 6371

    def centralAngle = Math.acos(Math.sin(x1) * Math.sin(x2) + Math.cos(x1) * Math.cos(x2) * Math.cos(y2 - y1))

    centralAngle * R
  }

  @tailrec
  private def thresholds(points: List[Point], temperature: Temperature, lowerBound: Option[Point]): (Option[Point], Option[Point]) = {
    if (points.isEmpty)
      (lowerBound, Option.empty)
    else {
      if (temperature < points.head._1) (lowerBound, Option(points.head))
      else thresholds(points.tail, temperature, Option(points.head))
    }
  }

  private def interpolate(pointMin: Point, pointMax: Point, temperature: Temperature): Color = {
    val in = interpolate(pointMin._1, pointMax._1, temperature) _
    Color(
      in(pointMin._2.red, pointMax._2.red),
      in(pointMin._2.green, pointMax._2.green),
      in(pointMin._2.blue, pointMax._2.blue)
    )
  }

  private def interpolate(minTemp: Temperature, maxTemp: Temperature, temperature: Temperature)(minColor: Int, maxColor: Int): Int = {
    Math.round(minColor + ((maxColor - minColor) * (temperature - minTemp) / (maxTemp - minTemp))).toInt
  }

}

