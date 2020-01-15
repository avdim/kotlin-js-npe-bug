class MyClass {
    val myVal: Int = 0
    fun myFun(arg: Int) = Unit
}
fun main() {
    val obj: MyClass? = null

    println("works good:")
    obj?.myFun(obj.myVal)

    println("throws NPE on JS:")
    obj?.myFun(obj.myVal ?: 0)
}
