# Geometry

MercurialFTC provides a variety of geometry classes that are both useful for performing maths with, and may pop up during usage of the library.&#x20;

Most of the methods on these classes are non-mutating, which means it has no side effects. This also means that the result of the operation needs to be stored in some way.

i.e.:

<pre class="language-java"><code class="lang-java"><strong>Angle myAngle = new AngleDegrees(90);
</strong><strong>myAngle.add(new AngleDegrees(90));
</strong></code></pre>

does not change the value of myAngle, instead:

```java
Angle myAngle = new AngleDegrees(90);
myAngle = myAngle.add(new AngleDegrees(90));
```

will update myAngle to have the result of the operation.
