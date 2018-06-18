(defproject doit "0.1.0-SNAPSHOT"
  :dependencies [[aero "1.1.3"]
                 [bidi "2.1.3"]
                 [cljsjs/google-platformjs-extern "1.0.0-0"]
                 [http-kit "2.2.0"]
                 [org.clojure/clojure "1.9.0"]
                 [org.clojure/java.jdbc "0.7.6"]
                 [org.clojure/clojurescript "1.9.946"]
                 [org.postgresql/postgresql "42.2.2"]
                 [day8.re-frame/http-fx "0.1.6"]
                 [ragtime "0.7.2"]
                 [re-frame "0.10.5"]
                 [reagent "0.7.0"]
                 [day8.re-frame/test "0.1.5"]]

  :plugins [[lein-cljsbuild "1.1.5"]]

  :test-paths ["test/clj"]

  :min-lein-version "2.5.3"

  :source-paths ["src/clj"]

  :main doit.core

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"
                                    "test/js"]

  :figwheel {:css-dirs ["resources/public/css"]}

  :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}

  :aliases {"dev"   ["do" "clean"
                     ["pdo" ["figwheel" "dev"]]]
            "build" ["with-profile" "+prod,-dev" "do"
                     ["clean"]
                     ["cljsbuild" "once" "min"]]}
  :profiles
  {:dev
   {:dependencies [[binaryage/devtools "0.9.4"]
                   [day8.re-frame/re-frame-10x "0.3.0"]
                   [day8.re-frame/tracing "0.5.0"]
                   [figwheel-sidecar "0.5.13"]
                   [com.cemerick/piggieback "0.2.2"]]

    :plugins [[lein-figwheel "0.5.13"]
              [lein-doo "0.1.8"]
              [lein-pdo "0.1.1"]]}
   :prod { :dependencies [[day8.re-frame/tracing-stubs "0.5.0"]]}}

  :cljsbuild
  {:builds
   [{:id           "dev"
     :source-paths ["src/cljs"]
     :figwheel     {:on-jsload "doit.core/mount-root"}
     :compiler     {:main                 doit.core
                    :output-to            "resources/public/js/compiled/app.js"
                    :output-dir           "resources/public/js/compiled/out"
                    :asset-path           "js/compiled/out"
                    :source-map-timestamp true
                    :preloads             [devtools.preload
                                           day8.re-frame-10x.preload]
                    :closure-defines      {"re_frame.trace.trace_enabled_QMARK_"        true
                                           "day8.re_frame.tracing.trace_enabled_QMARK_" true}
                    :external-config      {:devtools/config {:features-to-install :all}}
                    }}

    {:id           "min"
     :source-paths ["src/cljs"]
     :compiler     {:main            doit.core
                    :output-to       "resources/public/js/compiled/app.js"
                    :optimizations   :advanced
                    :parallel-build  :true
                    :closure-defines {goog.DEBUG false}
                    :pretty-print    false}}

    {:id           "test"
     :source-paths ["src/cljs" "test/cljs"]
     :compiler     {:main          doit.runner
                    :output-to     "resources/public/js/compiled/test.js"
                    :output-dir    "resources/public/js/compiled/test/out"
                    :optimizations :none}}
    ]}

  )
