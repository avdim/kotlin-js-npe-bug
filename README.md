## Fixed configuraion:
New Kotlin IR to JS compiler fix this Bug.
Let's configure in build.gradle.kts:
```Kotlin
tasks.getByName<org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile>("compileKotlinJs") {
    kotlinOptions {
        freeCompilerArgs += listOf("-Xir-produce-js", "-Xgenerate-dts")
    }
}
```  

```Kotlin
//Old code:
fun main() {
    val obj: MyClass? = null
    println("Fixed in new Kotlin IR to JS compiler")
    obj?.myFun(obj.myVal ?: 0)
}
```  

Let's compile JS. ```./gradlew compileKotlinJs.```
Same code now translates to better JS representation with checks to nullability:
```JavaScript
//build/js/packages/js-npe-bug/kotlin/js-npe-bug.js:
var obj = null;
println('Fixed in new Kotlin IR to JS compiler:');
{
  var tmp2_safe_receiver = obj;
  if (tmp2_safe_receiver == null)
    null;
  else {
    var tmp = tmp2_safe_receiver;
    var tmp1_elvis_lhs = obj._get_myVal_();
    tmp.myFun(tmp1_elvis_lhs == null ? 0 : tmp1_elvis_lhs);
    Unit_getInstance();
  }
}
```
  
Now look at Nullability check ```if (tmp2_safe_receiver == null)``` 


**Thank's JetBrains, it work's good!**    
```./gradlew check```  
