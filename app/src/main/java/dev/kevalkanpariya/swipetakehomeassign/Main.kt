package dev.kevalkanpariya.swipetakehomeassign

fun main() {
    saveAllData("My", "Name", "is", "Keval", "Kanpariya")
}

fun saveAllData(vararg data: String) {
    for(item in data) {
        checkValidity(item) {
            println("$item is saved")
        }
    }
}

fun checkValidity(item: String, onValidSuccess: ()-> Unit) {
    if(item.length > 3) {
        println("$item is valid")
        onValidSuccess()


    } else {
        println("$item is valid")
    }
}