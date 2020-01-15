#### Нашёл багу в kotlin JS  

Быстрое воспроизведение бага: https://play.kotlinlang.org/#eyJ2ZXJzaW9uIjoiMS4zLjYxIiwicGxhdGZvcm0iOiJqcyIsImFyZ3MiOiIiLCJqc0NvZGUiOiIiLCJub25lTWFya2VycyI6dHJ1ZSwidGhlbWUiOiJpZGVhIiwiY29kZSI6ImNsYXNzIE15Q2xhc3Mge1xuICAgIHZhbCBteVZhbDogSW50ID0gMFxuICAgIFxuICAgIGZ1biBteUZ1bihub25OdWxsYWJsZUFyZzogQW55KSB7XG4gICAgICAgIFxuICAgIH1cbn1cblxuZnVuIG1haW4oKSB7XG4gICAgdmFsIGNtZDogTXlDbGFzcz8gPSBudWxsXG4gICAgY21kPy5teUZ1bihjbWQubXlWYWwgPzogMClcbiAgICBwcmludGxuKFwiT24gSlMgZXJyb3IgdGhvcndzLiBPbiBKVk0gd29yaydzIGZpbmVcIilcbn0ifQ==
```Kotlin
class MyClass {
    val myVal: Int = 0
    fun myFun(nonNullableArg: Any) {
        
    }
}

fun main() {
    val cmd: MyClass? = null
    cmd?.myFun(cmd.myVal ?: 0)
    println("NPE thorws in JS. On JVM work's fine")
}
```  

Бага проявляется и на более ранних версиях Kotlin (например 1.3.61).
В этом примере использую kotlin 1.3.70-eap42

Для воспроизведения я написал Unit тест.  
Для запуска: ```./gradlew clean jsBrowserTest```

####Попытаемся понять что происходит.
Соберём js код: ```./gradlew compileKotlinJs```  
И смотрим в build/js/packages/js-npe-bug/kotlin/js-npe-bug.js

(Пока не вчитываемся, код не красивый)   
```JavaScript
function MyClass() {
this.myVal = 0;
}
MyClass.prototype.myFun_za3rmp$ = function (nonNullableArg) {
};
function good() {
var tmp$;
var cmd = null;
if (cmd != null) {
  cmd.myFun_za3rmp$((tmp$ = cmd.myVal) != null ? tmp$ : 0);
}}
function bad() {
var tmp$, tmp$_0;
var cmd = null;
tmp$_0 = (tmp$ = cmd.myVal) != null ? tmp$ : 0;
cmd != null ? (cmd.myFun_za3rmp$(tmp$_0), Unit) : null;
}
```

Приведу к более удобному виду:
```JavaScript
function MyClass() {
    this.myVal = 0;
}

MyClass.prototype.myFun = function (nonNullableArg) {
};

function good() {
    var A;
    var cmd = null;
    if (cmd != null) {
      cmd.myFun((A = cmd.myVal) != null ? A : 0);
    }
}

function bad() {
    var A;
    var B;
    var cmd = null;

    // NPE падает тут:
    B = (A = cmd.myVal) != null ? A : 0;

    cmd != null ? (cmd.myFun(B), Unit) : null;
}
```
Надеюсь что получится поправить такую багу...  
