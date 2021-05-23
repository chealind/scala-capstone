package observatory

import java.nio.file.Paths

trait Visualization2Test extends UnitSuite {

  test("bilinearInterpolation") {
    val cellPoint = CellPoint(0.5, 0.5)

    val temperature = Visualization2.bilinearInterpolation(cellPoint, 4.1, -1.4, 5.2, 9.1)

    assert(temperature == 4.25)
  }

  test("visualizeGrid") {
    val grid = Manipulation.makeGrid(stationYearlyTemperatures)

    val image = Visualization2.visualizeGrid(grid, temperatureColors, Tile(1, 3, 2))

    image.output(Paths.get(s"src/test/resources/test-grid-$year-2-1-3-tile.png"))

    assert(image.width == 256)
    assert(image.height == 256)
  }
}
