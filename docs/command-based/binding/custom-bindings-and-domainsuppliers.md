# Custom Bindings and DomainSuppliers

Custom Bindings and DomainSuppliers can easily be made, as each simply takes in its implemented supplier, and self-registers against the scheduler.

```java
Binding<?> myBinding = new Binding<>(<BooleanSupplier>);
```

```java
DomainSupplier myDomainSupplier = new DomainSupplier(<DoubleSupplier>);
```

This can be useful for turning off driver input in a Subsystem that takes in DomainSuppliers to control its motion with DomainSuppliers that always return 0&#x20;

```java
DomainSupplier myDomainSupplier = new DomainSupplier(() -> 0);
```
