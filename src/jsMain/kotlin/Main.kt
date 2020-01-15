class MyClass {
    val myVal: Int = 0
    fun myFun(arg: Int) = Unit
}

fun good() {
    // Вначале у меня был такой код и он работает хорошо.
    val obj: MyClass? = null
    if (obj != null) { // Тут Intellij IDEA предлагает упростить код ("quick fix")
        obj.myFun(obj.myVal ?: 0)
    }
}

// Применяем "quick fix" и получаем такой код:
fun bad() {
    // Это работает плохо. Вылетает NPE при обращении к myVal
    val obj: MyClass? = null
    obj?.myFun(obj.myVal ?: 0)
}
