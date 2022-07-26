// Stage 2/4: Add config
// need to revisit... want to introduce some Lambdas to this programs, and pass around some objects
package svcs

import java.io.File
import kotlin.system.exitProcess

const val VCS_DIRECTORY = "vcs"
const val CONFIG_FILE = "vcs/config.txt"
const val INDEX_FILE = "vcs/index.txt"

class Vsc (args: Array<String>) {
    val argsxx = args.toMutableList()
    var debugMode = false // this is so I can run in a loop and to manually be able to input read line
    val configFile:File = File(CONFIG_FILE)
    val indexFile:File = File(INDEX_FILE)

    var command = ""
    var param = ""

    val menu = mapOf(
        "config" to "Get and set a username.",
        "add" to "Add a file to the index.",
        "log" to "Show commit logs.",
        "commit" to "Save changes.",
        "checkout" to "Restore a file.",
    )

    init {
        if (!args.isEmpty()) {
            command = args[0]
            if (args.size == 2) {
                param = args[1]
            }
        }

        File(VCS_DIRECTORY).mkdir()
        configFile.createNewFile()
        indexFile.createNewFile()
    }

    fun run() {
        do {
            if (debugMode) {
                println("Enter something dang it!!!!")
                val choice = readln().split(" ").toMutableList()

                if (!choice.isEmpty()) {
                    command = choice[0]
                    if (choice.size == 2) param = choice[1]
                }
            }

            if (command.isEmpty()){
                println(commandHelp())
                exitProcess(0)
            }

            (when (command) {
                "--help" -> println(commandHelp())
                "config" -> println(commandConfig(param))
                "add" -> println(commandAdd(param))
                "log" -> println(commandLog())
                "commit" -> println(commandCommit())
                "checkout" -> println(commandCheckout())
                else ->  println("'${command}' is not a SVCS command.")
            })

            if(debugMode) argsxx.clear()
        } while (debugMode)
        exitProcess(0)
    }

    fun commandHelp():String = """
        These are SVCS commands:
        config     ${menu["config"].toString()}
        add        ${menu["add"].toString()}
        log        ${menu["log"].toString()}
        commit     ${menu["commit"].toString()}
        checkout   ${menu["checkout"].toString()}
    """.trimIndent()

    fun commandConfig(option: String): String {
        if (option == "") {
            val name = configFile.readText()
            if (name.isEmpty()) {
                return "Please, tell me who you are."
            } else {
                return "The username is $name."
            }
        } else {
            configFile.writeText(option)

            return "The username is ${option}."
        }
    }

    fun commandAdd(param: String): String = if (param == "") showFiles() else addFiles(param)

    fun commandLog():String = menu["log"].toString()

    fun commandCommit():String = menu["commit"].toString()

    fun commandCheckout():String = menu["checkout"].toString()

    fun showFiles(): String {
        val nameList = indexFile.readLines()
        return if (nameList.isEmpty()) {
            menu["add"].toString()
        } else {
            var tmpStr = "Tracked files:"
            for (name in nameList){
                tmpStr += "\n$name"
            }
            tmpStr
        }
    }

    fun addFiles(fileName: String): String {
        val addFile = File(fileName)

        return if (addFile.exists()){
            val nameList = indexFile.readLines()
            if (!nameList.contains(fileName)) {
                if (nameList.isEmpty()) {
                    indexFile.appendText(fileName)
                } else {
                    indexFile.appendText("\n" + fileName)
                }
            }
            "The file '${fileName}' is tracked."
        } else {
            "Can't find '${fileName}'."
        }
    }
}

fun main(args: Array<String>) {
    val vcsObj = Vsc(args)
    vcsObj.run()
}
// 142 134 153 171