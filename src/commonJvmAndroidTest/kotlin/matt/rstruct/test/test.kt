package matt.rstruct.test


import matt.rstruct.appNameFileRelativeToSourceSet
import matt.rstruct.changelistFileRelativeToSourceSet
import matt.rstruct.modIDFileRelativeToSourceSet
import matt.rstruct.valuesFileRelativeToSourceSet
import matt.test.assertions.JupiterTestAssertions.assertRunsInOneMinute
import kotlin.test.Test

class RstructTests {
    @Test
    fun initValues() =
        assertRunsInOneMinute {
            appNameFileRelativeToSourceSet
            modIDFileRelativeToSourceSet
            changelistFileRelativeToSourceSet
            valuesFileRelativeToSourceSet
        }
}
