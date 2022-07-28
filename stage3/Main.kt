// Stage 3/4: Log commit
// need to revisit... want to introduce some Lambdas to this program, and pass around some objects
package svcs

import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import kotlin.system.exitProcess

const val VCS_DIRECTORY = "vcs"
const val COMMIT_DIRECTORY = "vcs\\commits\\"
const val CONFIG_FILE = "vcs\\config.txt"
const val INDEX_FILE = "vcs\\index.txt"
const val LOG_FILE = "vcs\\log.txt"

class Vsc (args: Array<String>) {
    val configFile:File = File(CONFIG_FILE)
    val indexFile:File = File(INDEX_FILE)
    val logFile:File = File(LOG_FILE)
    val vcsDir:File = File(VCS_DIRECTORY)
    val commitDir:File = File(COMMIT_DIRECTORY)

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
        if (args.isNotEmpty()) {
            command = args[0]
            if (args.size == 2) {
                param = args[1]
            }
        }

        /*
         * Create VCS Structure
         */
        vcsDir.mkdir()
        commitDir.mkdir()
        configFile.createNewFile()
        indexFile.createNewFile()
        logFile.createNewFile()
    }

    fun deleteAll() {
        configFile.delete()
        indexFile.delete()
        logFile.delete()
        commitDir.deleteRecursively()
        vcsDir.delete()
    }

    fun run() {
        if (command.isEmpty()){
            println(commandHelp())
            exitProcess(0)
        }

        (when (command) {
            "--help" -> println(commandHelp())
            "config" -> println(commandConfig(param))
            "add" -> println(commandAdd(param))
            "log" -> commandLog()
            "commit" -> commandCommit()
            "checkout" -> println(commandCheckout())
            else ->  println("'${command}' is not a SVCS command.")
        })
    }

    fun commandHelp():String = """
        These are SVCS commands:
        config     ${menu["config"].toString()}
        add        ${menu["add"].toString()}
        log        ${menu["log"].toString()}
        commit     ${menu["commit"].toString()}
        checkout   ${menu["checkout"].toString()}
    """.trimIndent()

    /**
     * commitId is made up of the concat filename and text in file for all files in indexFiles.
     * and then hashed
     */
    fun getCommitId(): String {
        var textContent = ""
        val indexItems = indexFile.readLines()
        for (fileName in indexItems) {
            textContent += File(fileName).readText()
            textContent += fileName
        }

        return textContent.md5()
    }

    fun String.md5(): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
    }

    fun String.sha256(): String {
        val md = MessageDigest.getInstance("SHA-256")
        return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
    }

    fun commandConfig(option: String): String {
        if (option == "") {
            val name = configFile.readText()
            return if (name.isEmpty()) "Please, tell me who you are." else "The username is $name."
        } else {
            configFile.writeText(option)
            return "The username is ${option}."
        }
    }

    fun getUser(): String {
        val name = configFile.readText()
        if (name.isEmpty()) {
            error("??????")
        } else {
            return name
        }
    }

    fun commandAdd(param: String): String = if (param == "") showIndexFiles() else addFiles(param)

    /**
     * Read log File and print
     */
    fun commandLog() {
        val logLines = logFile.readText().trim()
        if (logLines.isEmpty()) {
            println("No commits yet.")
        } else {
            println("$logLines\n")
        }
    }

    fun commandCommit() {
        if (param == "") {
            println("Message was not passed.")
        } else {
            val trackedFiles = indexFile.readLines()
            if (trackedFiles.isEmpty()) {
                println("Nothing to commiwwwt.")
            } else {

                val foundChanges = checkForChanges()
                if (foundChanges) {
                    doCommit()
                    println("Changes are committed.")
                } else {
                    println("Nothing to commit.")
                }
            }
        }
    }

    fun doCommit() {
        val commitId = getCommitId()

        /* Create Commit Folder */
        File(COMMIT_DIRECTORY + commitId).mkdir()
        val myFile = File(COMMIT_DIRECTORY + commitId)
        myFile.createNewFile()

        /* Copy Files and Log */
        for (fileName in indexFile.readLines()) {
            val fileIn = File(fileName)
            val fileOut = File(COMMIT_DIRECTORY + "$commitId\\$fileName")
            fileIn.copyTo(fileOut, overwrite = true)
        }
        val buffer = logFile.readText() // Purpose of buffer it is, so we can have the latest log on top

        logFile.writeText("commit $commitId\n")
        logFile.appendText("Author: "+getUser()+"\n")
        logFile.appendText(param+"\n")
        logFile.appendText(buffer)
    }

    fun commandCheckout():String = menu["checkout"].toString()

    fun showIndexFiles(): String {
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

    fun checkForChanges() : Boolean {
        val commitDirIds = commitDir.listFiles()!!.map{it.name}
        if (commitDirIds.isEmpty()){
            return true
        } else {
            val commitId = getCommitId()

            if (!commitDirIds.contains(commitId)){
                return true
            }
        }
        return false
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
    // Test Cases on my PC
    //var args = arrayOf("xxx")
    //args = arrayOf("config") // Please, tell me who you are.
    //args = arrayOf("config", "max") // The username is max.
    //args = arrayOf("commit", "nothing yet") // Nothing to commit.
    //args = arrayOf("add")
    //args = arrayOf("add", "file1.txt") // The file 'file1.txt' is tracked.
    //args = arrayOf("add") //Tracked files:\n file1.txt
    //args = arrayOf("add", "file2.txt") // //Tracked files:\n file2.txt
    //args = arrayOf("add") //Tracked files:\n file1.txt\n file2.txt
    //args = arrayOf("commit") // Message was not passed.
    //args = arrayOf("log") // No commits yet.
    //args = arrayOf("commit", "Initial Check in, changed files") //
    //args = arrayOf("log") // See below note #1
    //args = arrayOf("commit", "Changed file1 a bit") //
    //args = arrayOf("commit", "Should not pass")
    //args = arrayOf("log") // See below note #1

    val vcsObj = Vsc(args)
        vcsObj.run()
} // 256