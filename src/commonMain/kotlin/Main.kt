class MyClass {
    val myVal: Int = 0
    fun myFun(arg: Int) = Unit
}

@CommonJsExport
fun main() {
    val obj: MyClass? = null

    println("works good:")
    obj?.myFun(obj.myVal)

    println("Fixed in new Kotlin IR to JS compiler")
    obj?.myFun(obj.myVal ?: 0)
}
