# iffy

A Java-compatible Object System in Clojure

## Usage

**Note:** *All examples are from [examples.clj](./src/iffy/examples.clj).*

Let's define a simple class:
```clojure
(defclass Stack []
  (meth push [x]
    (.update this :stack #(cons x %)))

  (meth pop []
    (let [x (first (:stack this))]
      (.update this :stack rest)
      x))

  (meth peek []
    (first (:stack this))))
```
This creates a class called `Stack` that:
- Holds some state in its public `state` field, which is a Clojure `atom`
- Accesses and mutates that state
  - `this` and `state` are passed automatically to every method
  - generated classes support keyword lookup (`(:stack this)`)

We can also extend other classes and/or implement interfaces:
```clojure
(defclass SmartList [java.util.ArrayList]
  (ctors [[] [int]])

  (over get [idx]
    (.superGet this (mod idx (.size this))))

  (meth map [f]
    (dotimes [i (.size this)]
      (let [curr-val (.get this i)
            new-val (f curr-val)]
        (.set this i new-val)))))
```
This creates a class called `SmartList` that:
- extends `ArrayList`
- exposes two constructors from the base class: [`()`](https://docs.oracle.com/javase/8/docs/api/java/util/ArrayList.html#ArrayList--) and [`(int initialCapacity)`](https://docs.oracle.com/javase/8/docs/api/java/util/ArrayList.html#ArrayList-int-)
- overrides `.get` so it can handle negative indices - notice the auto-generated `.superGet` method
- adds `.map` method, which mutates the state inherited from the base class

Let's see how we can use this class:
```clojure
> (def aseq (iffy.examples.SmartList.))
;; => #'user/aseq

> (.addAll aseq (range 10))
;; => true

> aseq
;; => [0 1 2 3 4 5 6 7 8 9]

> (.get aseq -1)
;; => 9

> (.map aseq inc)
;; => nil

> aseq
;; => [1 2 3 4 5 6 7 8 9 10]
```

## License

Copyright © 2018 Divyansh Prakash

Distributed under the Eclipse Public License version 1.0.
