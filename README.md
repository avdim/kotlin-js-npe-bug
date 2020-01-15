### Reproduce bug on JS
**JVM work's fine**  
Kotlin-JS **1.3.70-eap42** and **1.3.61** (maybe early versions too, not test)  
```Kotlin
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
```
To reproduce run unit test ```./gradlew check```  

### Попытаемся понять что происходит
```Kotlin
println("works good:")
obj?.myFun(obj.myVal)

println("throws NPE on JS:")
obj?.myFun(obj.myVal ?: 0)
```
Т.е. разница только в " obj.myVal **?: 0** "  
Так что же такое добавляет **?: 0**, что падает NPE ?

### Посмотрим на сгенерированный JS код:
Соберём js код: ```./gradlew compileKotlinJs```  
И смотрим в build/js/packages/js-npe-bug/kotlin/js-npe-bug.js

(Пока не вчитываемся, код не красивый)   
```JavaScript
function MyClass() {
    this.myVal = 0;
}
MyClass.prototype.myFun_za3lpa$ = function (arg) {  
};
var tmp$, tmp$_0;
var obj = null;
//println('works good:');
obj != null ? (obj.myFun_za3lpa$(obj.myVal), Unit) : null;
//println('throws NPE on JS:');
tmp$_0 = (tmp$ = obj.myVal) != null ? tmp$ : 0;
obj != null ? (obj.myFun_za3lpa$(tmp$_0), Unit) : null;
```

Приведу к более удобному виду:
```JavaScript
function MyClass() {
    this.myVal = 0;
}
MyClass.prototype.myFun = function (arg) {
  
};

var obj = null;

console.log('works good:');
obj != null ? (obj.myFun(obj.myVal), Unit) : null; // Эта строка хорошая

console.log('throws NPE on JS:');
var tmp1, tmp2;
tmp2 = (tmp1 = obj.myVal) != null ? tmp1 : 0; // Эта строка выбрасыват NPE (obj.myVal, где obj == null)
obj != null ? (obj.myFun(tmp2), Unit) : null; // Эта строка почти не поменялась (тоже хорошая)
```
Понятно... Значит obj предполагается не null. Этот код не прошёл проверку obj != null ? (...)  
Попробуем прикинуть как должно было быть:  
```JavaScript
var tmp1, tmp2;
obj != null ? (
    tmp2 = (tmp1 = obj.myVal) != null ? tmp1 : 0, // засунули внутрь
    obj.myFun(tmp2), Unit // остался старый кусок
) : null;
``` 
Выглядит страшно, но работает.

Так понимаю что JetBrains переписывают компилятор и может эта проблема решится с IR и новым js backend-ом.  
