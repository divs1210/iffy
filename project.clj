(defproject iffy "0.1.0-SNAPSHOT"
  :description "A Java-compatible Object System in Clojure"
  :url "https://github.com/divs1210/iffy"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]]
  :profiles {:dev {:plugins [[lein-auto "0.1.3"]]}}
  :aliases {"auto-test" ["auto" "do" "clean," "test"]}
  :aot :all)
