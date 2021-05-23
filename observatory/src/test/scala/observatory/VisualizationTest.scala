package observatory

import java.nio.file.Paths

trait VisualizationTest extends UnitSuite {

  test("test near stations distance") {
    val distance = Visualization.distance(Location(42.033, 111.533), Location(41.667, 111.783)).round
    assert(distance == 46)
  }

  test("test same stations distance") {
    val distance = Visualization.distance(Location(42.033, 111.533), Location(42.033, 111.533)).round
    assert(distance == 0)
  }

  test("test antipodes stations distance") {
    val distance = Visualization.distance(Location(42.033, 111.533), Location(-42.033, -68.447)).round
    assert(distance == 20013)
  }

  test("test predictTemperature distance < 1") {
    val t1 = Visualization.predictTemperature(stationYearlyTemperatures, Location(58.933, 6.917)).round
    val t2 = Visualization.predictTemperature(stationYearlyTemperatures, Location(58.928, 6.920)).round
    assert(t1 == 4)
    assert(t1 == t2)
  }

  test("test predictTemperature distance > 1") {
    val t1 = Visualization.predictTemperature(stationYearlyTemperatures, Location(62.928, 6.917)).round
    val t2 = Visualization.predictTemperature(stationYearlyTemperatures, Location(42.033, 121.61)).round
    assert(t1 == 8)
    assert(t2 == 5)
  }

  test("test interpolateColor exact") {
    val color = Visualization.interpolateColor(temperatureColors, 12.0)
    assert(color == Color(255, 255, 0))
  }

  test("test interpolateColor out of range") {
    val colorA = Visualization.interpolateColor(temperatureColors, 91.0)
    val colorB = Visualization.interpolateColor(temperatureColors, -80.0)
    assert(colorA == Color(255, 255, 255))
    assert(colorB == Color(0, 0, 0))
  }

  test("test interpolateColor in range") {
    val colorA = Visualization.interpolateColor(temperatureColors, -2.0)
    val colorB = Visualization.interpolateColor(temperatureColors, 22.0)
    assert(colorA == Color(0, 221, 255))
    assert(colorB == Color(255, 128, 0))
  }

  ignore("visualize") {
    val image = Visualization.visualize(stationYearlyTemperatures, temperatureColors)

    image.output(Paths.get(s"src/test/resources/test-$year-visualized.png"))

    assert(image.width == 360)
    assert(image.height == 180)
  }
}
