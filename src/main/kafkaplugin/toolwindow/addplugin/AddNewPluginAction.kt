package kafkaplugin.toolwindow.addplugin

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.DumbAware
import kafkaplugin.Icons
import org.apache.spark.SparkConf
import org.apache.spark.sql.Encoders
import org.apache.spark.sql.Row
import org.apache.spark.sql.RowFactory
import org.apache.spark.sql.SparkSession
import java.util.function.Consumer

class AddNewGroovyPluginAction : AddNewPluginAction(
        text = "Groovy Plugin",
        description = "Create new Groovy plugin"
)

open class AddNewPluginAction(
        text: String,
        description: String
) : AnAction(text, description, Icons.newPluginIcon), DumbAware {

    private val log = Logger.getInstance(AddNewPluginAction::class.java)
    private val addNewPluginTitle = "Add $text"

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project
        println("This is a message $project")
        Thread.currentThread().contextClassLoader = org.apache.log4j.ConsoleAppender::class.java.classLoader
        val resourceStream = Thread.currentThread().contextClassLoader.getResourceAsStream("spark-version-info.properties")
        if (resourceStream != null) {
            println("Found the shizzle")
        } else {
            println("Did not found the shizzle")
        }

        val sparkConf = SparkConf()
                .setMaster("local[*]")
                .set("spark.sql.crossJoin.enabled", "true")
                .setAppName("RecipeContentAggregatorStructuredStreamApplication")

        val sparkSession = SparkSession.builder()
                .config(sparkConf)
                .orCreate
        sparkSession.sparkContext()
                .setLogLevel("WARN")


        val rowList = mutableListOf(RowFactory.create(1), RowFactory.create(2), RowFactory.create(3))

        val dataFrame = sparkSession.createDataFrame(rowList, Encoders.INT().schema())
        dataFrame.createOrReplaceTempView("frame")
        val dataset = sparkSession.sql("SELECT value as theSecondInt FROM frame")
        dataset.show()
        dataset.collectAsList().forEach(Consumer<Row> { println(it) })
    }
}

