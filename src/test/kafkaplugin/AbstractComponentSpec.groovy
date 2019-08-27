package kafkaplugin

import com.holdenkarau.spark.testing.JavaDatasetSuiteBase
import kafkaplugin.AbstractComponentSpec.JavaDatasetSuiteBaseTrait
import org.apache.spark.SparkContext
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.sql.SparkSession
import org.junit.After
import org.junit.Before
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

abstract class AbstractComponentSpec extends Specification implements JavaDatasetSuiteBaseTrait {

    protected PollingConditions pollingConditions = new PollingConditions(timeout: 10)


    def setupSpec() {

    }

    def setup() {

    }

    def clean() {

    }

    def cleanupSpec() {

    }

    trait JavaDatasetSuiteBaseTrait {
        JavaDatasetSuiteBase datasetSuiteBase = new JavaDatasetSuiteBase()

        @Before
        def setupUserSpec() {
            datasetSuiteBase.runBefore()
        }

        @After
        def cleanupUserSpec() {
            datasetSuiteBase.runAfterClass()
        }

        SparkSession spark() {
            return datasetSuiteBase.spark()
        }

        SparkContext sc() {
            return datasetSuiteBase.sc()
        }

        JavaSparkContext jsc() {
            return datasetSuiteBase.jsc()
        }
    }
}
