package observatory

trait ManipulationTest extends UnitSuite {

  test("makeGrid") {
    val grid = Manipulation.makeGrid(stationYearlyTemperatures)

    val temperatures = for {
      lat <- -89 to 90
      lon <- -180 to 179
    } yield grid(GridLocation(lat, lon))

    assert(temperatures.size == 360 * 180)
  }

  test("average") {
    val temperatures = Set(Set((Location(11.5, 12.3), 9.1)), Set((Location(-62.3, 52.3), -4.5)), Set((Location(-12.3, 17.8), 1.2)))

    val average = Manipulation.average(temperatures)

    assert(average(GridLocation(0, 0)) == 1.9333333333333333)
  }
}
