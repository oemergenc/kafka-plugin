package kafkaplugin

import net.jpountz.lz4.LZ4BlockInputStream
import org.apache.spark.sql.Encoders
import org.apache.spark.sql.RowFactory

class ComponentTest extends AbstractComponentSpec {

//    def "correct is correct"() {
//        given:
//        def list = [1, 2, 3, 4]
//
//        when:
//        def rdd = jsc().parallelize(list)
//
//        then:
//        rdd.count() == list.size()
//    }
//
//    def "correct is correct2"() {
//        given:
//        def list = [1, 2, 3, 4].collect { RowFactory.create(it) }
//
//        when:
//        def dataFrame = spark().createDataFrame(list, Encoders.INT().schema())
//        dataFrame.createOrReplaceTempView("frame")
//        spark().sql("SELECT value as theInt FROM frame").show()
//
//        then:
//        dataFrame.count() == list.size()
//    }

    def "correct is correct3"() {
        given:
        def list = [1, 2, 3, 4].collect { RowFactory.create(it) }
        println(LZ4BlockInputStream.class.getProtectionDomain().getCodeSource().getLocation().toString())

        when:
        def dataFrame = spark().createDataFrame(list, Encoders.INT().schema())
        dataFrame.createOrReplaceTempView("frame")
        spark().sql("SELECT value as theSecondInt FROM frame").show()

        then:
        dataFrame.count() == list.size()
    }
}
