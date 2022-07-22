// Stage 1/4: Help page
package svcs

import kotlin.system.exitProcess

fun main(args: Array<String>) {
    //val choice = readln()

    if (args.isEmpty()){
        println(commandHelp())
//    println("""
//        These are SVCS commands:
//        config     Get and set a username.
//        add        Add a file to the index.
//        log        Show commit logs.
//        commit     Save changes.
//        checkout   Restore a file.
//    """.trimIndent())
        exitProcess(0)
    }
    when (args[0]) {
        "--help" -> println(commandHelp())
        "config" -> println(commandConfig())
        "add" -> println(commandAdd())
        "log" -> println(commandLog())
        "commit" -> println(commandCommit())
        "checkout" -> println(commandCheckout())
        //else ->  println("'[passed argument]' is not a SVCS command.")
        else ->  println("'${args[0]}' is not a SVCS command.")
    }
    exitProcess(0)

}

fun commandHelp():String {
    val text = """
        These are SVCS commands:
        config     Get and set a username.
        add        Add a file to the index.
        log        Show commit logs.
        commit     Save changes.
        checkout   Restore a file.
    """.trimIndent()
    return text
}

fun commandConfig():String {
    val text = "Get and set a username."
    return text
}

fun commandAdd():String {
    val text = "Add a file to the index."
    return text
}

fun commandLog():String {
    val text = "Show commit logs."
    return text
}
fun commandCommit():String {
    val text = "Save changes."
    return text
}
fun commandCheckout():String {
    val text = "Restore a file."
    return text
}

/*
These are SVCS commands:
input = readln()
config sets or outputs the name of a commit author;
--help      ---prints the help page;
add         ---adds a file to the list of tracked files or outputs this list;
log         ---shows all commits;
commit      ---saves file changes and the author name;
checkout    ---allows you to switch between commits and restore a previous file state.

 */